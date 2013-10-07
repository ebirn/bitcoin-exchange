package at.outdated.bitcoin.exchange.kraken;

import at.outdated.bitcoin.exchange.api.ExchangeApiClient;
import at.outdated.bitcoin.exchange.api.account.AccountInfo;
import at.outdated.bitcoin.exchange.api.currency.Currency;
import at.outdated.bitcoin.exchange.api.market.MarketDepth;
import at.outdated.bitcoin.exchange.api.market.TickerValue;
import at.outdated.bitcoin.exchange.kraken.jaxb.*;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Hex;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import java.security.MessageDigest;
import java.util.Date;
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

            log.info("payload: {}", postPayload);
            log.info("sign payload: {}", signatureData);

            String path = balanceTarget.getUri().getPath();
            log.info("path: {}", path);


            MessageDigest digest = MessageDigest.getInstance("SHA-256");

            digest.update(signatureData.getBytes());


            mac.update(path.getBytes("UTF-8"));

            byte[] rawSignature = mac.doFinal(digest.digest());

            // need exactly this function, otherwise might add linebreaks after 76 characters
            signature = new String(Base64.encodeBase64(rawSignature, false));
            log.info("signature: {}", signature);

            // POST data:
            // nonce = always increasing unsigned 64 bit integer
            // otp = two-factor password (if two-factor enabled, otherwise not required)


            String key = getUserId("kraken");
            log.info("key: {}", key);

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

        WebTarget webResource = client.target("https://api.kraken.com/0/public/Ticker?pair=XBT"+currency.name());
        //KrakenTickerResponse tickerResponse =

        TickerValue value = null;

        KrakenTickerResponse response = simpleGetRequest(webResource, KrakenTickerResponse.class);

        KrakenTickerValue tickerResponse = ( response.getResult().getXXBTZEUR());

        if(tickerResponse != null)
            value = tickerResponse.getValue();

        return value;
    }

    @Override
    public Number getLag() {
        return 1;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public MarketDepth getMarketDepth(Currency base, Currency quote) {

        WebTarget webResource = client.target("https://api.kraken.com/0/public/Depth?pair=XBT"+quote.name());
        KrakenResponse response = simpleGetRequest(webResource, KrakenResponse.class);

        KrakenDepthValue depthResponse = (KrakenDepthValue) response.getResult().getXXBTZEUR();

        MarketDepth depth = null;

        if(depthResponse != null)
            depth = depthResponse.getValue();

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
}
