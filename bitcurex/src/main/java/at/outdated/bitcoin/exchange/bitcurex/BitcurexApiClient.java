package at.outdated.bitcoin.exchange.bitcurex;

import at.outdated.bitcoin.exchange.api.ExchangeApiClient;
import at.outdated.bitcoin.exchange.api.Market;
import at.outdated.bitcoin.exchange.api.account.AccountInfo;
import at.outdated.bitcoin.exchange.api.currency.Currency;
import at.outdated.bitcoin.exchange.api.currency.CurrencyValue;
import at.outdated.bitcoin.exchange.api.market.MarketDepth;
import at.outdated.bitcoin.exchange.api.market.MarketOrder;
import at.outdated.bitcoin.exchange.api.market.TickerValue;
import at.outdated.bitcoin.exchange.api.market.TradeDecision;
import org.apache.commons.codec.binary.Base64;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.json.*;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.MediaType;
import java.io.StringReader;
import java.net.URLEncoder;
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

    @Override
    public AccountInfo getAccountInfo() {

        /// see https://bitcurex.com/en-pages,eurapi.html

        // getOrders
        // getFunds
        // getTransactions

        WebTarget fundsTarget = client.target("https://eur.bitcurex.com/api/0/getFunds");
        //WebTarget fundsTarget = client.target("https://eur.bitcurex.com/api/0/getFunds");


        Invocation.Builder builder = setupProtectedResource(fundsTarget, Entity.entity("", MediaType.APPLICATION_FORM_URLENCODED_TYPE));

        String payload = null;
        try {

            /*
            headers = array(
            'Rest-Key: ' . key,
            'Rest-Sign: ' . base64_encode(hash_hmac('sha512', post_data, base64_decode(secret), true)),
            );
            */

            String secret = getSecret();
            Mac mac = Mac.getInstance("HmacSHA512");
            SecretKeySpec secret_spec = new SecretKeySpec(Base64.decodeBase64(secret), "HmacSHA512");
            mac.init(secret_spec);



            String nonce = Long.toString((new Date()).getTime());
            payload = URLEncoder.encode("nonce="+nonce, "UTF-8");
            mac.update(payload.getBytes("UTF-8"));

            builder.header("Rest-Sign", Base64.encodeBase64(mac.doFinal(), false).toString());
        }
        catch(Exception e) {
            e.printStackTrace();
        }

        String raw = builder.post(Entity.entity(payload, MediaType.APPLICATION_FORM_URLENCODED_TYPE), String.class);

        log.info("raw: {}", raw);

        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public MarketDepth getMarketDepth(Currency base, Currency quote) {

        WebTarget depthTarget = client.target("https://" + quote.name().toLowerCase() + ".bitcurex.com/data/trades.json");

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
    public TickerValue getTicker(Currency currency) {


        WebTarget tickerResource = client.target("https://" + currency.name().toLowerCase() + ".bitcurex.com/data/ticker.json");

        BitcurexTickerValue bTicker = simpleGetRequest(tickerResource, BitcurexTickerValue.class);

        if(bTicker == null) return null;

        TickerValue ticker = bTicker.getTickerValue();
        ticker.setCurrency(currency);
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

        Invocation.Builder builder = res.request();

        builder.header("Rest-Key", getUserId());


        return builder;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
