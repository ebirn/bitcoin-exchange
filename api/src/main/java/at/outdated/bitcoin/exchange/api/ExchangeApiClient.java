package at.outdated.bitcoin.exchange.api;

import at.outdated.bitcoin.exchange.api.account.WalletHistory;
import at.outdated.bitcoin.exchange.api.client.AccountInfo;
import at.outdated.bitcoin.exchange.api.currency.Currency;
import at.outdated.bitcoin.exchange.api.market.TickerValue;
import at.outdated.bitcoin.exchange.api.track.NumberValueTrack;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.jersey.api.client.WebResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.HttpMethod;
import javax.ws.rs.core.MediaType;
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

    protected NumberValueTrack apiLagTrack = new NumberValueTrack(5);

    protected Client client = new Client();

    protected final String userAgent = "ExchangeApiClient/1.0-Snapshot";

    public abstract AccountInfo getAccountInfo();

    public abstract TickerValue getTicker(Currency currency);

    public abstract Number getLag();

    public abstract WalletHistory getWalletHistory(Currency currency);


    final public double getApiLag() {
        return apiLagTrack.getStatistics().getGeometricMean();  //To change body of implemented methods use File | Settings | File Templates.
    }


    protected <R> R simpleGetRequest(WebResource resource, Class<R> resultClass) {
        return simpleRequest(resource, resultClass, HttpMethod.GET, null);
    }

    protected <R> R simplePostRequest(WebResource resource, Class<R> resultClass, Object payload) {
        return simpleRequest(resource, resultClass, HttpMethod.POST, payload);
    }

    protected <R> R simplePutRequest(WebResource resource, Class<R> resultClass, Object payload) {
        return simpleRequest(resource, resultClass, HttpMethod.PUT, payload);
    }


    protected WebResource.Builder setupResource(WebResource res) {
        return res.header("User-Agent", userAgent).accept(MediaType.APPLICATION_JSON_TYPE);
    }

    protected abstract WebResource.Builder setupProtectedResource(WebResource res);

    protected <R> R simpleRequest(WebResource resource, Class<R> resultClass, String httpMethod, Object payload) {

        R result = null;

        Date requestDate = new Date();
        try {
            result = setupResource(resource).entity(payload).method(httpMethod, resultClass);
        }
        catch (UniformInterfaceException uie) {
            handleApiError(uie);
        }
        catch(Exception e) {
            log.error("unexpected exception: {}", e);
        }
        finally {
            updateApiLag(requestDate);
        }


        return result;
    }


    protected void handleApiError(UniformInterfaceException uie) {
        if(uie.getResponse().getClientResponseStatus() == ClientResponse.Status.BAD_GATEWAY) {

            log.error("API error: BAD GATEWAY");
        }
    }


    protected void updateApiLag(Date requestDate/*, Date responseDate*/) {
        Date responseDate = new Date();
        double apiDiff = (responseDate.getTime()-requestDate.getTime())/1000.0;
        apiLagTrack.insert(apiDiff);
    }


    /*
    private Properties getProperties()  {
        // loading xmlProfileGen.properties from the classpath
        Properties props = new Properties();

        ResourceBundle bundle = ResourceBundle.getBundle("bitcoin-exchange.properties");

        /*
        try(InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("bitcoin-exchange.properties")) {
            props.load(inputStream);
        }
        catch(IOException ioe) {
            ioe.printStackTrace();
        }

        return props;
    }
*/

    protected String getSecret(String market) {
        ResourceBundle bundle = ResourceBundle.getBundle("bitcoin-exchange");

        String name = market + ".secret";
        return bundle.getString(name);

        //return getProperties().get(name).toString();
    }

    protected String getUserId(String market) {
        ResourceBundle bundle = ResourceBundle.getBundle("bitcoin-exchange");
        String name = market + ".userid";
        return bundle.getString(name);

        //return getProperties().get(name).toString();
    }
}
