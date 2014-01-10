package at.outdated.bitcoin.exchange.cryptsy;

import at.outdated.bitcoin.exchange.api.OrderId;
import at.outdated.bitcoin.exchange.api.account.AccountInfo;
import at.outdated.bitcoin.exchange.api.client.ExchangeApiClient;
import at.outdated.bitcoin.exchange.api.client.MarketClient;
import at.outdated.bitcoin.exchange.api.client.TradeClient;
import at.outdated.bitcoin.exchange.api.currency.Currency;
import at.outdated.bitcoin.exchange.api.currency.CurrencyValue;
import at.outdated.bitcoin.exchange.api.market.*;
import org.apache.commons.codec.binary.Hex;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Form;
import java.text.NumberFormat;
import java.util.*;

/**
 * Created by ebirn on 06.01.14.
 */
public class CryptsyApiClient extends ExchangeApiClient implements MarketClient, TradeClient {

    Map<AssetPair,Integer> marketId = new HashMap<>();

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
            WebTarget tgt = client.target("http://pubapi.cryptsy.com/api.php?method=singlemarketdata&marketid=" + marketNum);
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
        WebTarget tgt = client.target("http://pubapi.cryptsy.com/api.php?method=singleorderdata&marketid=" + marketNum);

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
            log.error("failed to parse market depth", e);
        }

        return null;
    }

    @Override
    public AccountInfo getAccountInfo() {
        return new CryptsyAccountInfo();
    }


    @Override
    public boolean cancelOrder(OrderId order) {

        WebTarget tgt = client.target("https://www.cryptsy.com/api");

        Form form = new Form();
        form.param("method", "cancelorder");
        form.param("orderid", order.getIdentifier());

        String raw = protectedPostRequest(tgt, String.class, Entity.form(form));

        JsonObject root = jsonFromString(raw);

        if(root.getString("success").equalsIgnoreCase("1")) {
            return true;
        }

        return false;
    }

    @Override
    public OrderId placeOrder(AssetPair asset, TradeDecision decision, CurrencyValue volume, CurrencyValue price) {


/*
        marketid	Market ID for which you are creating an order for
        ordertype	Order type you are creating (Buy/Sell)
        quantity	Amount of units you are buying/selling in this order
        price	Price per unit you are buying/selling at
                */

        WebTarget tgt = client.target("https://www.cryptsy.com/api");


        NumberFormat nf = NumberFormat.getNumberInstance(Locale.US);
        nf.setMaximumFractionDigits(10);
        nf.setMinimumFractionDigits(5);
        nf.setMaximumIntegerDigits(15);

        String typeStr = "ERROR";
        switch(decision) {
            case BUY:
                typeStr = "Buy";
                break;

            case SELL:
                typeStr = "Sell";
                break;
        }

        Form form = new Form();
        form.param("method", "createorder");
        form.param("marketid", marketId.get(asset).toString());


        form.param("ordertype", typeStr);
        form.param("quantity", nf.format(volume.getValue()));
        form.param("price", nf.format(price.getValue()));

        String raw = protectedPostRequest(tgt, String.class, Entity.form(form));

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

        WebTarget tgt = client.target("https://www.cryptsy.com/api");

        Form form = new Form();
        form.param("method", "allmyorders");

        String raw = protectedPostRequest(tgt, String.class, Entity.form(form));

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

                TradeDecision d = TradeDecision.GETHELP;
                String dStr = jsonOrder.getString("ordertype");
                if(dStr.equalsIgnoreCase("Buy")) d = TradeDecision.BUY;
                if(dStr.equalsIgnoreCase("Sell")) d = TradeDecision.SELL;

                order.setDecision(d);

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
}