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
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
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


        WebTarget balanceTarget = client.target("https://api.kraken.com/0/private/TradeBalance");

        Future<String> rawBalance = setupProtectedResource(balanceTarget).async().get(String.class);
        try {
            log.debug("rawBalance: {}", rawBalance.get());


        }
        catch(Exception e) {
            log.error("failed to get account info", e);
        }
        return null;  //To change body of implemented methods use File | Settings | File Templates.
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

        long apiTimestamp = (new Date()).getTime();
        String signature = null;

        try {
            Mac mac = Mac.getInstance("HmacSHA512");
            SecretKeySpec secret_spec = new SecretKeySpec(Base64.decodeBase64(getSecret("kraken")), "HmacSHA512");
            mac.init(secret_spec);

            // path + NUL + POST (incl. nonce)

            String payload = "nonce="+apiTimestamp+"&otp=553834";
            String path = tgt.getUri().getPath();

            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            digest.update(payload.getBytes());
            mac.update(path.getBytes("UTF-8"));

            byte[] rawSignature = mac.doFinal(digest.digest());

            signature = Base64.encodeBase64URLSafeString(rawSignature);
        }
        catch(Exception e) {
            e.printStackTrace();
        }

        // POST data:
        // nonce = always increasing unsigned 64 bit integer
        // otp = two-factor password (if two-factor enabled, otherwise not required)


        Invocation.Builder builder = tgt.request();
        builder.header("API-Key", getUserId("kraken"));
        builder.header("API-Sign", signature);

        return builder;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
