package at.outdated.bitcoin.exchange.bitstamp;

import at.outdated.bitcoin.exchange.api.OrderId;
import at.outdated.bitcoin.exchange.api.account.*;
import at.outdated.bitcoin.exchange.api.client.RestExchangeClient;
import at.outdated.bitcoin.exchange.api.currency.CurrencyAddress;
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
import javax.json.JsonValue;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.GenericType;
import java.io.StringReader;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Created with IntelliJ IDEA.
 * User: ebirn
 * Date: 25.05.13
 * Time: 13:04
 * To change this template use File | Settings | File Templates.
 */
public class BitstampClient extends RestExchangeClient {

    SimpleDateFormat bitstampDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    WebTarget baseTarget;

    public BitstampClient(Market market) {
        super(market);

        this.baseTarget = client.target("https://www.bitstamp.net/api/");
        // initial trading fee is higher
        tradeFee = new SimplePercentageFee("0.006");
    }

    @Override
    protected <T> Invocation.Builder setupProtectedResource(WebTarget res, Entity<T> entity) {

        Form form = ((Entity<Form>) entity).getEntity();

        String apiKey = getUserId();
        String secret = getSecret();

        long nonce = (System.currentTimeMillis());

        form.param("nonce", Long.toString(nonce));
        form.param("key", apiKey);

        //message = nonce + client_id + api_key
        // signature = hmac.new(API_SECRET, msg=message, digestmod=hashlib.sha256).hexdigest().upper()

        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            SecretKeySpec secret_spec = new SecretKeySpec(secret.getBytes("UTF-8"), "HmacSHA256");
            mac.init(secret_spec);

            String signatureData = Long.toString(nonce) + getCustomerId() + apiKey;
            String signature = Hex.encodeHexString(mac.doFinal(signatureData.getBytes("UTF-8"))).toUpperCase();
            form.param("signature", signature);
        }
        catch(Exception e) {
            e.printStackTrace();
        }

