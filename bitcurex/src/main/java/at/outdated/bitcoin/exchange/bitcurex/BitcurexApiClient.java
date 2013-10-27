package at.outdated.bitcoin.exchange.bitcurex;

import at.outdated.bitcoin.exchange.api.ExchangeApiClient;
import at.outdated.bitcoin.exchange.api.Market;
import at.outdated.bitcoin.exchange.api.account.AccountInfo;
import at.outdated.bitcoin.exchange.api.account.Wallet;
import at.outdated.bitcoin.exchange.api.currency.Currency;
import at.outdated.bitcoin.exchange.api.currency.CurrencyValue;
import at.outdated.bitcoin.exchange.api.market.*;
import at.outdated.bitcoin.exchange.bitcurex.jaxb.BitcurexAccountInfo;
import at.outdated.bitcoin.exchange.bitcurex.jaxb.BitcurexTickerValue;
import at.outdated.bitcoin.exchange.bitcurex.jaxb.TransactionType;
import org.apache.commons.codec.binary.Base64;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.json.*;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Form;
import java.io.StringReader;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: ebirn
 * Date: 30.05.13
 * Time: 12:50
 * To change this template use File | Settings | File Templates.
 */
public class BitcurexApiClient extends ExchangeApiClient {

    public BitcurexApiClient(Market market) {
        super(market);
    }

    // FIXME complete implementation!
    @Override
    public AccountInfo getAccountInfo() {

        /// see https://bitcurex.com/en-pages,eurapi.html

        // getOrders
        // getFunds
        // getTransactions

        WebTarget fundsTarget = client.target("https://eur.bitcurex.com/api/0/getFunds");
        Entity entity = Entity.form(new Form());

        Invocation.Builder builder = setupProtectedResource(fundsTarget, entity);
        String rawFunds = builder.post(entity, String.class);
        log.debug("raw funds: {}", rawFunds);

        entity = Entity.form(new Form());
        WebTarget ordersTarget = client.target("https://eur.bitcurex.com/api/0/getOrders");

        String rawOrders =  setupProtectedResource(ordersTarget, entity).post(entity, String.class);

        log.debug("raw orders: {}", rawOrders);


        WebTarget transactionsTarget = client.target("https://eur.bitcurex.com/api/0/getTransactions");
        Form form = new Form();

        form.param("type", "" + TransactionType.BTC_DEPOST.ordinal());
        entity = Entity.form(form);
        String rawTransactions =  setupProtectedResource(transactionsTarget, entity).post(entity, String.class);

        log.debug("raw transactions: {}", rawTransactions);


        JsonObject jsonFunds = jsonFromString(rawFunds);
        JsonObject jsonOrders = jsonFromString(rawOrders);
        JsonObject jsonTransactions = jsonFromString(rawTransactions);

        BitcurexAccountInfo info = new BitcurexAccountInfo();

        Wallet eurWallet = new Wallet(Currency.EUR);
        info.addWallet(eurWallet);
        eurWallet.setBalance(new CurrencyValue(Double.parseDouble(jsonFunds.getString("eurs")), Currency.EUR));

        Wallet btcWallet = new Wallet(Currency.BTC);
        info.addWallet(btcWallet);
        btcWallet.setBalance(new CurrencyValue(Double.parseDouble(jsonFunds.getString("btcs")), Currency.BTC));

        return info;
    }

    @Override
    public MarketDepth getMarketDepth(AssetPair asset) {
        Currency base = asset.getBase();
        Currency quote = asset.getQuote();

        WebTarget depthTarget = client.target("https://{curr}.bitcurex.com/data/trades.json")
                .resolveTemplate("curr", quote.name().toLowerCase());

        String raw = super.simpleGetRequest(depthTarget, String.class);

        JsonArray rawDepth =  Json.createReader(new StringReader(raw)).readArray();

        MarketDepth depth = new MarketDepth();
        depth.setBaseCurrency(base);
        for(JsonValue v : rawDepth) {
            JsonObject trade = (JsonObject) v;

            double price = Double.parseDouble(trade.getString("price"));
            double volume = Double.parseDouble(trade.getString("amount"));
            int type = trade.getJsonNumber("type").intValue();
            //trade.getJsonNumber("date");
            //trade.getJsonNumber("tid");

            // sell
            if(type == 1) {
                depth.getAsks().add(new MarketOrder(TradeDecision.SELL, new CurrencyValue(volume, base), new CurrencyValue(price, quote)));
            }
            // buy
            else {
                depth.getBids().add(new MarketOrder(TradeDecision.BUY, new CurrencyValue(volume, base), new CurrencyValue(price, quote)));
            }
        }


        return depth;
    }

    @Override
    protected <R> R simpleGetRequest(WebTarget resource, Class<R> resultClass) {
        String resultStr =  super.simpleGetRequest(resource, String.class);

        log.debug("BITCUREX raw: " + resultStr);

        R result = null;

        result = BitcurexJsonResolver.convertFromJson(resultStr, resultClass);

        return result;
    }

    @Override
    public TickerValue getTicker(AssetPair asset) {

        if(asset.getBase() != Currency.BTC) {
            throw new IllegalArgumentException("unsupported currency");
        }

        WebTarget tickerResource = client.target("https://" + asset.getQuote().name().toLowerCase() + ".bitcurex.com/data/ticker.json");

        BitcurexTickerValue bTicker = simpleGetRequest(tickerResource, BitcurexTickerValue.class);

        if(bTicker == null) return null;

        TickerValue ticker = bTicker.getTickerValue();
        ticker.setCurrency(asset.getQuote());
        return ticker;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Number getLag() {
        return 1.0;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    protected <T> Invocation.Builder setupProtectedResource(WebTarget res, Entity<T> entity) {

        /*
        headers = array(
                'Rest-Key: ' . key,
                'Rest-Sign: ' . base64_encode(hash_hmac('sha512', post_data, base64_decode(secret), true)),
                );
        */


        try {

            String secret = getSecret();
            Mac mac = Mac.getInstance("HmacSHA512");
            SecretKeySpec secret_spec = new SecretKeySpec(Base64.decodeBase64(secret), "HmacSHA512");
            mac.init(secret_spec);



            String nonce = Long.toString((new Date()).getTime());

            Form form = ((Entity<Form>) entity).getEntity();
            form.param("nonce", nonce);

            mac.update(formData2String(form).getBytes("UTF-8"));

            String sign = new String(Base64.encodeBase64(mac.doFinal(), false));


            Invocation.Builder builder = res.request();
            builder.header("Rest-Sign", sign);
            builder.header("Rest-Key", getUserId());


            return builder;  //To c
        }
        catch(Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}
