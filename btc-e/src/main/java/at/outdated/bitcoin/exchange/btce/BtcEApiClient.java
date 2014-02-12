package at.outdated.bitcoin.exchange.btce;

import at.outdated.bitcoin.exchange.api.OrderId;
import at.outdated.bitcoin.exchange.api.account.Balance;
import at.outdated.bitcoin.exchange.api.account.TransactionType;
import at.outdated.bitcoin.exchange.api.account.WalletTransaction;
import at.outdated.bitcoin.exchange.api.client.RestExchangeClient;
import at.outdated.bitcoin.exchange.api.jaxb.JsonEnforcingFilter;
import at.outdated.bitcoin.exchange.api.market.Market;
import at.outdated.bitcoin.exchange.api.currency.Currency;
import at.outdated.bitcoin.exchange.api.currency.CurrencyValue;
import at.outdated.bitcoin.exchange.api.market.*;
import at.outdated.bitcoin.exchange.api.market.fee.SimplePercentageFee;
import org.apache.commons.codec.binary.Hex;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


// TODO: use fee api: https://hdbtce.kayako.com/Knowledgebase/Article/View/27/4/api-fee
//

public class BtcEApiClient extends RestExchangeClient {

    WebTarget tradeTarget, publicTarget;

    public BtcEApiClient(Market market) {
        super(market);
        client.register(JsonEnforcingFilter.class);

        tradeTarget = client.target("https://btc-e.com/tapi");
        publicTarget = client.target("https://btc-e.com/api/2/{base}_{quote}/");

        tradeFee = new SimplePercentageFee("0.002");

    }


    @Override
    public List<WalletTransaction> getTransactions() {

        List<WalletTransaction> list = new ArrayList<>();

        MultivaluedMap<String,String> data = new MultivaluedHashMap<>();
        data.add("method", "TransHistory");
        String raw = protectedPostRequest(tradeTarget, String.class, Entity.form(data));

        //log.debug("raw transactions: {}", raw);
        JsonObject transResponse = jsonFromString(raw);
        if(transResponse.getInt("success") == 1) {
            /*
            {
                "success":1,
                "return":{
                    "1081672":{
                        "type":1,
                        "amount":1.00000000,
                        "currency":"BTC",
                        "desc":"BTC Payment",
                        "status":2,
                        "timestamp":1342448420
                    }
                }
            }
            */

            //FIXME: this doesn't do anyting: account data parsing
            JsonObject transResult = transResponse.getJsonObject("return");
            for(String key : transResult.keySet()) {
                JsonObject jt = transResult.getJsonObject(key);
                Currency curr = Currency.valueOf(jt.getString("currency"));

                double volume = jt.getJsonNumber("amount").doubleValue();
                Date timestamp = new Date(jt.getJsonNumber("timestamp").longValue() * 1000L);
                String desc = jt.getString("desc");


                WalletTransaction t = new WalletTransaction();
                t.setId(new OrderId(market, key));
                t.setTimestamp(timestamp);
                t.setInfo(desc);
                t.setValue(new CurrencyValue(volume, curr));

                // FIXME IN / OUT? can volume be negative?
                t.setType(TransactionType.IN);

                list.add(t);
            }
        }else {
            log.error("failed to parse past transactions");
        }



        data = new MultivaluedHashMap<>();
        data.add("method", "TradeHistory");
        raw = protectedPostRequest(tradeTarget, String.class, Entity.form(data));
        //log.debug("raw trades: {}", raw);
        JsonObject tradeResponse = jsonFromString(raw);
        if(tradeResponse.getInt("success") == 1) {
        /*
                {
            "success":1,
            "return":{
                "166830":{
                    "pair":"btc_usd",
                    "type":"sell",
                    "amount":1,
                    "rate":1,
                    "order_id":343148,
                    "is_your_order":1,
                    "timestamp":1342445793
                }
            }
        }  */

            JsonObject jsonOrders = tradeResponse.getJsonObject("return");
            for(String key : jsonOrders.keySet()) {

                JsonObject jsonOrder = jsonOrders.getJsonObject(key);
                MarketOrder order = parseOrder(jsonOrder);

                WalletTransaction t = new WalletTransaction();

                t.setId(new OrderId(market, key));

                Date timestamp = new Date(jsonOrder.getJsonNumber("timestamp").longValue() * 1000L);
                t.setTimestamp(timestamp);

                switch(order.getType()) {
                    case ASK:
                        t.setType(TransactionType.OUT);
                        break;

                    case BID:
                        t.setType(TransactionType.IN);
                        break;
                }

                t.setValue(order.getVolume());
                t.setInfo("order: " + jsonOrder.getString("order_id", "none"));
            }

        }
        else {
            log.error("failed to parse past orders");
        }


        return list;
    }

