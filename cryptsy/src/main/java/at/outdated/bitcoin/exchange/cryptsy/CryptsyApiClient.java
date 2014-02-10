package at.outdated.bitcoin.exchange.cryptsy;

import at.outdated.bitcoin.exchange.api.OrderId;
import at.outdated.bitcoin.exchange.api.account.Balance;
import at.outdated.bitcoin.exchange.api.account.TransactionType;
import at.outdated.bitcoin.exchange.api.account.WalletTransaction;
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
import javax.ws.rs.core.GenericType;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by ebirn on 06.01.14.
 */
public class CryptsyApiClient extends RestExchangeClient implements MarketClient, TradeClient {

    Map<AssetPair,Integer> marketId = new HashMap<>();

    WebTarget tradeBase = client.target("https://www.cryptsy.com/api");
    WebTarget publicBase = client.target("http://pubapi.cryptsy.com/api.php");

    Fee buyFee, sellFee;

    DateFormat dateFormat;

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

        dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
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

        TickerValue ticker = new TickerValue(asset);

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


            ticker.setLast(last);
            ticker.setVolume(volume);
            ticker.setBid(bid);
            ticker.setAsk(ask);

        }
        catch(Exception e) {
            log.info("failed to load ticker for {}", asset);
            return null;
        }

        return ticker;
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

        Balance balance = new Balance();
        if(root.getString("success").equalsIgnoreCase("1")) {


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


        }
        else {
            log.error("failed to load balances");
            return null;
        }

        return balance;
    }


    @Override
    public List<MarketOrder> getTradeHistory(AssetPair asset, Date since) {
        int marketNum = marketId.get(asset);
        // http://pubapi.cryptsy.com/api.php?method=singleorderdata&marketid=

        Form form = new Form();
        form.param("method", "markettrades");

        WebTarget tgt = tradeBase.queryParam("marketid", marketNum);

        //GenericType<List<CryptsyTrade>> tradeList = new GenericType<List<CryptsyTrade>>() {};
        Entity e = Entity.form(form);
        MarketTradesResult tradesResult = protectedPostRequest(tgt, MarketTradesResult.class, e);

        // String raw = protectedPostRequest(tgt, String.class, e);
        //JsonObject root = jsonFromString(raw);

        /*
            tradeid	A unique ID for the trade
            datetime	Server datetime trade occurred
            tradeprice	The price the trade occurred at
            quantity	Quantity traded
            total	Total value of trade (tradeprice * quantity)
            initiate_ordertype	The type of order which initiated this trade
         */


        List<MarketOrder> history = null;

        if(tradesResult.isSuccess()) {
            history =  new ArrayList<>();
            for(CryptsyTrade ct : tradesResult.getResult()) {

                if(since.before(ct.datetime)) {
                    history.add(ct.getOrder(market, asset));
                }
            }
        }
        else {
            log.error("failed to load past trades: {}", tradesResult.getError());
            return null;
        }

        return history;

    }

    @Override
    public List<WalletTransaction> getTransactions() {

        List<WalletTransaction> transactions = new ArrayList<>();

        Form form = new Form();
        form.param("method", "allmytrades");

        String rawTrades = protectedPostRequest(tradeBase, String.class, Entity.form(form));
        /*
            tradeid	An integer identifier for this trade
            tradetype	Type of trade (Buy/Sell)
            datetime	Server datetime trade occurred
            marketid	The market in which the trade occurred
            tradeprice	The price the trade occurred at
            quantity	Quantity traded
            total	Total value of trade (tradeprice * quantity) - Does not include fees
            fee	Fee Charged for this Trade
            initiate_ordertype	The type of order which initiated this trade
            order_id	Original order id this trade was executed against
         */
        JsonObject rootTrades = jsonFromString(rawTrades);

        if(rootTrades.getString("success").equalsIgnoreCase("1")) {

            JsonArray jsonOrdersList = rootTrades.getJsonArray("return");
            for(int i=0; i<jsonOrdersList.size(); i++) {


                JsonObject jsonOrder = jsonOrdersList.getJsonObject(i);

                Date timestamp =  null;
                try {
                    timestamp = dateFormat.parse(jsonOrder.getString("datetime"));
                }
                catch(Exception e) {
                    log.error("failed to parse date", e);
                }

                WalletTransaction transLeft = new WalletTransaction();

                transLeft.setId(new OrderId(market, jsonOrder.getString("tradeid")));
                transLeft.setTimestamp(timestamp);
                transLeft.setInfo(jsonOrder.getString("order_id"));

                AssetPair asset = assetForMarketId(Integer.parseInt(jsonOrder.getString("marketid")));

                transLeft.setValue(new CurrencyValue(Double.parseDouble(jsonOrder.getString("quantity")), asset.getBase()));

                String dStr = jsonOrder.getString("tradetype");
                if(dStr.equalsIgnoreCase("Buy")) transLeft.setType(TransactionType.IN);
                if(dStr.equalsIgnoreCase("Sell"))  transLeft.setType(TransactionType.OUT);


                transactions.add(transLeft);

                /*****************************************************************/

                WalletTransaction transRight = new WalletTransaction();

                transRight.setId(new OrderId(market, jsonOrder.getString("tradeid")));
                transRight.setTimestamp(timestamp);

                transRight.setValue(new CurrencyValue(Double.parseDouble(jsonOrder.getString("tradeprice")), asset.getQuote()));
                transRight.setInfo(jsonOrder.getString("order_id"));

                dStr = jsonOrder.getString("tradetype");
                if(dStr.equalsIgnoreCase("Buy")) transRight.setType(TransactionType.OUT);
                if(dStr.equalsIgnoreCase("Sell"))  transRight.setType(TransactionType.IN);

                transactions.add(transRight);
            }

        }
        else {
            log.error("cannot parse trades");
        }

        /*

            currency	Name of currency account
            timestamp	The timestamp the activity posted
            datetime	The datetime the activity posted
            timezone	Server timezone
            type	Type of activity. (Deposit / Withdrawal)
            address	Address to which the deposit posted or Withdrawal was sent
            amount	Amount of transaction (Not including any fees)
            fee	Fee (If any) Charged for this Transaction (Generally only on Withdrawals)
            trxid	Network Transaction ID (If available)

         */

        form = new Form();
        form.param("method", "mytransactions");

        String rawTransactions = protectedPostRequest(tradeBase, String.class, Entity.form(form));

        JsonObject rootTransactions = jsonFromString(rawTransactions);

        if(rootTransactions.getString("success").equalsIgnoreCase("1")) {

            JsonArray transactionList = rootTransactions.getJsonArray("return");
            for(int i=0; i<transactionList.size(); i++) {
                JsonObject jsonTransaction = transactionList.getJsonObject(i);

                try {

                    WalletTransaction trans = new WalletTransaction();
                    trans.setId(new OrderId(market, jsonTransaction.getString("trxid")));

                    Date timestamp = new Date(jsonTransaction.getInt("timestamp") * 1000);
                    trans.setTimestamp(timestamp);

                    Currency curr = Currency.valueOf(jsonTransaction.getString("currency"));

                    trans.setValue(new CurrencyValue(new BigDecimal(jsonTransaction.getString("amount"), CurrencyValue.CURRENCY_MATH_CONTEXT), curr));

                    switch(jsonTransaction.getString("type")) {
                        case "Deposit":
                            trans.setType(TransactionType.DEPOSIT);
                            break;

                        case "Withdrawal":
                            trans.setType(TransactionType.WITHDRAW);
                            break;
                    }

                    transactions.add(trans);

                    String feeStr = jsonTransaction.getString("fee", null);
                    if(feeStr != null) {
                        WalletTransaction feeTrans = new WalletTransaction();
                        feeTrans.setValue(new CurrencyValue(new BigDecimal(feeStr, CurrencyValue.CURRENCY_MATH_CONTEXT), curr));
                        feeTrans.setTimestamp(timestamp);
                        feeTrans.setId(trans.getId());
                        feeTrans.setType(TransactionType.FEE);

                        transactions.add(feeTrans);
                    }

                    trans.setInfo("adress:" + jsonTransaction.getString("address"));
                }
                catch(Exception e) {
                    log.error("cannot process transaction: {}", transactionList.getJsonObject(i));
                }
            }
        }
        else {
            log.error("failed to process transaction list");
        }

        return transactions;
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
