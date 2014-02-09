package at.outdated.bitcoin.exchange.bter;

import at.outdated.bitcoin.exchange.api.OrderId;
import at.outdated.bitcoin.exchange.api.account.Balance;
import at.outdated.bitcoin.exchange.api.account.WalletTransaction;
import at.outdated.bitcoin.exchange.api.client.RestExchangeClient;
import at.outdated.bitcoin.exchange.api.market.*;
import at.outdated.bitcoin.exchange.api.account.AccountInfo;
import at.outdated.bitcoin.exchange.api.account.Wallet;
import at.outdated.bitcoin.exchange.api.currency.Currency;
import at.outdated.bitcoin.exchange.api.currency.CurrencyValue;
import at.outdated.bitcoin.exchange.api.market.fee.SimplePercentageFee;
import org.apache.commons.codec.binary.Hex;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonValue;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Form;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.*;

/**
 * Created by ebirn on 11.10.13.
 */
public class BterApiClient extends RestExchangeClient {

    WebTarget baseTarget, privateTarget;

    public BterApiClient(Market market) {
        super(market);

        baseTarget = client.target("https://bter.com/api/1/");
        privateTarget = baseTarget.path("/private/");

        tradeFee = new SimplePercentageFee("0.002");
    }

    @Override
    public List<WalletTransaction> getTransactions() {
        log.warn("transaction list not implemented");
        return new ArrayList<>();
    }

    @Override
    public Balance getBalance() {
        WebTarget fundsTarget = privateTarget.path("/getfunds");


        String rawFunds = protectedPostRequest(fundsTarget, String.class, Entity.form(new Form()));

        JsonObject jsonFunds = jsonFromString(rawFunds);

        Balance balance = new Balance(market);

        if(jsonFunds.get("available_funds").getValueType() == JsonValue.ValueType.OBJECT) {
            JsonObject funds = jsonFunds.getJsonObject("available_funds");
            JsonObject lockedFunds = jsonFunds.getJsonObject("locked_funds");
            for(String key : funds.keySet()) {

                try {
                    Currency c = Currency.valueOf(key);

                    balance.setAvailable(new CurrencyValue(new BigDecimal(funds.getString(key), CurrencyValue.CURRENCY_MATH_CONTEXT), c));

                    if(lockedFunds != null && lockedFunds.containsKey(key)) {
                        balance.setOpen(new CurrencyValue(new BigDecimal(lockedFunds.getString(key), CurrencyValue.CURRENCY_MATH_CONTEXT), c));
                    }
                }
                catch(Exception e) {
                    log.info("Currency '{}' unhandled, cannot read balance", key);
                }
            }
        }
        else {
            log.error("no funds available");
            return null;
        }


        return balance;
    }

    @Override
    public TickerValue getTicker(AssetPair asset) {

        WebTarget tickerTgt = baseTarget.path("/ticker/{base}_{quote}")
                .resolveTemplate("base", asset.getBase().name().toLowerCase())
                .resolveTemplate("quote", asset.getQuote().name().toLowerCase());


        BterTicker ticker = simpleGetRequest(tickerTgt, BterTicker.class);

        TickerValue tickerValue = ticker.getValue();

        tickerValue.setAsset(asset);

        return tickerValue;
    }

    @Override
    public List<MarketOrder> getTradeHistory(AssetPair asset, Date since) {
        // http://data.bter.com/api/1/trade/aaa_bbb

        WebTarget tgt = baseTarget.path("/trade/{base}_{quote}")
                .resolveTemplate("base", asset.getBase().name().toLowerCase())
                .resolveTemplate("quote", asset.getQuote().name().toLowerCase());

        // {"result":"true","data":[
        // {"date":"1391940727","price":4576,"amount":0.1962,"tid":"4422029","type":"buy"},
        // {"date":"1391940732","price":4578,"amount":0.1095,"tid":"4422033","type":"buy"},
        // ...

        BterTradeHistory result = simpleGetRequest(tgt, BterTradeHistory.class);

        List<MarketOrder> history = null;
        if(result != null && result.isSuccess()) {

            history = new ArrayList<>();
            for(BterTrade bt : result.getData()) {
                if(since.before(bt.date)) {
                    history.add(bt.getOrder(market, asset));
                }
            }
        }
        else {
            log.error("failed to load trade history");
        }

        return history;
    }

    @Override
    public Number getLag() {
        return 0.0;
    }

    @Override
    public MarketDepth getMarketDepth(AssetPair asset) {


        String base = asset.getBase().name().toLowerCase();
        String quote = asset.getQuote().name().toLowerCase();

        WebTarget depthTarget = client.target("https://bter.com/api/1/depth/" + base + "_" + quote);

        String rawDepth = depthTarget.request().get(String.class);

        JsonObject jsonDepth = jsonFromString(rawDepth);

        double[][] asks = parseNestedArray(jsonDepth.getJsonArray("asks"));
        double[][] bids = parseNestedArray(jsonDepth.getJsonArray("bids"));


        MarketDepth depth = new MarketDepth(asset);

        for(double[] ask : asks) {
            depth.addAsk(ask[1], ask[0]);
        }

        for(double[] bid : bids) {
            depth.addBid(bid[1], bid[0]);
        }

        return depth;
    }

