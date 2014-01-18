package at.outdated.bitcoin.exchange.kraken;

import at.outdated.bitcoin.exchange.api.OrderId;
import at.outdated.bitcoin.exchange.api.client.RestExchangeClient;
import at.outdated.bitcoin.exchange.api.market.Market;
import at.outdated.bitcoin.exchange.api.account.AccountInfo;
import at.outdated.bitcoin.exchange.api.account.TransactionType;
import at.outdated.bitcoin.exchange.api.account.Wallet;
import at.outdated.bitcoin.exchange.api.account.WalletTransaction;
import at.outdated.bitcoin.exchange.api.currency.Currency;
import at.outdated.bitcoin.exchange.api.currency.CurrencyValue;
import at.outdated.bitcoin.exchange.api.market.*;
import at.outdated.bitcoin.exchange.api.market.fee.SimplePercentageFee;
import at.outdated.bitcoin.exchange.kraken.jaxb.KrakenOpenOrderResult;
import at.outdated.bitcoin.exchange.kraken.jaxb.KrakenOrderCancelResult;
import at.outdated.bitcoin.exchange.kraken.jaxb.KrakenResponse;
import org.apache.commons.codec.binary.Base64;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonString;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.GenericType;
import java.security.MessageDigest;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;


/**
 * Created with IntelliJ IDEA.
 * User: ebirn
 * Date: 17.09.13
 * Time: 18:23
 * To change this template use File | Settings | File Templates.
 */

//FIXME: use GenericType in return values to do acutail jax/rs parsing
public class KrakenClient extends RestExchangeClient {

    public KrakenClient(Market market) {
        super(market);
        this.tradeFee = new SimplePercentageFee("0.002");
    }

    @Override
    public AccountInfo getAccountInfo() {

        KrakenAccountInfo accountInfo = new KrakenAccountInfo();


        WebTarget tradeHistoryTgt = client.target("https://api.kraken.com/0/private/TradesHistory");
        String rawTradeHistory = protectedPostRequest(tradeHistoryTgt, String.class, Entity.form(new Form()));
        //log.debug("tradeHistory: {}", rawTradeHistory);

        JsonObject jsonTrades = jsonFromString(rawTradeHistory).getJsonObject("result").getJsonObject("trades");
        if(jsonTrades != null) {
            for(String tradeKey : jsonTrades.keySet()) {
                // TODO finish this
                jsonTrades.getJsonObject(tradeKey);
                //log.debug("TRADE: {}", tradeKey);
            }
        }


        WebTarget ledgerTgt = client.target("https://api.kraken.com/0/private/Ledgers");
        String rawLedger = protectedPostRequest(ledgerTgt, String.class, Entity.form(new Form()));
        //log.debug("ledger: {}", rawLedger);
        JsonObject jsonLedger = jsonFromString(rawLedger).getJsonObject("result").getJsonObject("ledger");
        if(jsonLedger != null) parseLedger(accountInfo, jsonLedger);


        WebTarget balanceTgt = client.target("https://api.kraken.com/0/private/Balance");
        String rawBalance = protectedPostRequest(balanceTgt, String.class, Entity.form(new Form()));
        //log.debug("balance: {}", rawBalance);

        JsonObject balances = jsonFromString(rawBalance).getJsonObject("result");

        if(balances != null)
            for(String currKey : balances.keySet()) {
            Currency curr = parseCurrency(currKey);

            Wallet wallet = accountInfo.getWallet(curr);
            if(wallet == null) {
                wallet = new Wallet(curr);
                accountInfo.addWallet(wallet);
            }

            wallet.setBalance(new CurrencyValue(Double.parseDouble(balances.getString(currKey)), curr));
        }

        // TODO: fee parsing
        WebTarget feeTgt = client.target("https://api.kraken.com/0/public/AssetPairs?info=fees");

        return accountInfo;
    }

    @Override
    public TickerValue getTicker(AssetPair asset) {

        String currencyKey = fixSymbol(asset.getBase()) + fixSymbol(asset.getQuote());

        WebTarget webResource = client.target("https://api.kraken.com/0/public/Ticker?pair=" + currencyKey);
        //KrakenTickerResponse tickerResponse =

       String tickerRaw = simpleGetRequest(webResource, String.class);

        JsonObject jsonTicker = jsonFromString(tickerRaw);



        JsonObject resultData = jsonTicker.getJsonObject("result").getJsonObject(currencyKey);

        TickerValue value = new TickerValue();
        value.setAsset(asset);
        value.setLast(Double.parseDouble(resultData.getJsonArray("c").getString(0)));

        value.setVolume(Double.parseDouble(resultData.getJsonArray("v").getString(0)));

        value.setAsk(Double.parseDouble(resultData.getJsonArray("a").getString(0)));
        value.setBid(Double.parseDouble(resultData.getJsonArray("b").getString(0)));

        value.setHigh(Double.parseDouble(resultData.getJsonArray("h").getString(0)));
        value.setLow(Double.parseDouble(resultData.getJsonArray("l").getString(0)));
        return value;
    }

