package at.outdated.bitcoin.exchange.kraken;

import at.outdated.bitcoin.exchange.api.ExchangeApiClient;
import at.outdated.bitcoin.exchange.api.Market;
import at.outdated.bitcoin.exchange.api.account.AccountInfo;
import at.outdated.bitcoin.exchange.api.account.TransactionType;
import at.outdated.bitcoin.exchange.api.account.Wallet;
import at.outdated.bitcoin.exchange.api.account.WalletTransaction;
import at.outdated.bitcoin.exchange.api.currency.Currency;
import at.outdated.bitcoin.exchange.api.currency.CurrencyValue;
import at.outdated.bitcoin.exchange.api.jaxb.JSONResolver;
import at.outdated.bitcoin.exchange.api.market.*;
import at.outdated.bitcoin.exchange.kraken.jaxb.*;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Hex;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.json.JsonObject;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.MediaType;
import java.security.MessageDigest;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Future;


/**
 * Created with IntelliJ IDEA.
 * User: ebirn
 * Date: 17.09.13
 * Time: 18:23
 * To change this template use File | Settings | File Templates.
 */
public class KrakenClient extends ExchangeApiClient {

    public KrakenClient(Market market) {
        super(market);
    }

    @Override
    public AccountInfo getAccountInfo() {

        KrakenAccountInfo accountInfo = new KrakenAccountInfo();


        WebTarget tradeHistoryTgt = client.target("https://api.kraken.com/0/private/TradesHistory");
        String rawTradeHistory = syncRequest(tradeHistoryTgt, String.class, "POST", Entity.form(new Form()), true);
        log.info("tradeHistory: {}", rawTradeHistory);

        JsonObject jsonTrades = jsonFromString(rawTradeHistory).getJsonObject("result").getJsonObject("trades");
        if(jsonTrades != null) {
            for(String tradeKey : jsonTrades.keySet()) {
                // TODO finish this
                jsonTrades.getJsonObject(tradeKey);
                log.debug("TRADE: {}", tradeKey);
            }
        }

        WebTarget ledgerTgt = client.target("https://api.kraken.com/0/private/Ledgers");
        String rawLedger = syncRequest(ledgerTgt, String.class, "POST", Entity.form(new Form()), true);
        log.info("ledger: {}", rawLedger);
        JsonObject jsonLedger = jsonFromString(rawLedger).getJsonObject("result").getJsonObject("ledger");
        if(jsonLedger != null) parseLedger(accountInfo, jsonLedger);



        WebTarget balanceTgt = client.target("https://api.kraken.com/0/private/Balance");
        String rawBalance = syncRequest(balanceTgt, String.class, "POST", Entity.form(new Form()), true);
        log.info("balance: {}", rawBalance);

        JsonObject balances = jsonFromString(rawBalance).getJsonObject("result");
        if(balances != null)
        for(String currKey : balances.keySet()) {
            Currency curr = parseCurrency(currKey);
            accountInfo.getWallet(curr).setBalance(new CurrencyValue(Double.parseDouble(balances.getString(currKey)), curr));
        }

        WebTarget feeTgt = client.target("https://api.kraken.com/0/public/AssetPairs?info=fees");

        return accountInfo;
    }

    @Override
    public TickerValue getTicker(Currency currency) {

        WebTarget webResource = client.target("https://api.kraken.com/0/public/Ticker?pair=" + fixSymbol(Currency.BTC) + fixSymbol(currency));
        //KrakenTickerResponse tickerResponse =

       KrakenTickerResponse response = simpleGetRequest(webResource, KrakenTickerResponse.class);
       // String tickerRaw = simpleGetRequest(webResource, String.class);
        // log.debug("ticker raw: {}", tickerRaw);

        TickerValue value = response.getResult().getXXBTZEUR().getValue();
        value.setCurrency(currency);

        return value;
    }

