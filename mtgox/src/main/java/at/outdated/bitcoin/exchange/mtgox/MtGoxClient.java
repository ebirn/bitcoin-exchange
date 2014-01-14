package at.outdated.bitcoin.exchange.mtgox;

import at.outdated.bitcoin.exchange.api.OrderId;
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
import org.apache.commons.lang3.StringUtils;

import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonValue;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.*;

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

    Map<Currency,Double> multiplier = new HashMap<>();

    public MtGoxClient(Market market) {
        super(market);
        client = ClientBuilder.newBuilder().register(MtGoxJSONResolver.class).build();
        apiBaseResource = client.target(API_BASE_URL);


        multiplier.put(Currency.BTC, 1.0e8);
        multiplier.put(Currency.USD, 1.0e5);
        multiplier.put(Currency.EUR, 1.0e5);
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

        for(DepthEntry ask : rawDepth.getAsks()) {
            depth.addAsk(ask.amount, ask.price);
        }

        for(DepthEntry bid : rawDepth.getBids()) {
            depth.addBid(bid.amount, bid.price);
        }

        sortDepth(depth);

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
    public boolean cancelOrder(OrderId order) {

        // BTCUSD/MONEY/ORDER/cancel


        Response res = signedRequest("BTCUSD/money/order/cancel", "oid="+order.getIdentifier());

        if(res == null) {
            log.error("failed to cancel order {} - request failed.", order.getIdentifier());
            return false;
        }

        OrderDeletionResult result = res.readEntity(OrderDeletionResult.class);

        if(result.getResult().equalsIgnoreCase("success") &&
                result.getData().getOid().equalsIgnoreCase(order.getIdentifier())) {
            return true;
        }

        log.error("failed to cancel order {}", order.getIdentifier());
        return false;
    }

    @Override
    public OrderId placeOrder(AssetPair asset, TradeDecision decision, CurrencyValue volume, CurrencyValue price) {

        String type = null;
        switch(decision) {
            case BUY:
                type = "bid";
                break;

            case SELL:
                type = "ask";
                break;
        }

        List<String> orderData = new ArrayList<>();
        orderData.add("type=" + type);

        long volumeValue = Math.round(volume.getValue() * multiplier.get(volume.getCurrency()));
        long priceValue =  Math.round(price.getValue() * multiplier.get(price.getCurrency()));

        orderData.add("amount_int=" + Long.toString( volumeValue ));
        orderData.add("price_int=" + Long.toString( priceValue ));

        Response res = signedRequest("BTCUSD/MONEY/ORDER/ADD", StringUtils.join(orderData, "&"));
        MtgoxOrderPlaced placed = res.readEntity(MtgoxOrderPlaced.class);


        if(placed.getResult().equalsIgnoreCase("success")) {
            return new OrderId(market, placed.getData());
        }
/*
        type    "bid" or ask
        amount_int  amount of BTC to buy or sell, as an integer
        price_int   The price per bitcoin in the auxiliary currency, as an integer, optional if you wish to trade at the market price
  */
        log.error("failed to place order");
        return null;
    }

    @Override
    public List<MarketOrder> getOpenOrders() {

        // this is stupid: the BTCUSD part doesn't do anything
        Response res = signedRequest("BTCUSD/money/orders", "");

        MtGoxOrderList orderList = res.readEntity(MtGoxOrderList.class);
        List<MarketOrder> orders = new ArrayList<>();

        for(MtGoxOrder mtGoxOrder : orderList.getData()) {
            orders.add(converOrder(mtGoxOrder));
        }

        return orders;
    }

    @Override
    protected CurrencyAddress lookupUpDepositAddress(Currency curr) {


        String accountId = getPropertyString("accountid");

        String raw = simpleGetRequest(client.target("https://data.mtgox.com/api/2/BTCUSD/money/bitcoin/get_address").queryParam("account", accountId), String.class);

        JsonObject jo = jsonFromString(raw);

        return new CurrencyAddress(curr, jo.getString("data"));
    }


    MarketOrder converOrder(MtGoxOrder mtGoxOrder) {

        MarketOrder order = new MarketOrder();
        order.setId(new OrderId(market, mtGoxOrder.getOid()));

        CurrencyValue volume = mtGoxOrder.getAmount();
        order.setVolume(mtGoxOrder.getAmount());

        CurrencyValue price = mtGoxOrder.getPrice();
        order.setPrice(mtGoxOrder.getPrice());

        AssetPair asset = market.getAsset(volume.getCurrency(), price.getCurrency());

        order.setAsset(asset);

        switch(mtGoxOrder.getType()) {
            case BUY:
                order.setDecision(TradeDecision.BUY);
                break;

            case SELL:
                order.setDecision(TradeDecision.SELL);
        }


        return order;
    }


}