    @Override
    public Number getLag() {
        return 1;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public MarketDepth getMarketDepth(AssetPair asset) {

        Currency base = asset.getBase();
        Currency quote = asset.getQuote();

        WebTarget webResource = client.target("https://api.kraken.com/0/public/Depth?pair=" + fixSymbol(base) + fixSymbol(quote));
        String rawDepth = simpleGetRequest(webResource, String.class);

        //log.debug("raw depth: {}", rawDepth);

        JsonObject jsonDepth = jsonFromString(rawDepth).getJsonObject("result").getJsonObject(fixSymbol(base) + fixSymbol(quote));

        double asks[][] = parseNestedArray(jsonDepth.getJsonArray("asks"));
        double bids[][] = parseNestedArray(jsonDepth.getJsonArray("bids"));


        MarketDepth depth = new MarketDepth(asset);

        for(double ask[] : asks) {
            double price = ask[0];
            double volume = ask[1];
            depth.addAsk(volume, price);
        }

        for(double bid[] : bids) {
            double price = bid[0];
            double volume = bid[1];
            depth.addBid(volume, price);
        }

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

            //log.debug("payload: {}", formStr);
            //log.debug("sign payload: {}", signatureData);

            String path = tgt.getUri().getPath();
            //log.debug("path: {}", path);


            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            digest.update(signatureData.getBytes());

            mac.update(path.getBytes("UTF-8"));

            byte[] rawSignature = mac.doFinal(digest.digest());

            // need exactly this function, otherwise might add linebreaks after 76 characters
            String signature = new String(Base64.encodeBase64(rawSignature, false));
            //log.debug("signature: {}", signature);

            // POST data:
            // nonce = always increasing unsigned 64 bit integer

            String key = getUserId();

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

        if(c.isCrypto()) {
            baseStr = "X" + baseStr;
        }
        else {
            baseStr = "Z" + baseStr;
        }

        return baseStr;
    }

    private String fixSymbol(AssetPair asset) {
        return fixSymbol(asset.getBase()) + fixSymbol(asset.getQuote());
    }

    private Currency parseCurrency(String currStr) {
        // remove prefix
        currStr = currStr.substring(1);

        switch(currStr) {

            case "XBT":
                return Currency.BTC;

            default:
                return Currency.valueOf(currStr);
        }

    }

    private TransactionType parseLedgerType(String type) {
        TransactionType tt = null;
        switch(type) {
            case "deposit":
                tt = TransactionType.DEPOSIT;
                break;

            case "trade":
                tt = TransactionType.IN;
                break;

            case "withdrawal":
                tt = TransactionType.WITHDRAW;
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
                accountInfo.addWallet(w);
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

    /*
    see also https://www.kraken.com/help/api#get-open-orders
     */
    @Override
    public List<MarketOrder> getOpenOrders() {

        List<MarketOrder> orders = new ArrayList<>();


        // TODO: write adapter for JSON "key": { ... } -> Map<Key,Object> mapping
        GenericType<KrakenResponse<KrakenOpenOrderResult>> resultType = new GenericType<KrakenResponse<KrakenOpenOrderResult>>() {};

        Entity payload = Entity.form(new Form());
        String rawOpenResult =  setupProtectedResource(client.target("https://api.kraken.com/0/private/OpenOrders"), payload).post(payload).readEntity(String.class);

        // sample result:
        // {"error":[],"result":{"open":{"OHKQFZ-ALIP3-SBVVCF":{"refid":null,"userref":null,"status":"open","opentm":1388937107.352,"starttm":0,"expiretm":0,"descr":{"pair":"XBTEUR","type":"buy","ordertype":"limit","price":"0.75000","price2":"0","leverage":"none","order":"buy 2.00000000 XBTEUR @ limit 0.75000"},"vol":"2.00000000","vol_exec":"0.00000000","cost":"0.00000","fee":"0.00000","price":"0.00000","misc":"","oflags":""},"OSJPS5-K5GK2-NDEQBQ":{"refid":null,"userref":null,"status":"open","opentm":1388937079.8802,"starttm":0,"expiretm":0,"descr":{"pair":"XBTEUR","type":"buy","ordertype":"limit","price":"1.00000","price2":"0","leverage":"none","order":"buy 1.00000000 XBTEUR @ limit 1.00000"},"vol":"1.00000000","vol_exec":"0.00000000","cost":"0.00000","fee":"0.00000","price":"0.00000","misc":"","oflags":""}}}}

        JsonObject jsonOpenResult = jsonFromString(rawOpenResult);
        JsonObject jsonOpen = jsonOpenResult.getJsonObject("result").getJsonObject("open");

        for(String key : jsonOpen.keySet()) {
            log.info("open order: {}", key);

            MarketOrder order = new MarketOrder();

            order.setId(new OrderId(market, key));

            orders.add(order);
        }


        return orders;
    }

    /*
        see also: https://www.kraken.com/help/api#add-standard-order
         */
    @Override
    public OrderId placeOrder(AssetPair asset, TradeDecision decision, CurrencyValue volume, CurrencyValue price) {

        //  https://api.kraken.com/0/private/AddOrder
        WebTarget orderTgt = client.target("https://api.kraken.com/0/private/AddOrder");

        Form params = new Form();

        String type = null;

        switch(decision) {
            case BUY:
                type = "buy";
                break;

            case SELL:
                type = "sell";
                break;

            default:
                type = "ERROR";
        }

        params.param("pair", fixSymbol(asset));
        params.param("type", type); // buy / sell
        params.param("ordertype", "limit"); // for now we don't support anything else

        NumberFormat nf = NumberFormat.getNumberInstance(Locale.US);
        nf.setMinimumFractionDigits(8);
        nf.setMaximumIntegerDigits(8);
        nf.setMaximumFractionDigits(15);

        params.param("price", nf.format(price.getValue()));
        params.param("volume", nf.format(volume.getValue()));

        //params.param("validate", "true");


        Entity entity = Entity.form(params);
        String rawResult = setupProtectedResource(orderTgt, entity).buildPost(entity).invoke(String.class);

        log.info("raw result: {}", rawResult);

        JsonObject jsonResult = jsonFromString(rawResult);

        JsonArray error = jsonResult.getJsonArray("error");
        if(error.size() > 0) {
            log.error("failed to add order: {}", error.getValuesAs(JsonString.class));
            return null;
        }


        jsonResult = jsonResult.getJsonObject("result");

        log.info("created order: {}", jsonResult.getJsonObject("descr").getString("order"));

        List<String> txids = new ArrayList<>();
        for(JsonString txid : jsonResult.getJsonArray("txid").getValuesAs(JsonString.class)) {

            txids.add(txid.getString());
        }

        log.info("transaction ids: {}", txids);

        return new OrderId(market, txids.get(0));
    }

    /*
    see also: https://www.kraken.com/help/api#cancel-open-order
     */
    @Override
    public boolean cancelOrder(OrderId order) {

        GenericType<KrakenResponse<KrakenOrderCancelResult>> resultType = new GenericType<KrakenResponse<KrakenOrderCancelResult>>() {


        };

        Form params = new Form();
        params.param("txid", order.getIdentifier());

        Entity payload = Entity.form(params);

        //KrakenResponse<KrakenOrderCancelResult> result = setupProtectedResource(client.target("https://api.kraken.com/0/private/CancelOrder"), payload).post(payload).readEntity(resultType);
        String rawResult = setupProtectedResource(client.target("https://api.kraken.com/0/private/CancelOrder"), payload).post(payload).readEntity(String.class);

        log.info("cancel result: {}", rawResult);

        JsonObject jsonResult = jsonFromString(rawResult);

        JsonArray error = jsonResult.getJsonArray("error");
        // there was an error, cannot delete order
        if(error != null && error.size() > 0) {
            log.warn("cancel error: {}", error.getValuesAs(JsonString.class));
            return false;
        }

        JsonObject result = jsonResult.getJsonObject("result");
        // FIXME:
        if(result != null) {

            int count = result.getInt("count", 0);
            int pending = 0;
            JsonArray pendingArr = result.getJsonArray("pending");
            if(pendingArr != null) {
                pending = pendingArr.size();
            }

            int total = pending + count;

            log.info("removed {} orders, (pending: {})", count, pending);
            return total > 0;
        }

        log.warn("failed to cancel: {}", order);
        return false;
    }
}
