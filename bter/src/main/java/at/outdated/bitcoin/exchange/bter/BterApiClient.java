package at.outdated.bitcoin.exchange.bter;

import at.outdated.bitcoin.exchange.api.OrderId;
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

    public BterApiClient(Market market) {
        super(market);

        tradeFee = new SimplePercentageFee("0.002");
    }

    @Override
    public AccountInfo getAccountInfo() {


        WebTarget fundsTarget = client.target("https://bter.com/api/1/private/getfunds");


        String rawFunds = protectedPostRequest(fundsTarget, String.class, Entity.form(new Form()));

        JsonObject jsonFunds = jsonFromString(rawFunds);

        BterAccountInfo info = new BterAccountInfo();


        if(jsonFunds.get("available_funds").getValueType() == JsonValue.ValueType.OBJECT) {
            JsonObject funds = jsonFunds.getJsonObject("available_funds");
            JsonObject lockedFunds = jsonFunds.getJsonObject("locked_funds");
            for(String key : funds.keySet()) {

                try {
                    Currency c = Currency.valueOf(key);

                    Wallet w = new Wallet(c);
                    w.setBalance(new CurrencyValue(Double.valueOf(funds.getString(key)), c));

                    if(lockedFunds != null && lockedFunds.containsKey(key)) {
                        w.setOpenOrders(new CurrencyValue(Double.valueOf(lockedFunds.getString(key)), c));
                    }

                    info.addWallet(w);
                }
                catch(Exception e) {
                    log.info("Currency '{}' unhandled, cannot create wallet", key);
                }
            }
        }
        else {
            log.info("no funds available");
        }


        return info;
    }

    @Override
    public TickerValue getTicker(AssetPair asset) {

        WebTarget tickerTgt = client.target("https://bter.com/api/1/ticker/" + asset.getBase().name().toLowerCase() + "_" + asset.getQuote().name().toLowerCase());

        BterTicker ticker = simpleGetRequest(tickerTgt, BterTicker.class);

        TickerValue tickerValue = ticker.getValue();

        tickerValue.setAsset(asset);

        return tickerValue;
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