        return res.request();
    }


    @Override
    public List<WalletTransaction> getTransactions() {

        WebTarget transactionsTgt = baseTarget.path("/user_transactions/");
        String rawTransactions = protectedPostRequest(transactionsTgt, String.class, Entity.form(new Form()));

        JsonArray jsonTransactions = jsonArrayFromString(rawTransactions);

        List<WalletTransaction> list = new ArrayList<>();
        for(int i=0; i<jsonTransactions.size(); i++) {
            try {
                JsonObject jt = jsonTransactions.getJsonObject(i);
                long orderId = -1L;

                if(jt.get("order_id").getValueType() == JsonValue.ValueType.NUMBER)
                    orderId = jt.getJsonNumber("order_id").longValue();

                double usd = Double.parseDouble(jt.getString("usd"));
                double btc = Double.parseDouble(jt.getString("btc"));
                double btc_usd = Double.parseDouble(jt.getString("btc_usd"));
                double fee = Double.parseDouble(jt.getString("fee"));

                int type = jt.getInt("type");
                long id = jt.getJsonNumber("id").longValue();
                Date timestamp = bitstampDate.parse(jt.getString("datetime"));

                switch(type) {
                    case 0: // deposit
                        parseDeposit(list, usd, btc, fee, timestamp, id, orderId);
                        break;

                    case 1: //withdrawal
                        parseWithdrawal(list, usd, btc, fee, timestamp, id, orderId);
                        break;

                    case 2: //trade
                        parseTrade(list, usd, btc, fee, timestamp, id, orderId);
                        break;

                    default:
                        throw new IllegalArgumentException("unknown transaction type");
                }
                //getAccountInfo().getWallet()
            }
            // rethrow possoble problems
            catch(IllegalArgumentException iae) {
                throw iae;
            }
            // unknown errors are a problem
            catch(Exception e) {
                log.error("error parsing transaction", e);
            }

        }

        return list;
    }

    @Override
    public Balance getBalance() {
        Form balanceForm = new Form();
        balanceForm.param("sort", "asc");
        balanceForm.param("offset", "0");
        balanceForm.param("limit", "1000");

        WebTarget balanceResource = baseTarget.path("/balance/");
        BitstampAccountBalance bitstampBalance =  protectedPostRequest(balanceResource, BitstampAccountBalance.class, Entity.form(balanceForm));

        Balance balance = new Balance(market);

        balance.setAvailable(new CurrencyValue(bitstampBalance.btcAvailable, Currency.BTC));
        balance.setOpen(new CurrencyValue(bitstampBalance.btcReserved, Currency.BTC));

        balance.setAvailable(new CurrencyValue(bitstampBalance.usdAvailable, Currency.USD));
        balance.setOpen(new CurrencyValue(bitstampBalance.usdReserved, Currency.USD));

        //tradeFee = new SimplePercentageFee("0.006");
        tradeFee = new SimplePercentageFee(bitstampBalance.fee.divide(new BigDecimal(100.0)));

        return balance;
    }

    @Override
    public MarketDepth getMarketDepth(AssetPair asset) {
        Currency base = asset.getBase();
        Currency quote = asset.getQuote();

        // https://www.bitstamp.net/api/order_book/

        WebTarget depthTarget = baseTarget.path("/order_book/");

        String depthString = simpleGetRequest(depthTarget, String.class);

        JsonObject depthData = Json.createReader(new StringReader(depthString)).readObject();

        MarketDepth depth = new MarketDepth(asset);

        double[][] asks = parseNestedArray(depthData.getJsonArray("asks"));
        double[][] bids = parseNestedArray(depthData.getJsonArray("bids"));


        for(double[] bid : bids) {
            depth.addBid(bid[1], bid[0]);
        }

        for(double[] ask : asks) {
            depth.addAsk(ask[1], ask[0]);
        }

        return depth;
    }

    @Override
    public TickerValue getTicker(AssetPair asset) {

        WebTarget tickerResource = baseTarget.path("/ticker/");
        BitstampTickerValue bticker = simpleGetRequest(tickerResource, BitstampTickerValue.class);

        TickerValue ticker = null;
        if(bticker != null) {
            ticker = bticker.getTickerValue();
            ticker.setAsset(asset);
        }

        return ticker;
    }

    @Override
    public List<MarketOrder> getTradeHistory(AssetPair asset, Date since) {
        // https://www.bitstamp.net/api/transactions/
        WebTarget historyTgt = baseTarget.path("/transactions/");

        // {"date": "1391900651", "tid": 3406563, "price": "686.54", "amount": "0.00723919"}

        GenericType<List<BitstampOrder>> orderList = new GenericType<List<BitstampOrder>>() {};

        List<BitstampOrder> trades = historyTgt.request().get(orderList);

        List<MarketOrder> history = new ArrayList<>();

        for(BitstampOrder bo : trades) {

            if(since.before(bo.getDate())) {
                history.add(convertOrder(bo));
            }
        }

        return history;
    }

    protected String getCustomerId() {
        ResourceBundle bundle = ResourceBundle.getBundle("bitcoin-exchange");
        return bundle.getString("bitstamp.customerid");
    }


    private void parseDeposit(List<WalletTransaction> list , double usd, double btc, double fee, Date timestamp, long id, long orderId) {

        String descr = Long.toString(id) + ", " + Long.toString(orderId);

        if(usd != 0.0) list.add(parseTransaction(id, orderId, TransactionType.DEPOSIT, Currency.USD, usd, timestamp));

        if(btc != 0.0) list.add(parseTransaction(id, orderId, TransactionType.DEPOSIT, Currency.BTC, btc, timestamp));

        if(fee > 0.0) list.add(parseTransaction(id, orderId, TransactionType.FEE, Currency.USD, fee, timestamp));
    }

    private void parseTrade(List<WalletTransaction> list, double usd, double btc, double fee, Date timestamp, long id, long orderId) {

        String descr = Long.toString(id) + ", " + Long.toString(orderId);

        TransactionType transactionType = null;
        if(usd != 0.0) {
            if(usd < 0.0)  transactionType = TransactionType.OUT;
            else transactionType = TransactionType.IN;

            list.add(parseTransaction(id, orderId, transactionType, Currency.USD, usd, timestamp));
        }

        if(btc != 0.0) {
            if(btc<0.0)  transactionType = TransactionType.OUT;
            else transactionType = TransactionType.IN;

            list.add(parseTransaction(id, orderId, transactionType, Currency.BTC, btc, timestamp));
        }

        if(fee > 0.0) list.add(parseTransaction(id, orderId, TransactionType.FEE, Currency.USD, fee, timestamp));

    }

    private void parseWithdrawal(List<WalletTransaction> list, double usd, double btc, double fee, Date timestamp, long id, long orderId) {

        String descr = Long.toString(id) + ", " + Long.toString(orderId);

        if(usd != 0.0) list.add(parseTransaction(id, orderId, TransactionType.WITHDRAW, Currency.USD, usd, timestamp));

        if(btc != 0.0) list.add(parseTransaction(id, orderId, TransactionType.WITHDRAW, Currency.BTC, btc, timestamp));

        if(fee > 0.0) list.add(parseTransaction(id, orderId, TransactionType.FEE, Currency.USD, fee, timestamp));
    }

    private WalletTransaction parseTransaction(long id, long orderId, TransactionType type, Currency curr, double volume, Date timestamp) {

        WalletTransaction transaction = new WalletTransaction(type, new CurrencyValue(Math.abs(volume), curr));
        transaction.setId(new OrderId(market, Long.toString(id)));
        transaction.setInfo(Long.toString(orderId));
        transaction.setTimestamp(timestamp);

        return transaction;
    }

    @Override
    protected CurrencyAddress lookupUpDepositAddress(Currency curr) {

        //"https://www.bitstamp.net/api/bitcoin_deposit_address/"

        String address = null;
        Entity e = Entity.form(new Form());

        switch(curr) {
            case BTC:
                address = setupProtectedResource(baseTarget.path("bitcoin_deposit_address/"), e).post(e, String.class);
                address = address.replace("\"", "");
                break;

            case XRP:
                address = setupProtectedResource(baseTarget.path("/ripple_address/"), e).post(e, BitstampAddress.class).getAddress();
                break;

            default:
                throw new IllegalArgumentException("no withdrawals for " + curr);
        }

        return new CurrencyAddress(curr, address);

    }

    @Override
    public boolean cancelOrder(OrderId order) {

        // https://www.bitstamp.net/api/cancel_order/

        WebTarget tgt = baseTarget.path("/cancel_order/");

        Form form = new Form();
        form.param("id", order.getIdentifier());

        String raw = protectedPostRequest(tgt, String.class, Entity.form(form));

        if(raw.equalsIgnoreCase("true")) {
            return true;
        }

        JsonObject json = jsonFromString(raw);
        log.error("failed to delete order {} - {}", order.getIdentifier(), json.getString("error"));
        return false;
    }

    @Override
    public OrderId placeOrder(AssetPair asset, OrderType type, CurrencyValue volume, CurrencyValue price) {

        Form form = new Form();

        form.param("amount", volume.valueToString());
        form.param("price", price.valueToString());

        WebTarget tgt = null;
        switch(type) {
            case BID:
                tgt = baseTarget.path("/buy/");
                break;

            case ASK:
                tgt = baseTarget.path("/sell/");
                break;

            default:
                log.error("cannot place order to {}", type);
                return null;

        }

        //String raw = protectedPostRequest(tgt, String.class, Entity.form(form));
        //return null;

        BitstampOrder placedOrder = protectedPostRequest(tgt, BitstampOrder.class, Entity.form(form));
        return new OrderId(market, Integer.toString(placedOrder.getId()));
    }

    @Override
    public List<MarketOrder> getOpenOrders() {

        // https://www.bitstamp.net/api/open_orders/

        WebTarget tgt = baseTarget.path("/open_orders/");

        GenericType<List<BitstampOrder>> orderType = new GenericType<List<BitstampOrder>>() {};
        Entity<Form> entity = Entity.form(new Form());

        List<BitstampOrder> bitstampOrders = setupProtectedResource(tgt, entity).post(entity, orderType);

        List<MarketOrder> orders = new ArrayList<>();

        for(BitstampOrder rawOrder : bitstampOrders) {
            orders.add(convertOrder(rawOrder));
        }

        return orders;
    }

    MarketOrder convertOrder(BitstampOrder rawOrder) {
        MarketOrder order = new MarketOrder();

        order.setId(new OrderId(market, Integer.toString(rawOrder.getId())));
        order.setAsset(market.getAsset(Currency.BTC, Currency.USD));

        order.setType(rawOrder.getType());

        order.setPrice(rawOrder.getPrice());
        order.setVolume(rawOrder.getAmount());

        if(rawOrder.date != null) {
            order.setTimestamp(rawOrder.date);
        }
        else if(rawOrder.datetime != null) {
            order.setTimestamp(rawOrder.datetime);
        }

        return order;
    }
}
