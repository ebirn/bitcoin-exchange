package at.outdated.bitcoin.exchange.cryptsy;

import at.outdated.bitcoin.exchange.api.OrderId;
import at.outdated.bitcoin.exchange.api.account.AccountInfo;
import at.outdated.bitcoin.exchange.api.account.Balance;
import at.outdated.bitcoin.exchange.api.client.RestExchangeClient;
import at.outdated.bitcoin.exchange.api.client.MarketClient;
import at.outdated.bitcoin.exchange.api.client.TradeClient;
import at.outdated.bitcoin.exchange.api.currency.Currency;
import at.outdated.bitcoin.exchange.api.currency.CurrencyValue;
import at.outdated.bitcoin.exchange.api.market.*;
import at.outdated.bitcoin.exchange.api.market.fee.Fee;
import at.outdated.bitcoin.exchange.api.market.fee.SimplePercentageFee;
import org.apache.commons.codec.binary.Hex;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Form;
import java.math.BigDecimal;
import java.nio.channels.spi.AbstractSelectionKey;
import java.text.NumberFormat;
import java.util.*;

/**
 * Created by ebirn on 06.01.14.
 */
public class CryptsyApiClient extends RestExchangeClient implements MarketClient, TradeClient {

    Map<AssetPair,Integer> marketId = new HashMap<>();

    WebTarget tradeBase = client.target("https://www.cryptsy.com/api");
    WebTarget publicBase = client.target("http://pubapi.cryptsy.com/api.php");

    Fee buyFee, sellFee;

    public CryptsyApiClient(Market market) {
        super(market);

        setMarketNum(Currency.LTC, Currency.BTC, 3); //3
        setMarketNum(Currency.NVC, Currency.BTC, 13); // 13
        setMarketNum(Currency.NMC, Currency.BTC, 29); // 29
        setMarketNum(Currency.PPC, Currency.BTC, 28); // 28
        setMarketNum(Currency.QRK, Currency.BTC, 71); // 71

        /*******************/

        setMarketNum(Currency.PPC, Currency.LTC, 125); // 125
        setMarketNum(Currency.QRK, Currency.LTC, 126); // 126

        buyFee = new SimplePercentageFee("0.002");
        sellFee = new SimplePercentageFee("0.003");

    }

    private void setMarketNum(Currency base, Currency quote, int marketNum) {
        AssetPair asset = market.getAsset(base, quote);
        marketId.put(asset, marketNum);
    }

    @Override
    public Number getLag() {
        return 0.1234;
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

        builder.header("Key", getUserId());
        builder.header("Sign", hexSignature);

        return builder;
    }

    @Override
    public TickerValue getTicker(AssetPair asset) {

        int marketNum = marketId.get(asset);

        try {
            WebTarget tgt = publicBase.queryParam("method", "singlemarketdata").queryParam("marketid", marketNum);

            String raw = simpleGetRequest(tgt, String.class);
            JsonObject root = jsonFromString(raw);

            JsonObject jsonMarket = root.getJsonObject("return").getJsonObject("markets").getJsonObject(asset.getBase().name());

            JsonArray jsonSells = jsonMarket.getJsonArray("sellorders");

            JsonArray jsonBuys = jsonMarket.getJsonArray("buyorders");
            double bid = Double.parseDouble(jsonBuys.getJsonObject(0).getString("price"));
            double ask = Double.parseDouble(jsonSells.getJsonObject(0).getString("price"));


            double last = Double.parseDouble(jsonMarket.getString("lasttradeprice"));
            double volume = Double.parseDouble(jsonMarket.getString("volume"));


            TickerValue ticker = new TickerValue(asset);
            ticker.setLast(last);
            ticker.setVolume(volume);
            ticker.setBid(bid);
            ticker.setAsk(ask);

            return ticker;
        }
        catch(Exception e) {
            log.info("failed to load ticker for {}", asset);
        }

        return null;
    }

    @Override
    public MarketDepth getMarketDepth(AssetPair asset) {

        int marketNum = marketId.get(asset);
        // http://pubapi.cryptsy.com/api.php?method=singleorderdata&marketid=
        WebTarget tgt = publicBase.queryParam("method", "singleorderdata").queryParam("marketid", marketNum);

        String raw = simpleGetRequest(tgt, String.class);
        try {
            JsonObject root = jsonFromString(raw);

            JsonObject jsonDepth = root.getJsonObject("return").getJsonObject(asset.getBase().name());

            MarketDepth depth = new MarketDepth(asset);

            // ask = sell (first: lowest ask)
            JsonArray jsonSells = jsonDepth.getJsonArray("sellorders");
            for(int i=0; i<jsonSells.size(); i++) {
                JsonObject obj = jsonSells.getJsonObject(i);
                double price = Double.parseDouble(obj.getString("price"));
                double volume = Double.parseDouble(obj.getString("quantity"));

                depth.addAsk(volume, price);
            }

            // bid = buy (first: higest bid)
            JsonArray jsonBuys = jsonDepth.getJsonArray("buyorders");
            for(int i=0; i<jsonBuys.size(); i++) {
                JsonObject obj = jsonBuys.getJsonObject(i);
                double price = Double.parseDouble(obj.getString("price"));
                double volume = Double.parseDouble(obj.getString("quantity"));

                depth.addBid(volume, price);
            }

            return depth;
        }
        catch(Exception e) {
            log.error("failed to parse market depth {}", asset);
            log.error("", e);
        }

        return null;
    }