    @Override
    public Balance getBalance() {


        MultivaluedMap<String,String> data = null;

        data = new MultivaluedHashMap<>();
        data.add("method", "getInfo");
        InfoResponse infoRes = protectedPostRequest(tradeTarget, InfoResponse.class, Entity.form(data));

        BtcEAccountInfo info = infoRes.result;

        Balance balance = new Balance(market);

        BtceFunds funds = info.funds;

        balance.setAvailable(new CurrencyValue(funds.btc, Currency.BTC));
        balance.setAvailable(new CurrencyValue(funds.eur, Currency.EUR));
        balance.setAvailable(new CurrencyValue(funds.ftc, Currency.FTC));
        balance.setAvailable(new CurrencyValue(funds.ltc, Currency.LTC));
        balance.setAvailable(new CurrencyValue(funds.nmc, Currency.NMC));
        balance.setAvailable(new CurrencyValue(funds.nvc, Currency.NVC));
        balance.setAvailable(new CurrencyValue(funds.ppc, Currency.PPC));
        //balance.setAvailable(new CurrencyValue(funds.rur, Currency.RUR));
        //balance.setAvailable(new CurrencyValue(funds.trc, Currency.));
        balance.setAvailable(new CurrencyValue(funds.usd, Currency.USD));

        return balance;
    }

    @Override
    public MarketDepth getMarketDepth(AssetPair asset) {
        Currency base = asset.getBase();
        Currency quote = asset.getQuote();

        // (price, volume)

        WebTarget resource = publicTarget.path("/depth")
            .resolveTemplate("base", base.name().toLowerCase())
            .resolveTemplate("quote", quote.name().toLowerCase());

        String response = super.simpleGetRequest(resource, String.class);

        JsonReader reader = Json.createReader(new StringReader(response));

        JsonObject root = reader.readObject();
        JsonArray asksArr = root.getJsonArray("asks");
        JsonArray bidsArr = root.getJsonArray("bids");

        MarketDepth depth = new MarketDepth(asset);

        for(int i=0; i<asksArr.size(); i++ ) {
            double price = asksArr.getJsonArray(i).getJsonNumber(0).doubleValue();
            double volume = asksArr.getJsonArray(i).getJsonNumber(1).doubleValue();

            depth.addAsk(volume, price);
        }
        for(int i=0; i<bidsArr.size(); i++ ) {
            double price = bidsArr.getJsonArray(i).getJsonNumber(0).doubleValue();
            double volume = bidsArr.getJsonArray(i).getJsonNumber(1).doubleValue();

            depth.addBid(volume, price);
        }

        return depth;
    }

    @Override
    public TickerValue getTicker(AssetPair asset) {

        // https://btc-e.com/api/2/btc_usd/ticker

        WebTarget tickerResource = publicTarget.path("/ticker")
            .resolveTemplate("base", asset.getBase().name().toLowerCase())
            .resolveTemplate("quote", asset.getQuote().name().toLowerCase());

        TickerResponse response = simpleGetRequest(tickerResource, TickerResponse.class);

        BtcETickerValue btcETickerValue = response.getTicker();

        TickerValue value = btcETickerValue.getTickerValue();
        value.setAsset(asset);

        return value;
    }

    @Override
    public List<MarketOrder> getTradeHistory(AssetPair asset, Date since) {

        // https://btc-e.com/api/2/btc_usd/trades

        WebTarget tgt = publicTarget.path("/trades")
                .resolveTemplate("base", asset.getBase().name().toLowerCase())
                .resolveTemplate("quote", asset.getQuote().name().toLowerCase());

        /*
        [{"date":1391940472,"price":701.39,"amount":0.01,"tid":29283411,"price_currency":"USD","item":"BTC","trade_type":"ask"},
        {"date":1391940441,"price":698.213,"amount":0.13,"tid":29283401,"price_currency":"USD","item":"BTC","trade_type":"ask"},{
         */

        GenericType<List<BtceTrade>> tradeList = new GenericType<List<BtceTrade>>() {};

        List<BtceTrade> trades = tgt.request().get(tradeList);

        if(trades == null) {
            log.error("failed to lead past trades");
            return null;
        }

        List<MarketOrder> history = new ArrayList<>();
        for(BtceTrade t : trades) {
            if(since.before(t.date)) {
                history.add(t.getOrder(market));
            }
        }

        return history;
    }

    @Override
    protected <T> Invocation.Builder setupProtectedResource(WebTarget tgt, Entity<T> entity) {

        String apiKey = getUserId();
        String apiSecret = getSecret();

        Invocation.Builder builder = null;

        try {
            Mac mac = Mac.getInstance("HmacSHA512");
            SecretKeySpec secret_spec = new SecretKeySpec(apiSecret.getBytes("UTF-8"), "HmacSHA512");
            mac.init(secret_spec);
            //    1.381.050.637
            long nonce = (new Date()).getTime()/1000L; //max:2.147.483.647

            Entity<Form> e = (Entity<Form>) entity;
            e.getEntity().param("nonce", Long.toString(nonce));

            String payload = formData2String(e.getEntity());
            //log.debug("encoded payload: {}", payload);

            byte[] rawSignature = mac.doFinal(payload.getBytes("UTF-8"));
            String signature = new String(Hex.encodeHex(rawSignature));

            builder = tgt.request();

            builder.header("Key", apiKey);
            //log.debug("Key: {}", apiKey);

            builder.header("Sign", signature);
            //log.debug("Sign: {}", signature);
        }
        catch (Exception e) {
            log.error("error: {}", e);
        }

        return builder;
    }


