package at.outdated.bitcoin.exchange.mtgox;

import at.outdated.bitcoin.exchange.api.Currency;
import at.outdated.btrader.mechanics.track.NumberValueTrack;
import at.outdated.bitcoin.exchange.mtgox.auth.Nonce;
import at.outdated.bitcoin.exchange.mtgox.auth.RequestAuth;
import at.outdated.bitcoin.exchange.mtgox.wallet.WalletHistory;
import at.outdated.bitcoin.exchange.mtgox.wallet.WalletTransaction;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.client.urlconnection.HTTPSProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.util.Date;
import java.util.Iterator;

/**
 * Created with IntelliJ IDEA.
 * User: ebirn
 * Date: 03.05.13
 * Time: 11:18
 * To change this template use File | Settings | File Templates.
 */

@Stateless
@LocalBean
public class MtGoxClient {

    private final String API_BASE_URL = "https://data.mtgox.com/api/2/";

    //Paths
    private final String API_GET_INFO = "money/info";
    private final String API_GET_WALLET_HISTORY = "money/wallet/history";
    private final String API_TICKER_USD = "BTCUSD/MONEY/TICKER";
    private final String API_TICKER_EUR = "BTCEUR/MONEY/TICKER";
    private final String API_TICKER_FAST_USD = "BTCUSD/MONEY/TICKER_FAST";
    private final String API_TICKER_FAST_EUR = "BTCEUR/MONEY/TICKER_FAST";

    private final String API_WITHDRAW = "MONEY/BITCOIN/SEND_SIMPLE";
    private final String API_LAG = "MONEY/ORDER/LAG";
    private final String API_ADD_ORDER = "BTCUSD/MONEY/ORDER/ADD";

    private final String SIGN_HASH_FUNCTION = "HmacSHA512";
    private final String ENCODING = "UTF-8";

    private Client client = new Client();
    private WebResource apiBaseResource;

    private static Logger log = LoggerFactory.getLogger("client.mtgox");

    @PostConstruct
    public void startup() {

        KeyStore ks = loadCertificates();


        String storePass = "h4rdc0r_";
        try(InputStream keyStoreStream = RequestAuth.class.getResourceAsStream("mtgox.jks")) {

            ks.load(keyStoreStream, storePass.toCharArray());
            TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            tmf.init(ks);
            KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
            kmf.init(ks, storePass.toCharArray());

            SSLContext ctx = SSLContext.getInstance("TLS");
            ctx.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);
            SSLContext.setDefault(ctx);

            HTTPSProperties httpsProperties = new HTTPSProperties(null, ctx);

            DefaultClientConfig dcc = new DefaultClientConfig();
            dcc.getProperties().put(HTTPSProperties.PROPERTY_HTTPS_PROPERTIES, httpsProperties);

            client = Client.create(dcc);

            apiBaseResource = client.resource(API_BASE_URL);

        }
        catch(Exception e) {
            log.error("failed to load certificates");
            e.printStackTrace();
        }
    }

    private KeyStore loadCertificates() {
        KeyStore ks = null;
        try {
            ks = KeyStore.getInstance("JKS");
        }
        catch (KeyStoreException kse) {
            log.error("failed to initialize certificate keystore");
        }

        return ks;
    }

    public AccountInfo getAccountInfo() {

        Date requestDate = new Date();
        ClientResponse res = signedRequest(API_GET_INFO, "");

        if(res == null) {
            log.warn("failed to get account info");
            return null;
        }

        AccountInfo accountInfo = res.getEntity(ApiAccountInfo.class).getData();
        return accountInfo;
    }



    public TickerResponse getTicker(Currency currency) {

        TickerResponse ticker = null;

        try {
            String uri = "BTC" + currency.name() + "/money/ticker";
            WebResource webResource = client.resource(API_BASE_URL + uri);
            ApiTickerResponse tickerResponse = webResource.accept("application/json").get(ApiTickerResponse.class);

            ticker = tickerResponse.getData();
            ticker.setInCurrency(currency);
        }
        catch(UniformInterfaceException uie) {
            log.warn(currency + " ticker request failed.", uie);
        }
        return ticker;
    }


    public TickerResponse getFastTicker(Currency currency) {

        TickerResponse tickerResponse = null;

        try {
            WebResource webResource = client.resource(API_BASE_URL + API_TICKER_FAST_EUR);
            tickerResponse = webResource.accept("application/json").get(ApiTickerResponse.class).getData();
        }
        catch(UniformInterfaceException uie) {
            log.warn(currency + " ticker request failed.", uie);
        }

        return tickerResponse;
    }


    public LagResponse getLag() {
        WebResource webResource = client.resource(API_BASE_URL + API_LAG);


        try {
            ApiLagResponse lagResponse = webResource.accept(MediaType.APPLICATION_JSON_TYPE).get(ApiLagResponse.class);

            if(lagResponse == null) {
                log.warn("no response from api for trading lag");
                return null;
            }

            return lagResponse.getData();
        }
        catch (UniformInterfaceException uie) {
            log.warn("failed to update trading lag");
        }
        return null;
    }


    private ClientResponse signedRequest(String path, String data) {

        try {
            long nonce = Nonce.next()*1000;

            String payload = "nonce="+nonce;

            if(data != null && data.isEmpty() == false) {
                payload += ("&"+data);
            }

            RequestAuth auth = new RequestAuth();
            String signature = auth.hmac(path, payload);

            Date requestDate = new Date();

            WebResource.Builder builder = apiBaseResource.path(path).accept("application/json");
            ClientResponse res = builder.header("Rest-Key", RequestAuth.getKey())
                                        .header("Rest-Sign", signature)
                                        .type(MediaType.APPLICATION_FORM_URLENCODED_TYPE)
                                        .entity(payload)
                                        .post(ClientResponse.class);

            updateApiLag(requestDate);

            if(res.getClientResponseStatus().getFamily() == Response.Status.Family.SUCCESSFUL)
                return res;
        }
        catch(UniformInterfaceException  e) {
            handleApiError(e);
        }

        return null;
    }


    private NumberValueTrack apiLagTrack = new NumberValueTrack(5);

    protected void updateApiLag(Date requestDate/*, Date responseDate*/) {
        Date responseDate = new Date();
        double apiDiff = (responseDate.getTime()-requestDate.getTime())/1000.0;
        apiLagTrack.insert(apiDiff);
    }

    public double getApiLag() {
        return apiLagTrack.getStatistics().getMean();
    }


    public WalletHistory getWalletHistory(Currency currency) {

        Date requestDate = new Date();
        ClientResponse res = signedRequest(API_GET_WALLET_HISTORY, "currency="+currency.name());
        updateApiLag(requestDate);

        if(res == null) return null;

        WalletHistory walletHistory = res.getEntity(ApiWalletHistory.class).getData();

        // TODO: fix this somwhere else? remove empty/null Transaction
        Iterator<WalletTransaction> it = walletHistory.getTransactions().iterator();
        while(it.hasNext()) {
            WalletTransaction trans = it.next();
            if(trans.getValue() == null) it.remove();
        }

        return walletHistory;
    }



    private void handleApiError(UniformInterfaceException uie) {
        if(uie.getResponse().getClientResponseStatus() == ClientResponse.Status.BAD_GATEWAY) {

            log.error("API error: BAD GATEWAY");
        }
    }
}
