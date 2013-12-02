package at.outdated.bitcoin.exchange.mtgox;

import at.outdated.bitcoin.exchange.api.client.ExchangeApiClient;
import at.outdated.bitcoin.exchange.api.currency.CurrencyAddress;
import at.outdated.bitcoin.exchange.api.market.Market;
import at.outdated.bitcoin.exchange.api.account.AccountInfo;
import at.outdated.bitcoin.exchange.api.account.Wallet;
import at.outdated.bitcoin.exchange.api.account.WalletTransaction;
import at.outdated.bitcoin.exchange.api.currency.Currency;
import at.outdated.bitcoin.exchange.api.currency.CurrencyValue;
import at.outdated.bitcoin.exchange.api.market.*;
import at.outdated.bitcoin.exchange.mtgox.auth.Nonce;
import at.outdated.bitcoin.exchange.mtgox.auth.RequestAuth;

import javax.json.JsonObject;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.Date;
import java.util.Iterator;

/**
 * Created with IntelliJ IDEA.
 * User: ebirn
 * Date: 03.05.13
 * Time: 11:18
 * To change this template use File | Settings | File Templates.
 */
//FIXME: retro fit this class to the default class/client layout
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

    private WebTarget apiBaseResource;

    public MtGoxClient(Market market) {
        super(market);
        client = ClientBuilder.newBuilder().register(MtGoxJSONResolver.class).build();
        apiBaseResource = client.target(API_BASE_URL);
    }


    @Override
    public AccountInfo getAccountInfo() {


        Response res = signedRequest(API_GET_INFO, "");

        if(res == null) {
            log.warn("failed to get account info");
            return null;
        }

        MtGoxAccountInfo accountInfo = res.readEntity(ApiAccountInfo.class).getData();

        // fix up wallte structure, load transaction data
        MtGoxWallets wallets = accountInfo.getWallets();
        for(Currency c : wallets.getCurrencies()) {
            Wallet w = wallets.getWallet(c);

            MtGoxWalletHistory history = this.getWalletHistory(c);
            w.setTransactions(history.getTransactions());

            accountInfo.addWallet(w);
        }

        return accountInfo;
    }


    @Override
    public MarketDepth getMarketDepth(AssetPair asset) {


        WebTarget depthTarget = client.target(API_BASE_URL + asset.getBase().name() + asset.getQuote().name() + "/money/depth/fetch");

        //String raw = simpleGetRequest(depthTarget, String.class);

        ApiDepthResponse res = simpleGetRequest(depthTarget, ApiDepthResponse.class);

        DepthResponse rawDepth = res.data;

        MarketDepth depth = new MarketDepth(asset);

        for(DepthEntry e : rawDepth.getAsks()) {
            depth.addAsk(e.amount, e.price);
        }

        for(DepthEntry e : rawDepth.getBids()) {
            depth.addAsk(e.amount, e.price);
        }

        return depth;
    }

    @Override
    public TickerValue getTicker(AssetPair asset) {

        TickerValue ticker = null;

        String uri = asset.getBase().name() + asset.getQuote().name() + "/money/ticker";
        WebTarget webResource = client.target(API_BASE_URL + uri);
        ApiTickerResponse tickerResponse = simpleGetRequest(webResource, ApiTickerResponse.class);

        if(tickerResponse != null && tickerResponse.getData() != null) {
            tickerResponse.getData().setInCurrency(asset.getQuote());
            tickerResponse.getData().setItemCurrency(asset.getBase());
            ticker = tickerResponse.getData().getTickerValue();
        }



        return ticker;
    }


    public TickerValue getFastTicker(Currency currency) {

        TickerValue ticker = null;
        WebTarget webResource = client.target(API_BASE_URL + API_TICKER_FAST_EUR);
        ApiTickerResponse res =  simpleGetRequest(webResource, ApiTickerResponse.class);

        if(res != null) ticker = res.getData().getTickerValue();

        return ticker;
    }


    @Override
    public Number getLag() {

        WebTarget webResource = client.target(API_BASE_URL + API_LAG);
        ApiLagResponse lagResponse = simpleGetRequest(webResource, ApiLagResponse.class);

        if(lagResponse == null) {
            log.warn("no response from api for trading lag");
            return null;
        }

        return lagResponse.getData().getSeconds();
    }

    //FIXME: actually use that
    @Override
    protected <T> Invocation.Builder setupProtectedResource(WebTarget res, Entity<T> entity) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    private Response signedRequest(String path, String data) {

        try {
            long nonce = Nonce.next()*1000;

            String payload = "nonce="+nonce;

            if(data != null && data.isEmpty() == false) {
                payload += ("&"+data);
            }

            RequestAuth auth = new RequestAuth();
            String signature = auth.hmac(path, payload, getSecret());

            Date requestDate = new Date();

            Invocation.Builder builder = apiBaseResource.path(path).request("application/json");
            Response res = builder.header("Rest-Key", getUserId())
                                        .header("Rest-Sign", signature)
                                        .post(Entity.entity(payload, MediaType.APPLICATION_FORM_URLENCODED_TYPE));



            updateApiLag(requestDate);

            if(res.getStatusInfo().getFamily() == Response.Status.Family.SUCCESSFUL)
                return res;
        }
        //catch(UniformInterfaceException  e) {
        //    handleApiError(e);
        //}
        catch(Exception e) {
            e.printStackTrace();
        }

        return null;
    }


    public MtGoxWalletHistory getWalletHistory(Currency currency) {

        Date requestDate = new Date();
        Response res = signedRequest(API_GET_WALLET_HISTORY, "currency="+currency.name());
        updateApiLag(requestDate);

        if(res == null) return null;

        MtGoxWalletHistory mtGoxWalletHistory = res.readEntity(ApiWalletHistory.class).getData();

        // TODO: fix this somwhere else? remove empty/null Transaction
        Iterator<WalletTransaction> it = mtGoxWalletHistory.getTransactions().iterator();
        while(it.hasNext()) {
            WalletTransaction trans = it.next();
            if(trans.getValue() == null) it.remove();
        }

        return mtGoxWalletHistory;
    }


    @Override
    protected CurrencyAddress lookupUpDepositAddress(Currency curr) {


        String accountId = getPropertyString("accountid");

        String raw = simpleGetRequest(client.target("https://data.mtgox.com/api/2/BTCUSD/money/bitcoin/get_address").queryParam("account", accountId), String.class);

        JsonObject jo = jsonFromString(raw);

        return new CurrencyAddress(curr, jo.getString("data"));
    }
}
