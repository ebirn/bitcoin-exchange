package at.outdated.bitcoin.exchange.api;

import at.outdated.bitcoin.exchange.api.account.AccountInfo;
import at.outdated.bitcoin.exchange.api.currency.Currency;
import at.outdated.bitcoin.exchange.api.market.MarketDepth;
import at.outdated.bitcoin.exchange.api.market.TickerValue;
import at.outdated.bitcoin.exchange.api.track.NumberTrack;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.ws.rs.HttpMethod;
import javax.ws.rs.client.*;
import javax.ws.rs.core.MediaType;
import java.io.StringReader;
import java.util.Date;
import java.util.ResourceBundle;

/**
 * Created with IntelliJ IDEA.
 * User: ebirn
 * Date: 24.05.13
 * Time: 17:00
 * To change this template use File | Settings | File Templates.
 */
public abstract class ExchangeApiClient {
    protected static Logger log = LoggerFactory.getLogger("client");

    protected NumberTrack apiLagTrack = new NumberTrack(5);

    protected Client client = ClientBuilder.newClient();

    protected final String userAgent = "ExchangeApiClient/1.0-Snapshot";

    public abstract AccountInfo getAccountInfo();

    public abstract TickerValue getTicker(Currency currency);

    public abstract Number getLag();


    protected JsonObject jsonFromString(String s) {
        return Json.createReader(new StringReader(s)).readObject();
    }

    protected double[][] parseNestedArray(JsonArray jsonArray) {


        int len = jsonArray.size();
        double[][] resultArray = new double[len][];

        for(int i=0; i<len; i++) {


            JsonArray innerJsonArray = jsonArray.getJsonArray(i);
            int innerLen = innerJsonArray.size();
            double[] inner = new double[innerLen];

            for(int j=0; j<innerLen; j++) {
                inner[j] = Float.parseFloat(innerJsonArray.get(j).toString());
            }
            resultArray[i] = inner;
        }

        return resultArray;
    }

    public abstract MarketDepth getMarketDepth(Currency base, Currency quote);

    final public double getApiLag() {
        return apiLagTrack.getStatistics().getGeometricMean();  //To change body of implemented methods use File | Settings | File Templates.
    }


    protected <R> R simpleGetRequest(WebTarget resource, Class<R> resultClass) {
        return simpleRequest(resource, resultClass, HttpMethod.GET, null);
    }

    protected <R> R simplePostRequest(WebTarget resource, Class<R> resultClass, Object payload) {
        return simpleRequest(resource, resultClass, HttpMethod.POST, payload);
    }

    protected <R> R simplePutRequest(WebTarget resource, Class<R> resultClass, Object payload) {
        return simpleRequest(resource, resultClass, HttpMethod.PUT, payload);
    }


    protected Invocation.Builder setupResource(WebTarget res) {
        return res.request().header("User-Agent", userAgent).accept(MediaType.APPLICATION_JSON_TYPE);
    }

    protected abstract Invocation.Builder setupProtectedResource(WebTarget res);

    protected <R> R simpleRequest(WebTarget resource, Class<R> resultClass, String httpMethod, Object payload) {

        R result = null;

        Date requestDate = new Date();
        try {
            result = setupResource(resource).method(httpMethod, Entity.json(payload), resultClass); //invoke(resultClass);
        }
        // FIXME: replace that!
        //catch ( uie) {
        //    handleApiError(uie);
       // }
        catch(Exception e) {
            log.error("unexpected exception: {}", e);
        }
        finally {
            updateApiLag(requestDate);
        }


        return result;
    }


    /*
    protected void handleApiError(UniformInterfaceException uie) {
        if(uie.getResponse().getClientResponseStatus() == ClientResponse.Status.BAD_GATEWAY) {

            log.error("API error: BAD GATEWAY");
        }
    }
*/

    protected void updateApiLag(Date requestDate/*, Date responseDate*/) {
        Date responseDate = new Date();
        double apiDiff = (responseDate.getTime()-requestDate.getTime())/1000.0;
        apiLagTrack.insert(apiDiff);
    }



    protected String getSecret(String market) {
        ResourceBundle bundle = ResourceBundle.getBundle("at.outdated.bitcoin.exchange.api.bitcoin-exchange");

        String name = market + ".secret";
        return bundle.getString(name);

        //return getProperties().get(name).toString();
    }

    protected String getUserId(String market) {
        ResourceBundle bundle = ResourceBundle.getBundle("at.outdated.bitcoin.exchange.api.bitcoin-exchange");
        String name = market + ".userid";
        return bundle.getString(name);

        //return getProperties().get(name).toString();
    }



    // transactions in the past -> for calculating performance
    // https://www.bitstamp.net/api/user_transactions/





    // open orders: orders have been sent, but not processed yet
    // https://www.bitstamp.net/api/open_orders/


}