    @Override
    public AccountInfo getAccountInfo() {
        return new CryptsyAccountInfo();
    }

    @Override
    public Balance getBalance() {
        Form form = new Form();
        form.param("method", "getinfo");

        String raw = protectedPostRequest(tradeBase, String.class, Entity.form(form));

        /*
        balances_available	Array of currencies and the balances availalbe for each
        balances_hold	Array of currencies and the amounts currently on hold for open orders
        servertimestamp	Current server timestamp
        servertimezone	Current timezone for the server
        serverdatetime	Current date/time on the server
        openordercount	Count of open orders on your account
        */

        JsonObject root = jsonFromString(raw);

        log.info("raw: {}", raw);
        if(root.getString("success").equalsIgnoreCase("1")) {
            Balance balance = new Balance();

            JsonObject jsonResult = root.getJsonObject("return");

            JsonObject jsonAvailable = jsonResult.getJsonObject("balances_available");
            JsonObject jsonOpen = jsonResult.getJsonObject("balances_hold");

            for(Currency c : market.getCurrencies()) {

                String key = c.name();

                if(jsonAvailable.containsKey(key)) {
                    balance.setAvailable(new CurrencyValue(new BigDecimal(jsonAvailable.getString(key), CurrencyValue.CURRENCY_MATH_CONTEXT), c));
                }

                if(jsonOpen.containsKey(key)) {
                    balance.setOpen(new CurrencyValue(new BigDecimal(jsonOpen.getString(key), CurrencyValue.CURRENCY_MATH_CONTEXT), c));
                }
            }

            return balance;
        }

        log.error("failed to load balances");

        return null;
    }

    @Override
    public boolean cancelOrder(OrderId order) {

        Form form = new Form();
        form.param("method", "cancelorder");
        form.param("orderid", order.getIdentifier());

        String raw = protectedPostRequest(tradeBase, String.class, Entity.form(form));

        JsonObject root = jsonFromString(raw);

        if(root.getString("success").equalsIgnoreCase("1")) {
            return true;
        }

        return false;
    }

    @Override
    public OrderId placeOrder(AssetPair asset, OrderType type, CurrencyValue volume, CurrencyValue price) {


/*
        marketid	Market ID for which you are creating an order for
        ordertype	Order type you are creating (Buy/Sell)
        quantity	Amount of units you are buying/selling in this order
        price	Price per unit you are buying/selling at
                */

        NumberFormat nf = NumberFormat.getNumberInstance(Locale.US);
        nf.setMaximumFractionDigits(10);
        nf.setMinimumFractionDigits(5);
        nf.setMaximumIntegerDigits(15);

        String typeStr = "ERROR";
        switch(type) {
            case BID:
                typeStr = "Buy";
                break;

            case ASK:
                typeStr = "Sell";
                break;
        }

        Form form = new Form();
        form.param("method", "createorder");
        form.param("marketid", marketId.get(asset).toString());


        form.param("ordertype", typeStr);
        form.param("quantity", nf.format(volume.getValue()));
        form.param("price", nf.format(price.getValue()));

        String raw = protectedPostRequest(tradeBase, String.class, Entity.form(form));

        JsonObject root = jsonFromString(raw);

        if(root.getString("success").equalsIgnoreCase("1")) {
            String idStr = root.getString("orderid");
            return new OrderId(market, idStr);
        }

        String error = root.getString("error");
        log.error("failed to place order: {}", error);

        return null;
    }

    @Override
    public List<MarketOrder> getOpenOrders() {

        Form form = new Form();
        form.param("method", "allmyorders");

        String raw = protectedPostRequest(tradeBase, String.class, Entity.form(form));

        JsonObject root = jsonFromString(raw);

        if(root.getString("success").equalsIgnoreCase("1")) {
            List<MarketOrder> orders = new ArrayList<>();

            JsonArray jsonOrdersList = root.getJsonArray("return");
            for(int i=0; i<jsonOrdersList.size(); i++) {

                JsonObject jsonOrder = jsonOrdersList.getJsonObject(i);

                MarketOrder order = new MarketOrder();

                order.setId(new OrderId(market, jsonOrder.getString("orderid")));

                AssetPair asset = assetForMarketId(Integer.parseInt(jsonOrder.getString("marketid")));
                order.setAsset(asset);

                order.setVolume(new CurrencyValue(Double.parseDouble(jsonOrder.getString("quantity")), asset.getBase()));

                order.setPrice(new CurrencyValue(Double.parseDouble(jsonOrder.getString("price")), asset.getQuote()));

                OrderType type = OrderType.UNDEF;
                String dStr = jsonOrder.getString("ordertype");
                if(dStr.equalsIgnoreCase("Buy")) type = OrderType.BID;
                if(dStr.equalsIgnoreCase("Sell")) type = OrderType.ASK;

                order.setType(type);

                orders.add(order);
            }

            return orders;
        }

        return null;
    }

    private AssetPair assetForMarketId(int i) {

        for(AssetPair asset : marketId.keySet()) {
            if(marketId.get(asset) == i) {
                return asset;
            }
        }

        return null;
    }

    @Override
    public Fee getTradeFee(OrderType trade) {

        switch(trade) {
            case BID:
                return buyFee;

            case ASK:
                return sellFee;
        }

        // you sould never come here
        return null;
    }
}
