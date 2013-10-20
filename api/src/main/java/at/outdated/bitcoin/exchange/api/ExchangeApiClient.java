package at.outdated.bitcoin.exchange.api;

import at.outdated.bitcoin.exchange.api.account.AccountInfo;
import at.outdated.bitcoin.exchange.api.currency.Currency;
import at.outdated.bitcoin.exchange.api.market.MarketDepth;
import at.outdated.bitcoin.exchange.api.market.TickerValue;
import at.outdated.bitcoin.exchange.api.track.NumberTrack;
import org.glassfish.jersey.message.internal.ReaderWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.ws.rs.HttpMethod;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.client.*;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import java.io.IOException;
import java.io.OutputStream;
import java.io.StringReader;
import java.net.URLEncoder;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.concurrent.Future;

/**
 * Created with IntelliJ IDEA.
 * User: ebirn
 * Date: 24.05.13
 * Time: 17:00
 * To change this template use File | Settings | File Templates.
 */
public abstract class ExchangeApiClient {
    protected Logger log = LoggerFactory.getLogger("client");

    protected NumberTrack apiLagTrack = new NumberTrack(5);

    protected Client client = ClientBuilder.newClient();

    protected final String userAgent = "ExchangeApiClient/1.0a";

    public abstract AccountInfo getAccountInfo();

    public TickerValue getTicker(Currency base) {
        return getTicker(base, market.getPrimaryCurrency());
    }

    public abstract TickerValue getTicker(Currency base, Currency quote);

    public double getQuote(Currency base, Currency quote) {

        double rate = Double.NaN;

        TickerValue ticker = null;

        try {
            ticker = getTicker(base, quote);
            rate = ticker.getBid();
        }
        catch(Exception e) {

        }

        if(ticker == null) {
            ticker = getTicker(quote, base);
            rate = 1.0 / ticker.getAsk();
        }

        return rate;
    }

    public abstract Number getLag();

    protected Market market;


    public ExchangeApiClient(Market market) {
        this.market = market;
        log = LoggerFactory.getLogger("client." + market.getKey());
    }


    protected JsonObject jsonFromString(String s) {
        return Json.createReader(new StringReader(s)).readObject();
    }

    protected JsonArray jsonArrayFromString(String s) {
        return Json.createReader(new StringReader(s)).readArray();
    }

    protected double[][] parseNestedArray(JsonArray jsonArray) {


        int len = jsonArray.size();
        double[][] resultArray = new double[len][];

        for(int i=0; i<len; i++) {


            JsonArray innerJsonArray = jsonArray.getJsonArray(i);
            int innerLen = innerJsonArray.size();
            double[] inner = new double[innerLen];

            for(int j=0; j<innerLen; j++) {

                // parse crappy
                switch(innerJsonArray.get(j).getValueType()) {
                    case STRING:
                        inner[j] = Double.parseDouble(innerJsonArray.getString(j));
                        break;

                    case NUMBER:
                        inner[j] = innerJsonArray.getJsonNumber(j).doubleValue();
                        break;
                }
            }
            resultArray[i] = inner;
        }

        return resultArray;
    }

    public abstract MarketDepth getMarketDepth(Currency base, Currency quote);

    final public double getApiLag() {
        return apiLagTrack.getStatistics().getGeometricMean();  //To change body of implemented methods use File | Settings | File Templates.
    }


    // simplified public api
    protected <R> R simpleGetRequest(WebTarget resource, Class<R> resultClass) {
        return syncRequest(resource, resultClass, HttpMethod.GET, null);
    }

    protected <R> R simplePostRequest(WebTarget resource, Class<R> resultClass, Entity payload) {
        return syncRequest(resource, resultClass, HttpMethod.POST, payload);
    }

    protected <R> R simplePutRequest(WebTarget resource, Class<R> resultClass, Entity payload) {
        return syncRequest(resource, resultClass, HttpMethod.PUT, payload);
    }