    @Override
    public Number getLag() {
        return 1;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public MarketDepth getMarketDepth(Currency base, Currency quote) {

        WebTarget webResource = client.target("https://api.kraken.com/0/public/Depth?pair=" + fixSymbol(base) + fixSymbol(quote));
        String rawDepth = simpleGetRequest(webResource, String.class);

        log.debug("raw depth: {}", rawDepth);


        JsonObject jsonDepth = jsonFromString(rawDepth).getJsonObject("result").getJsonObject("X"+fixSymbol(base)+"Z"+fixSymbol(quote));

        double asks[][] = parseNestedArray(jsonDepth.getJsonArray("asks"));
        double bids[][] = parseNestedArray(jsonDepth.getJsonArray("bids"));


        MarketDepth depth = new MarketDepth();
        addOrders(TradeDecision.SELL, asks, depth.getAsks(), base, quote);
        addOrders(TradeDecision.BUY, bids, depth.getBids(), base, quote);

        depth.setBaseCurrency(base);

        return depth;
    }

    @Override
    protected <T> Invocation.Builder setupProtectedResource(WebTarget tgt, Entity<T> entity) {
        // see https://www.kraken.com/help/api
        // headers:
        // API-Key = API key
        // API-Sign = Message signature using HMAC-SHA512 of (URI path + SHA256(nonce + POST data)) and base64 decoded secret API key

        long nonce = ((new Date()).getTime() * 1000L);
        Invocation.Builder builder = null;

        try {
            Mac mac = Mac.getInstance("HmacSHA512");
            SecretKeySpec secret_spec = new SecretKeySpec(Base64.decodeBase64(getSecret()), "HmacSHA512");
            mac.init(secret_spec);

            Form form = ((Entity<Form>) entity).getEntity();
            form.param("nonce", Long.toString(nonce));
            // path + NUL + POST (incl. nonce)

            String formStr = formData2String(form);
            String signatureData = Long.toString(nonce) + formStr;

            log.debug("payload: {}", formStr);
            log.debug("sign payload: {}", signatureData);

            String path = tgt.getUri().getPath();
            log.debug("path: {}", path);


            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            digest.update(signatureData.getBytes());

            mac.update(path.getBytes("UTF-8"));

            byte[] rawSignature = mac.doFinal(digest.digest());

            // need exactly this function, otherwise might add linebreaks after 76 characters
            String signature = new String(Base64.encodeBase64(rawSignature, false));
            log.debug("signature: {}", signature);

            // POST data:
            // nonce = always increasing unsigned 64 bit integer

            String key = getUserId();
            log.debug("key: {}", key);

            builder = tgt.request();
            builder.header("API-Key", key);
            builder.header("API-Sign", signature);

        }
        catch(Exception e) {
            log.error("setup protected request", e);
        }

        return builder;  //To change body of implemented methods use File | Settings | File Templates.
    }



    private String fixSymbol(Currency c) {
        String baseStr = null;

        switch(c) {
            case BTC:
                baseStr = "XBT";
                break;

            default:
                baseStr = c.name().toUpperCase();
        }

        return baseStr;
    }

    private Currency parseCurrency(String curr) {
        Currency c = null;

        switch(curr) {

            case "XBT":
                c = Currency.BTC;
                break;

            case "ZEUR":
                c = Currency.EUR;
                break;

            case "ZUSD":
                c = Currency.USD;
                break;

            default:
                c = Currency.valueOf(curr);
        }

        return c;
    }

    private void addOrders(TradeDecision dec, double[][] raw, List<MarketOrder> orders, Currency base, Currency quote) {
        for(double[] askVal : raw) {

            CurrencyValue price = new CurrencyValue(askVal[0], quote);
            CurrencyValue volume = new CurrencyValue(askVal[1], base);

            orders.add(new MarketOrder(dec, volume, price));
        }
    }


    private TransactionType parseLedgerType(String type) {
        TransactionType tt = null;
        switch(type) {
            case "deposit":
                tt = TransactionType.DEPOSIT;
                break;

            default:
        }
        return tt;
    }


    private void parseLedger(AccountInfo accountInfo, JsonObject jsonLedger) {
        for(String ledgerKey : jsonLedger.keySet()) {

            JsonObject ledger = jsonLedger.getJsonObject(ledgerKey);


            String refId = ledger.getString("refid");
            Date timestamp = new Date((long)(1000.0 * ledger.getJsonNumber("time").doubleValue()));
            TransactionType type = parseLedgerType(ledger.getString("type"));
            String aclass = ledger.getString("aclass");

            Currency curr = parseCurrency(ledger.getString("asset"));

            double amount = Double.parseDouble(ledger.getString("amount"));
            double fee = Double.parseDouble(ledger.getString("fee"));
            double balance = Double.parseDouble(ledger.getString("balance"));

            WalletTransaction trans = new WalletTransaction();
            //trans.setBalance();
            trans.setDatestamp(timestamp);
            trans.setType(type);
            trans.setValue(new CurrencyValue(amount, curr));
            trans.setInfo(refId);

            Wallet w = accountInfo.getWallet(curr);
            if(w==null) {
                w = new Wallet(curr);
                accountInfo.setWallet(w);
            }

            w.addTransaction(trans);


            // only add implicit fee entry if fee was actually paid
            if(fee > 0.0) {
                WalletTransaction feeTransaction = new WalletTransaction();
                feeTransaction.setDatestamp(timestamp);
                feeTransaction.setType(TransactionType.FEE);
                feeTransaction.setValue(new CurrencyValue(fee, curr));
                feeTransaction.setInfo(refId);
                w.addTransaction(feeTransaction);
            }

        }
    }
}
