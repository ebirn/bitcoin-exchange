package at.outdated.bitcoin.exchange.kraken;

import at.outdated.bitcoin.exchange.api.ExchangeApiClient;
import at.outdated.bitcoin.exchange.api.account.AccountInfo;
import at.outdated.bitcoin.exchange.api.currency.Currency;
import at.outdated.bitcoin.exchange.api.currency.CurrencyValue;
import at.outdated.bitcoin.exchange.api.market.MarketDepth;
import at.outdated.bitcoin.exchange.api.market.MarketOrder;
import at.outdated.bitcoin.exchange.api.market.TickerValue;
import at.outdated.bitcoin.exchange.api.market.TradeDecision;
import at.outdated.bitcoin.exchange.kraken.jaxb.*;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Hex;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.json.JsonObject;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
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

    @Override
    public AccountInfo getAccountInfo() {


        WebTarget balanceTarget = client.target("https://api.kraken.com/0/private/Balance");

        long apiTimestamp = (new Date()).getTime()*1000L;

        String postPayload = "nonce="+apiTimestamp;
        String signature = null;

        try {
            Mac mac = Mac.getInstance("HmacSHA512");
            SecretKeySpec secret_spec = new SecretKeySpec(Base64.decodeBase64(getSecret("kraken")), "HmacSHA512");
            mac.init(secret_spec);

            // path + NUL + POST (incl. nonce)
            String signatureData = apiTimestamp + postPayload;

            log.debug("payload: {}", postPayload);
            log.debug("sign payload: {}", signatureData);

            String path = balanceTarget.getUri().getPath();
            log.debug("path: {}", path);


            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            digest.update(signatureData.getBytes());

            mac.update(path.getBytes("UTF-8"));

            byte[] rawSignature = mac.doFinal(digest.digest());

            // need exactly this function, otherwise might add linebreaks after 76 characters
            signature = new String(Base64.encodeBase64(rawSignature, false));
            log.debug("signature: {}", signature);

            // POST data:
            // nonce = always increasing unsigned 64 bit integer
            // otp = two-factor password (if two-factor enabled, otherwise not required)


            String key = getUserId("kraken");
            log.debug("key: {}", key);

            Invocation.Builder builder = balanceTarget.request();
            builder.header("API-Key", key);
            builder.header("API-Sign", signature);

            String rawBalance = builder.post(Entity.entity(postPayload, MediaType.APPLICATION_FORM_URLENCODED_TYPE), String.class);

            log.debug("rawBalance: {}", rawBalance);


        }
        catch(Exception e) {
            log.error("failed to get account info", e);
        }
        return new KrakenAccountInfo();  //To change body of implemented methods use File | Settings | File Templates.
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
    protected Invocation.Builder setupProtectedResource(WebTarget tgt) {
        // see https://www.kraken.com/help/api

        // headers:
        // API-Key = API key
        // API-Sign = Message signature using HMAC-SHA512 of (URI path + SHA256(nonce + POST data)) and base64 decoded secret API key

        // 1381172423349903L; //
        //
        long apiTimestamp = 1381173303029257L; //(new Date()).getTime();
        String signature = null;

        try {
            Mac mac = Mac.getInstance("HmacSHA512");
            SecretKeySpec secret_spec = new SecretKeySpec(Base64.decodeBase64(getSecret("kraken")), "HmacSHA512");
            mac.init(secret_spec);

            // path + NUL + POST (incl. nonce)

            String postPayload = "nonce="+apiTimestamp;
            String signatureData = apiTimestamp + postPayload;

            log.info("payload: {}", postPayload);
            log.info("sign payload: {}", signatureData);

            String path = tgt.getUri().getPath();
            log.info("path: {}", path);


            MessageDigest digest = MessageDigest.getInstance("SHA-256");

            digest.update(signatureData.getBytes());
            mac.update(path.getBytes("UTF-8"));

            byte[] rawSignature = mac.doFinal(digest.digest());

            // need exactly this function, otherwise might add linebreaks after 76 characters
            signature = new String(Base64.encodeBase64(rawSignature, false));
            log.info("signatur: {}", signature);
        }
        catch(Exception e) {
            e.printStackTrace();
        }

        // POST data:
        // nonce = always increasing unsigned 64 bit integer
        // otp = two-factor password (if two-factor enabled, otherwise not required)


        String key = getUserId("kraken");
        log.info("key: {}", key);

        Invocation.Builder builder = tgt.request();
        builder.header("API-Key", key);
        builder.header("API-Sign", signature);

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

    private void addOrders(TradeDecision dec, double[][] raw, List<MarketOrder> orders, Currency base, Currency quote) {
        for(double[] askVal : raw) {

            CurrencyValue price = new CurrencyValue(askVal[0], quote);
            CurrencyValue volume = new CurrencyValue(askVal[1], base);

            orders.add(new MarketOrder(dec, volume, price));
        }
    }


}
