package at.outdated.bitcoin.exchange.mtgox;

import at.outdated.bitcoin.exchange.api.OrderId;
import at.outdated.bitcoin.exchange.api.account.*;
import at.outdated.bitcoin.exchange.api.client.RestExchangeClient;
import at.outdated.bitcoin.exchange.api.currency.CurrencyAddress;
import at.outdated.bitcoin.exchange.api.market.Market;
import at.outdated.bitcoin.exchange.api.currency.Currency;
import at.outdated.bitcoin.exchange.api.currency.CurrencyValue;
import at.outdated.bitcoin.exchange.api.market.*;
import at.outdated.bitcoin.exchange.api.market.fee.SimplePercentageFee;
import at.outdated.bitcoin.exchange.mtgox.auth.Nonce;
import at.outdated.bitcoin.exchange.mtgox.auth.RequestAuth;
import org.apache.commons.lang3.StringUtils;

import javax.json.JsonObject;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.math.BigDecimal;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: ebirn
 * Date: 03.05.13
 * Time: 11:18
 * To change this template use File | Settings | File Templates.
 */
//FIXME: retro fit this class to the default class/client layout
public class MtGoxClient extends RestExchangeClient {

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

    Map<Currency,BigDecimal> multiplier = new HashMap<>();

    public MtGoxClient(Market market) {
        super(market);
        apiBaseResource = client.target(API_BASE_URL);

        multiplier.put(Currency.BTC, new BigDecimal("1.0e8", CurrencyValue.CURRENCY_MATH_CONTEXT));
        multiplier.put(Currency.USD, new BigDecimal("1.0e5", CurrencyValue.CURRENCY_MATH_CONTEXT));
        multiplier.put(Currency.EUR, new BigDecimal("1.0e5", CurrencyValue.CURRENCY_MATH_CONTEXT));

        tradeFee = new SimplePercentageFee("0.006");
    }

    @Override
    public Balance getBalance() {
        Response res = signedRequest(API_GET_INFO, "");

        if(res == null) {
            log.warn("failed to get account info");
            return null;
        }

        MtGoxAccountInfo accountInfo = res.readEntity(ApiAccountInfo.class).getData();

        // fix up wallte structure, load transaction data
        MtGoxWallets wallets = accountInfo.getWallets();

        Balance balance = new Balance(market);

        for(Currency c : wallets.getCurrencies()) {
            MtGoxWallet w = wallets.getWallet(c);

            balance.setAvailable(w.getBalance());
            balance.setOpen(w.getOpenOrders());
        }

        return balance;
    }

    @Override
    public List<WalletTransaction> getTransactions() {

        Response res = signedRequest(API_GET_INFO, "");

        if(res == null) {
            log.warn("failed to get account info");
            return null;
        }

        MtGoxAccountInfo accountInfo = res.readEntity(ApiAccountInfo.class).getData();


        List<WalletTransaction> list = new ArrayList<>();
        // fix up wallte structure, load transaction data
        MtGoxWallets wallets = accountInfo.getWallets();
        for(Currency c : wallets.getCurrencies()) {
            MtGoxWallet w = wallets.getWallet(c);

            MtGoxWalletHistory history = this.getWalletHistory(c);

            for(MtGoxWalletTransaction mtgoxTrans : history.getTransactions()) {

                WalletTransaction trans = new WalletTransaction();

                trans.setId(new OrderId(market, mtgoxTrans.getId()));
                trans.setTimestamp(mtgoxTrans.getTimestamp());
                trans.setType(mtgoxTrans.getType());

                trans.setValue(mtgoxTrans.getValue());
                trans.setBalance(mtgoxTrans.getBalance());
                trans.setInfo(mtgoxTrans.getInfo());

                list.add(trans);
            }
        }

        Collections.sort(list, new WalletTransactionTimestampComparator());

        return list;
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

    // BTCUSD/money/trades/fetch?since=1364767190000000


    @Override
    public List<MarketOrder> getTradeHistory(AssetPair asset, Date since) {

        WebTarget tradesTarget = client.target(API_BASE_URL + asset.getBase().name() + asset.getQuote().name() + "/money/trades/fetch").resolveTemplate("since", since.getTime());
        ApiTradesResponse response = simpleGetRequest(tradesTarget, ApiTradesResponse.class);

        List<MarketOrder> orders = new ArrayList<>();
        for(MtGoxTrade trade : response.getData()) {
            orders.add(trade.getOrder(market));
        }

        return orders;
    }

    public TickerValue getFastTicker(Currency currency) {

        TickerValue ticker = null;
        WebTarget webResource = client.target(API_BASE_URL + API_TICKER_FAST_EUR);
        ApiTickerResponse res =  simpleGetRequest(webResource, ApiTickerResponse.class);

        if(res != null) ticker = res.getData().getTickerValue();

        return ticker;
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
        Iterator<MtGoxWalletTransaction> it = mtGoxWalletHistory.getTransactions().iterator();
        while(it.hasNext()) {
            MtGoxWalletTransaction trans = it.next();
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
    public OrderId placeOrder(AssetPair asset, OrderType type, CurrencyValue volume, CurrencyValue price) {

        List<String> orderData = new ArrayList<>();
        orderData.add("type=" + type.name().toLowerCase());

        long volumeValue = volume.getValue().multiply(multiplier.get(volume.getCurrency())).longValue();
        long priceValue =  price.getValue().multiply(multiplier.get(price.getCurrency())).longValue();

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
                order.setType(OrderType.BID);
                break;

            case SELL:
                order.setType(OrderType.ASK);
        }


        return order;
    }


}
