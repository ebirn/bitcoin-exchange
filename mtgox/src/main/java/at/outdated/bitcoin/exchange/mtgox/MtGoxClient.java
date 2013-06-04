package at.outdated.bitcoin.exchange.mtgox;

import at.outdated.bitcoin.exchange.api.ExchangeApiClient;
import at.outdated.bitcoin.exchange.api.account.AccountInfo;
import at.outdated.bitcoin.exchange.api.account.Wallet;
import at.outdated.bitcoin.exchange.api.account.WalletTransaction;
import at.outdated.bitcoin.exchange.api.currency.Currency;
import at.outdated.bitcoin.exchange.api.market.TickerValue;
import at.outdated.bitcoin.exchange.mtgox.auth.Nonce;
import at.outdated.bitcoin.exchange.mtgox.auth.RequestAuth;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.jersey.client.urlconnection.HTTPSProperties;
import org.slf4j.LoggerFactory;

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

public class MtGoxClient extends ExchangeApiClient {

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

    private WebResource apiBaseResource;

    static {
        log = LoggerFactory.getLogger("client.mtgox");
    }

    public MtGoxClient() {
        apiBaseResource = client.resource(API_BASE_URL);
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

    @Override
    public AccountInfo getAccountInfo() {


        ClientResponse res = signedRequest(API_GET_INFO, "");

        if(res == null) {
            log.warn("failed to get account info");
            return null;
        }

        MtGoxAccountInfo accountInfo = res.getEntity(ApiAccountInfo.class).getData();

        // fix up wallte structure, load transaction data
        MtGoxWallets wallets = accountInfo.getWallets();
        for(Currency c : wallets.getCurrencies()) {
            Wallet w = wallets.getWallet(c);

            MtGoxWalletHistory history = this.getWalletHistory(c);
            w.setTransactions(history.getTransactions());

            accountInfo.setWallet(w);
        }

        return accountInfo;
    }



    @Override
    public TickerValue getTicker(Currency currency) {

        TickerValue ticker = null;

        String uri = "BTC" + currency.name() + "/money/ticker";
        WebResource webResource = client.resource(API_BASE_URL + uri);
        ApiTickerResponse tickerResponse = simpleGetRequest(webResource, ApiTickerResponse.class);

        if(tickerResponse != null && tickerResponse.getData() != null) {
            tickerResponse.getData().setInCurrency(currency);
            tickerResponse.getData().setItemCurrency(Currency.BTC);
            ticker = tickerResponse.getData().getTickerValue();

        }

        return ticker;
    }


    public TickerValue getFastTicker(Currency currency) {

        TickerValue ticker = null;
        WebResource webResource = client.resource(API_BASE_URL + API_TICKER_FAST_EUR);
        ApiTickerResponse res =  simpleGetRequest(webResource, ApiTickerResponse.class);

        if(res != null) ticker = res.getData().getTickerValue();

        return ticker;
    }


    @Override
    public Number getLag() {

        WebResource webResource = client.resource(API_BASE_URL + API_LAG);
        ApiLagResponse lagResponse = simpleGetRequest(webResource, ApiLagResponse.class);

        if(lagResponse == null) {
            log.warn("no response from api for trading lag");
            return null;
        }

        return lagResponse.getData().getSeconds();
    }

    @Override
    protected WebResource.Builder setupProtectedResource(WebResource res) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    private ClientResponse signedRequest(String path, String data) {

        try {
            long nonce = Nonce.next()*1000;

            String payload = "nonce="+nonce;

            if(data != null && data.isEmpty() == false) {
                payload += ("&"+data);
            }

            RequestAuth auth = new RequestAuth();
            String signature = auth.hmac(path, payload, getSecret("mtgox"));

            Date requestDate = new Date();

            WebResource.Builder builder = apiBaseResource.path(path).accept("application/json");
            ClientResponse res = builder.header("Rest-Key", getUserId("mtgox"))
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


    public MtGoxWalletHistory getWalletHistory(Currency currency) {

        Date requestDate = new Date();
        ClientResponse res = signedRequest(API_GET_WALLET_HISTORY, "currency="+currency.name());
        updateApiLag(requestDate);

        if(res == null) return null;

        MtGoxWalletHistory mtGoxWalletHistory = res.getEntity(ApiWalletHistory.class).getData();

        // TODO: fix this somwhere else? remove empty/null Transaction
        Iterator<WalletTransaction> it = mtGoxWalletHistory.getTransactions().iterator();
        while(it.hasNext()) {
            WalletTransaction trans = it.next();
            if(trans.getValue() == null) it.remove();
        }

        return mtGoxWalletHistory;
    }




}
