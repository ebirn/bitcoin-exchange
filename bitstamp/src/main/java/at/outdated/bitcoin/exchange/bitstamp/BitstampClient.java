package at.outdated.bitcoin.exchange.bitstamp;

import at.outdated.bitcoin.exchange.api.OrderId;
import at.outdated.bitcoin.exchange.api.client.ExchangeApiClient;
import at.outdated.bitcoin.exchange.api.currency.CurrencyAddress;
import at.outdated.bitcoin.exchange.api.market.Market;
import at.outdated.bitcoin.exchange.api.account.AccountInfo;
import at.outdated.bitcoin.exchange.api.account.TransactionType;
import at.outdated.bitcoin.exchange.api.account.Wallet;
import at.outdated.bitcoin.exchange.api.account.WalletTransaction;
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
public class BitstampClient extends ExchangeApiClient {

    SimpleDateFormat bitstampDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public BitstampClient(Market market) {
        super(market);
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
    public AccountInfo getAccountInfo() {

        BitstampAccountInfo info = new BitstampAccountInfo();
        Wallet wUSD = new Wallet(Currency.USD);
        Wallet wBTC = new Wallet(Currency.BTC);

        info.addWallet(wUSD);
        info.addWallet(wBTC);

        Form balanceForm = new Form();
        balanceForm.param("sort", "asc");
        balanceForm.param("offset", "0");
        balanceForm.param("limit", "1000");

        WebTarget balanceResource = client.target("https://www.bitstamp.net/api/balance/");
        BitstampAccountBalance balance =  protectedPostRequest(balanceResource, BitstampAccountBalance.class, Entity.form(balanceForm));

        WebTarget transactionsTgt = client.target("https://www.bitstamp.net/api/user_transactions/");
        String rawTransactions = protectedPostRequest(transactionsTgt, String.class, Entity.form(new Form()));


        info.setFee(new SimplePercentageFee(balance.getFee().doubleValue() / 100.0));

        JsonArray jsonTransactions = jsonArrayFromString(rawTransactions);
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
                        parseDeposit(info, usd, btc, fee, timestamp, id, orderId);
                        break;

                    case 1: //withdrawal
                        parseWithdrawal(info, usd, btc, fee, timestamp, id, orderId);
                        break;

                    case 2: //trade
                        parseTrade(info, usd, btc, fee, timestamp, id, orderId);
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

        wBTC.setBalance(new CurrencyValue(balance.getBtcBalance().doubleValue(), Currency.BTC));
        wUSD.setBalance(new CurrencyValue(balance.getUsdBalance().doubleValue(), Currency.USD));


        return info;  //To change body of implemented methods use File | Settings | File Templates.
    }


    @Override
    public MarketDepth getMarketDepth(AssetPair asset) {
        Currency base = asset.getBase();
        Currency quote = asset.getQuote();

        // https://www.bitstamp.net/api/order_book/

        WebTarget depthTarget = client.target("https://www.bitstamp.net/api/order_book/");

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

        WebTarget tickerResource = client.target("https://www.bitstamp.net/api/ticker/");
        BitstampTickerValue bticker = simpleGetRequest(tickerResource, BitstampTickerValue.class);

        TickerValue ticker = null;
        if(bticker != null) {
            ticker = bticker.getTickerValue();
            ticker.setAsset(asset);
        }


        return ticker;
    }

    @Override
    public Number getLag() {
        return 1.0;
    }

    protected String getCustomerId() {
        ResourceBundle bundle = ResourceBundle.getBundle("bitcoin-exchange");
        return bundle.getString("bitstamp.customerid");
    }


    private void parseDeposit(AccountInfo info, double usd, double btc, double fee, Date timestamp, long id, long orderId) {

        String descr = Long.toString(id) + ", " + Long.toString(orderId);


        if(usd != 0.0) parseTransaction(info, TransactionType.DEPOSIT, Currency.USD, usd, timestamp, descr);

        if(btc != 0.0) parseTransaction(info, TransactionType.DEPOSIT, Currency.BTC, btc, timestamp, descr);

        if(fee > 0.0) parseTransaction(info, TransactionType.FEE, Currency.USD, fee, timestamp, descr);
    }

    private void parseTrade(AccountInfo info, double usd, double btc, double fee, Date timestamp, long id, long orderId) {

        String descr = Long.toString(id) + ", " + Long.toString(orderId);

        TransactionType transactionType = null;
        if(usd != 0.0) {
            if(usd < 0.0)  transactionType = TransactionType.OUT;
            else transactionType = TransactionType.IN;

            parseTransaction(info, transactionType, Currency.USD, usd, timestamp, descr);
        }

        if(btc != 0.0) {
            if(btc<0.0)  transactionType = TransactionType.OUT;
            else transactionType = TransactionType.IN;

            parseTransaction(info, transactionType, Currency.BTC, btc, timestamp, descr);
        }

        if(fee > 0.0) parseTransaction(info, TransactionType.FEE, Currency.USD, fee, timestamp, descr);

    }

    private void parseWithdrawal(AccountInfo info, double usd, double btc, double fee, Date timestamp, long id, long orderId) {

        String descr = Long.toString(id) + ", " + Long.toString(orderId);

        if(usd != 0.0) parseTransaction(info, TransactionType.WITHDRAW, Currency.USD, usd, timestamp, descr);

        if(btc != 0.0) parseTransaction(info, TransactionType.WITHDRAW, Currency.BTC, btc, timestamp, descr);

        if(fee > 0.0) parseTransaction(info, TransactionType.FEE, Currency.USD, fee, timestamp, descr);
    }

    private void parseTransaction(AccountInfo info, TransactionType type, Currency curr, double volume, Date timestamp, String descr) {

        WalletTransaction transaction = new WalletTransaction(type, new CurrencyValue(Math.abs(volume), curr));
        transaction.setInfo(descr);
        transaction.setDatestamp(timestamp);

        info.getWallet(curr).addTransaction(transaction);
    }

    @Override
    protected CurrencyAddress lookupUpDepositAddress(Currency curr) {

        //"https://www.bitstamp.net/api/bitcoin_deposit_address/"

        String address = null;
        Entity e = Entity.form(new Form());

        switch(curr) {
            case BTC:
                address = setupProtectedResource(client.target("https://www.bitstamp.net/api/bitcoin_deposit_address/"), e).post(e, String.class);
                address = address.replace("\"", "");
                break;

            case XRP:
                address = setupProtectedResource(client.target("https://www.bitstamp.net/api/ripple_address/"), e).post(e, BitstampAddress.class).getAddress();
                break;

            default:
                throw new IllegalArgumentException("no withdrawals for " + curr);
        }

        return new CurrencyAddress(curr, address);

    }

    @Override
    public boolean cancelOrder(OrderId order) {

        // https://www.bitstamp.net/api/cancel_order/

        WebTarget tgt = client.target("https://www.bitstamp.net/api/cancel_order/");

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
    public OrderId placeOrder(AssetPair asset, TradeDecision decision, CurrencyValue volume, CurrencyValue price) {

        Form form = new Form();

        form.param("amount", volume.valueToString());
        form.param("price", price.valueToString());

        WebTarget tgt = null;
        switch(decision) {
            case BUY:
                tgt = client.target("https://www.bitstamp.net/api/buy/");
                break;

            case SELL:
                tgt = client.target("https://www.bitstamp.net/api/sell/");
                break;

            default:
                log.error("cannot place order to {}", decision);
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

        WebTarget tgt = client.target("https://www.bitstamp.net/api/open_orders/");

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

        switch(rawOrder.getType()) {
            case BUY:
                order.setDecision(TradeDecision.BUY);
                break;

            case SELL:
                order.setDecision(TradeDecision.SELL);
        }

        order.setPrice(new CurrencyValue(rawOrder.getPrice().doubleValue(), Currency.USD));
        order.setVolume(new CurrencyValue(rawOrder.getAmount().doubleValue(), Currency.BTC));
        order.setAsset(market.getAsset(Currency.BTC, Currency.USD));

        return order;
    }
}