    @Override
    public boolean cancelOrder(OrderId order) {
        // CancelOrder

        Form data = new Form();

        data.param("method", "CancelOrder");
        data.param("order_id", order.getIdentifier());

        String raw = protectedPostRequest(tradeTarget, String.class, Entity.form(data));

        JsonObject jsonResponse = jsonFromString(raw);

        if(jsonResponse.getInt("success") == 0) {
            log.error("failed to cancel order: {}", order.getIdentifier());
            return false;
        }


        int cancelledId = jsonResponse.getJsonObject("return").getInt("order_id");
        if(cancelledId == Integer.parseInt(order.getIdentifier())) {
            return true;
        }

        log.error("failed to cancel order: {} (unknown)", order.getIdentifier());

        return false;
    }

    @Override
    public OrderId placeOrder(AssetPair asset, OrderType type, CurrencyValue volume, CurrencyValue price) {

        // method: Trade

        Form data = new Form();
        data.param("method", "Trade");


        String pairStr = asset.getBase().name().toLowerCase() + "_" + asset.getQuote().name().toLowerCase();

        data.param("pair", pairStr); // btc_usd

        switch(type) {
            case ASK:
                data.param("type", "sell");
                break;

            case BID:
                data.param("type", " buy");
                break;
        }



        data.param("rate", price.valueToString());
        data.param("amount", volume.valueToString());

        String raw = protectedPostRequest(tradeTarget, String.class, Entity.form(data));

        /*
            {
        "success":1,
            "return":{
                "received":0.1,
                "remains":0,
                "order_id":0,
                "funds":{
                    "usd":325,
                    "btc":2.498,
                    "sc":121.998,
                    "ltc":0,
                    "ruc":0,
                    "nmc":0
                }
            }
        }
     */
        JsonObject jsonOrderResult = jsonFromString(raw);

        if(jsonOrderResult.getInt("success") == 0) {
            log.error("failed to place order");
            return null;
        }

        return new OrderId(market, jsonOrderResult.getJsonObject("return").getInt("order_id") + "");
    }

    @Override
    public List<MarketOrder> getOpenOrders() {

        // ActiveOrders

        /*
        {
            "success":1,
            "return":{
                "343152":{
                    "pair":"btc_usd",
                    "type":"sell",
                    "amount":1.00000000,
                    "rate":3.00000000,
                    "timestamp_created":1342448420,
                    "status":0
                }
            }
            }
        */
        Form data = new Form();
        data.param("method", "ActiveOrders");
        //data.param("pair", "CancelOrder"); // btc_usd

        String raw = protectedPostRequest(tradeTarget, String.class, Entity.form(data));


        JsonObject jsonResult = jsonFromString(raw);

        List<MarketOrder> orders = new ArrayList<>();

        if(jsonResult.getInt("success") == 0) {

            // empty list is returned as error "no orders" - stupid!
            if(!jsonResult.getString("error", "").equalsIgnoreCase("no orders")) {
                log.error("failed to get active orders");
                orders = null;
            }

        }
        else {
            JsonObject jsonOrders = jsonResult.getJsonObject("return");
            for(String key : jsonOrders.keySet()) {

                MarketOrder order = parseOrder(jsonOrders.getJsonObject(key));
                order.setId(new OrderId(market, key));

                orders.add(order);
            }
        }
        return orders;
    }


    private MarketOrder parseOrder(JsonObject jsonOrder) {
        /*
        		"343152":{
			"pair":"btc_usd",
			"type":"sell",
			"amount":1.00000000,
			"rate":3.00000000,
			"timestamp_created":1342448420,
			"status":0
		}
         */
        MarketOrder order = new MarketOrder();

        String[] parts = jsonOrder.getString("pair").split("_");

        Currency left = Currency.valueOf(parts[0].toUpperCase());
        Currency right = Currency.valueOf(parts[1].toUpperCase());

        AssetPair asset = market.getAsset(left, right);
        order.setAsset(asset);

        order.setType(OrderType.parse(jsonOrder.getString("type")));

        double price = jsonOrder.getJsonNumber("rate").doubleValue();
        order.setPrice(new CurrencyValue(price, right));


        double volume = jsonOrder.getJsonNumber("amount").doubleValue();
        order.setVolume(new CurrencyValue(volume, left));

        return order;
    }

}