    @Override
    protected <T> Invocation.Builder setupProtectedResource(WebTarget res, Entity<T> entity) {

        long nonce = ((new Date()).getTime());

        Form form = ((Entity<Form>) entity).getEntity();
        form.param("nonce", Long.toString(nonce));

        String hexSignature = null;
        try {
            Mac mac = Mac.getInstance("HmacSHA512");
            SecretKeySpec secret_spec = new SecretKeySpec(getSecret().getBytes("UTF-8"), "HmacSHA512");
            mac.init(secret_spec);
            mac.update(formData2String(form).getBytes());

            hexSignature = Hex.encodeHexString(mac.doFinal()).toLowerCase();
        }
        catch(Exception e) {
            log.error("failed to setup hashing");
        }

        Invocation.Builder builder = res.request();

        builder.header("KEY", getUserId());
        builder.header("SIGN", hexSignature);

        return builder;
    }


    @Override
    public boolean cancelOrder(OrderId order) {

        // https://bter.com/api/1/private/cancelorder

        // param: order_id
        WebTarget tgt = client.target("https://bter.com/api/1/private/cancelorder");

        Form form = new Form();

        form.param("order_id", order.getIdentifier());

        Entity e = Entity.form(form);

        String rawCancel = setupProtectedResource(tgt, e).post(e).readEntity(String.class);

        JsonObject jsonCancel = jsonFromString(rawCancel);

        if(jsonCancel.getBoolean("result")) {
            return true;
        }

        log.info("failed to cancel order: {}", jsonCancel.getString("msg"));

        return false;
    }

    @Override
    public OrderId placeOrder(AssetPair asset, OrderType type, CurrencyValue volume, CurrencyValue price) {

        WebTarget tgt = client.target("https://bter.com/api/1/private/cancelorder");

        Form form = new Form();

        String pairStr = asset.getBase().name().toLowerCase() + "_" + asset.getQuote().name().toLowerCase();

        switch(type) {
            case BID:
                form.param("type", "BUY"); // sell / buy"BUY";
                break;

            case ASK:
                form.param("type", "SELL"); // sell / buy"BUY";
                break;
        }


        form.param("pair", pairStr); // aaa_bbb

        form.param("rate", price.valueToString()); // price
        form.param("amount", volume.valueToString());  // volume

        Entity e = Entity.form(form);

        String rawOrder = setupProtectedResource(tgt, e).post(e).readEntity(String.class);


        JsonObject jsonOrder = jsonFromString(rawOrder);

        if(jsonOrder.getString("result").equalsIgnoreCase("true")) {
            return new OrderId(market, jsonOrder.getString("order_id"));
        }

        log.error("failed to place order: {}", jsonOrder.getString("msg"));

        return null;
    }

    @Override
    public List<MarketOrder> getOpenOrders() {

        // https://bter.com/api/1/private/orderlist

/*
	{
		"result":true,
		"orders":[
			{
				"id":"15088",
				"sell_type":"BTC",
				"buy_type":"LTC",
				"sell_amount":"0.39901357",
				"buy_amount":"12.0"
			},
			{
				"id":"15092",
				"sell_type":"LTC",
				"buy_type":"BTC",
				"sell_amount":"13.0",
				"buy_amount":"0.4210"
			}
			]
		"msg":"Success"
	}
 */

        WebTarget tgt = client.target("https://bter.com/api/1/private/orderlist");

        Entity e = Entity.form(new Form());

        String rawOpenOrders = setupProtectedResource(tgt, e).post(e).readEntity(String.class);

        JsonObject jsonOpenOrders = jsonFromString(rawOpenOrders);

        if(jsonOpenOrders.getBoolean("result") == false) {

            log.warn("failed to list orders: {}", jsonOpenOrders.getString("msg"));

            return new ArrayList<>();
        }

        List<MarketOrder> openOrders = new ArrayList<>();


        JsonArray ordersArray = jsonOpenOrders.getJsonArray("orders");
        for(int i=0; i< ordersArray.size(); i++) {
            JsonObject jsonOrder = ordersArray.getJsonObject(i);

            openOrders.add(parseOrder(jsonOrder));

        }

        return openOrders;
    }


    private MarketOrder parseOrder(JsonObject jsonOrder) {
        MarketOrder order = new MarketOrder();


        Currency sellCurr = Currency.valueOf(jsonOrder.getString("sell_type"));
        BigDecimal sellAmount = new BigDecimal(jsonOrder.getString("sell_amount"));

        Currency buyCurr = Currency.valueOf(jsonOrder.getString("buy_type"));
        BigDecimal buyAmount = new BigDecimal(jsonOrder.getString("buy_amount"));

        AssetPair asset = market.getAsset(sellCurr, buyCurr);

        order.setId(new OrderId(market, jsonOrder.getString("id")));
        order.setAsset(asset);

        BigDecimal price = buyAmount.divide(sellAmount);

        order.setPrice(new CurrencyValue(price, buyCurr));
        order.setVolume(new CurrencyValue(sellAmount, sellCurr));


        OrderType type = null;

        if(sellCurr == asset.getBase() && buyCurr == asset.getQuote()) {
            type = OrderType.ASK;
        }
        else if(buyCurr == asset.getBase() && sellCurr == asset.getQuote()){
            type = OrderType.BID;
        }
        else {
            type = OrderType.UNDEF;
        }

        order.setType(type);


        return order;
    }
}