    // simplified protected api
    protected <R> R protectedGetRequest(WebTarget resource, Class<R> resultClass) {
        return syncRequest(resource, resultClass, HttpMethod.GET, null, true);
    }

    protected <R> R protectedPostRequest(WebTarget resource, Class<R> resultClass, Entity payload) {
        return syncRequest(resource, resultClass, HttpMethod.POST, payload, true);
    }

    protected <R> R protectedPutRequest(WebTarget resource, Class<R> resultClass, Entity payload) {
        return syncRequest(resource, resultClass, HttpMethod.PUT, payload, true);
    }

    protected <T> Invocation.Builder setupResource(WebTarget res, Entity<T> e) {
        return res.request().header("User-Agent", userAgent);
    }

    protected abstract <T> Invocation.Builder setupProtectedResource(WebTarget res, Entity<T> entity);

    protected <R> Future<R> asyncRequest(WebTarget resource, Class<R> resultClass, String httpMethod, Entity payload) {
        return asyncRequest(resource, resultClass, httpMethod, payload, false);
    }

    protected <R> Future<R> asyncRequest(WebTarget resource, Class<R> resultClass, String httpMethod, Entity payload, boolean secure) {

        Future<R> result = null;

        Invocation.Builder builder = null;
        if(secure) {
            builder = setupProtectedResource(resource, payload);
        }
        else {
            builder = setupResource(resource, payload);
        }

        result = builder.async().method(httpMethod, payload, resultClass);

        return result;
    }

    protected <R> R syncRequest(WebTarget resource, Class<R> resultClass, String httpMethod, Entity payload) {
        return syncRequest(resource, resultClass, httpMethod, payload, false);
    }

    protected <R> R syncRequest(WebTarget resource, Class<R> resultClass, String httpMethod, Entity payload, boolean secure) {

        R result = null;
        Date requestDate = new Date();
        try {
            Invocation.Builder builder = null;

            if(secure) {
                builder = setupProtectedResource(resource, payload);
            }
            else {
                builder = setupResource(resource, payload);
            }

            result = builder.header("User-Agent", userAgent).method(httpMethod, payload, resultClass);
        }
        //
        catch (WebApplicationException wae) {
            handleApiError(wae);
        }
        catch(Exception e) {
            log.error("unexpected exception: {}", e);
        }
        finally {
            updateApiLag(requestDate);
        }

        return result;
    }


    // very basic request error handling: log it!
    protected void handleApiError(javax.ws.rs.WebApplicationException wae) {
        log.error("failed request: {}", wae.getResponse().getStatusInfo());
        log.error(wae.getMessage());
    }


    protected void updateApiLag(Date requestDate/*, Date responseDate*/) {
        Date responseDate = new Date();
        double apiDiff = (responseDate.getTime()-requestDate.getTime())/1000.0;
        apiLagTrack.insert(apiDiff);
    }



    protected String getSecret() {
        ResourceBundle bundle = ResourceBundle.getBundle("bitcoin-exchange");

        String name = market.getKey() + ".secret";
        return bundle.getString(name);
    }

    protected String getUserId() {
        ResourceBundle bundle = ResourceBundle.getBundle("bitcoin-exchange");

        String name = market.getKey() + ".userid";
        return bundle.getString(name);
    }


    // this is taken from Form MessageBodyWriter in Glassfish, should produce same output
    // necessary for API Sign headers
    protected String formData2String(Form form) {
        final StringBuilder sb = new StringBuilder();

        try {
            for (Map.Entry<String, List<String>> e : form.asMap().entrySet()) {
                for (String value : e.getValue()) {
                    if (sb.length() > 0) {
                        sb.append('&');
                    }
                    sb.append(URLEncoder.encode(e.getKey(), "UTF-8"));
                    if (value != null) {
                        sb.append('=');
                        sb.append(URLEncoder.encode(value, "UTF-8"));
                    }
                }
            }
        }
        catch(Exception e) {
            log.error("failed to convert form", e);
        }

        return sb.toString();
    }
}
