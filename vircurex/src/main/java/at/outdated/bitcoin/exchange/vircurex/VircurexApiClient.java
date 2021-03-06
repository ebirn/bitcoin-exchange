package at.outdated.bitcoin.exchange.vircurex;

import at.outdated.bitcoin.exchange.api.OrderId;
import at.outdated.bitcoin.exchange.api.account.Balance;
import at.outdated.bitcoin.exchange.api.account.WalletTransaction;
import at.outdated.bitcoin.exchange.api.client.RestExchangeClient;
import at.outdated.bitcoin.exchange.api.currency.Currency;
import at.outdated.bitcoin.exchange.api.currency.CurrencyValue;
import at.outdated.bitcoin.exchange.api.market.*;
import at.outdated.bitcoin.exchange.api.market.fee.SimplePercentageFee;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.lang3.StringUtils;

import javax.json.JsonObject;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.GenericType;
import java.math.BigDecimal;
import java.security.MessageDigest;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.*;


/**
 * Created by ebirn on 11.10.13.
 */
public class VircurexApiClient extends RestExchangeClient {

    WebTarget baseTarget;

    private enum OType {
        UNRELEASED,
        RELEASED;
    }

    public VircurexApiClient(Market market) {
        super(market);

        baseTarget = client.target("https://api.vircurex.com/api/");
        tradeFee =  new SimplePercentageFee("0.002");
    }

    @Override
    public Balance getBalance() {

        WebTarget balancesTgt = baseTarget.path("/get_balances.json");

        Form f = new Form();
        f.param("word", getPropertyString("word.balance"));
        f.param("command", "get_balances");

        String rawBalances = protectedGetRequest(balancesTgt, String.class, Entity.form(f));

        JsonObject jsonBalances = jsonFromString(rawBalances).getJsonObject("balances");

        Balance balance = new Balance(market);
        for(String currKey : jsonBalances.keySet()) {

            try {
                Currency curr = Currency.valueOf(currKey);
                BigDecimal balanceValue = new BigDecimal(jsonBalances.getJsonObject(currKey).getString("balance"), CurrencyValue.CURRENCY_MATH_CONTEXT);
                BigDecimal availableValue = new BigDecimal(jsonBalances.getJsonObject(currKey).getString("availablebalance"), CurrencyValue.CURRENCY_MATH_CONTEXT);


                balance.setAvailable(new CurrencyValue(availableValue, curr));
                balance.setOpen(new CurrencyValue(balanceValue.subtract(availableValue), curr));

            }
            catch(Exception e) {
                log.error("failed to retrieve balance {}", currKey);
            }
        }

        return balance;
    }


    // TODO: find out what we can do here
    @Override
    public List<WalletTransaction> getTransactions() {

        log.warn("this method has not been implemented.");
        // command: read_orderexecutions

        List<WalletTransaction> list = new ArrayList<>();

        return list;
    }

    @Override
    public TickerValue getTicker(AssetPair asset) {

        // get_info_for_1_currency
        WebTarget tickerTgt = baseTarget.path("/get_info_for_1_currency.json")
            .queryParam("base", asset.getBase())
            .queryParam("alt", asset.getQuote());
        // {"base":"BTC","alt":"LTC","lowest_ask":"62.30141425","highest_bid":"61.12503063","last_trade":"62.30529595","volume":"82.92624028"}%

        VircurexTicker ticker = simpleGetRequest(tickerTgt, VircurexTicker.class);

        TickerValue tickerValue = null;
        if(ticker != null) {
            tickerValue = ticker.getValue();
        }

        return tickerValue;
    }


    @Override
    public List<MarketOrder> getTradeHistory(AssetPair asset, Date since) {

        WebTarget tradesTgt = baseTarget.path("/trades.json")
                .queryParam("base", asset.getBase())
                .queryParam("alt", asset.getQuote());

        // parameter since = tradeid

        GenericType<List<VircurexTrade>> tradeList = new GenericType<List<VircurexTrade>>(){};

        /*
        [{"date":1391011966,"tid":1331206,"amount":"1.0","price":"0.02669999"},
        {"date":1391011966,"tid":1331208,"amount":"2.9995","price":"0.0267"},
        */

        List<VircurexTrade> trades = tradesTgt.request().get(tradeList);

        List<MarketOrder> history = null;

        if(trades != null) {
            history = new ArrayList<>();

            for(VircurexTrade trade : trades) {

                if(since.before(trade.date)) {
                    history.add(trade.getOrder(market, asset));
                }
            }

        }


        return history;
    }

    @Override
    public MarketDepth getMarketDepth(AssetPair asset) {

        WebTarget depthTarget = baseTarget.path("/orderbook.json?base={base}&alt={quote}")
            .resolveTemplate("base", asset.getBase().name())
            .resolveTemplate("quote", asset.getQuote().name());


        String rawDepth = simpleGetRequest(depthTarget, String.class);

        MarketDepth depth = null;

        // partial init if there is a result
        if(rawDepth != null) {
            depth = new MarketDepth();
            depth.setAsset(asset);

            // response can be completely empty
            if(!rawDepth.isEmpty()) {
                JsonObject jsonDepth = jsonFromString(rawDepth);

                try {
                    BigDecimal[][] bids = this.parseNestedArray(jsonDepth.getJsonArray("bids"));
                    for(BigDecimal[] bid : bids) {
                        BigDecimal volume = bid[1];
                        BigDecimal price = bid[0];

                        depth.addBid(volume, price);
                    }

                    BigDecimal[][] asks = this.parseNestedArray(jsonDepth.getJsonArray("asks"));
                    for(BigDecimal[] ask : asks) {
                        BigDecimal volume = ask[1];
                        BigDecimal price = ask[0];

                        depth.addAsk(volume, price);
                    }

                }
                catch(ClassCastException cce) {
                    log.info("canot parse depth, probably empty?", cce);
                    return null;
                }
            }
        }

        if(depth == null) {
            log.error("failed to load depth for {}", asset);
        }

        return depth;
    }


    @Override
    protected <T> Invocation.Builder setupProtectedResource(WebTarget res, Entity<T> entity) {
    /*
         t = Time.now.gmtime.strftime("%Y-%m-%dT%H:%M:%S")
         trx_id = Digest::SHA2.hexdigest("#{t}-#{rand}")
         user_name = "MY_USER_NAME"
         secret_word = "123456789"
         tok = Digest::SHA2.hexdigest("#{secret_word};#{user_name};#{t};#{trx_id};create_order;sell;10;btc;50;nmc")
         Order.call_https("https://api.vircurex.com","/api/create_order.json?account=#{user_name}&id=#{trx_id}&token=#{tok}&timestamp=#{t}&ordertype=sell&amount=10&currency1=btc&unitprice=50&currency2=nmc")
     */

        //FIXME to response authentication (check token)
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
            sdf.setTimeZone(TimeZone.getTimeZone("UTC"));

            Date now = new Date();
            String timestamp = sdf.format(now);

            String txId = Hex.encodeHexString(digest.digest(timestamp.getBytes()));
            String user = getUserId();
            if(user == null) {
                throw new IllegalStateException("cannot setup secure request, missing user ID.");
            }

            Form form = (Form) entity.getEntity();

            String word = form.asMap().getFirst("word");
            form.asMap().remove("word");


            if(word == null) {
                throw new IllegalStateException("cannot setup secure request, missing secret word.");
            }

            //List<String> params = form.asMap().get("params");

            String command = form.asMap().getFirst("command");
            form.asMap().remove("command");

            res = res.queryParam("account", user)
                .queryParam("id",txId)
                .queryParam("timestamp", timestamp);

            ArrayList<String> tokenValues = new ArrayList<>();
            tokenValues.add(word);
            tokenValues.add(user);
            tokenValues.add(timestamp);
            tokenValues.add(txId);
            tokenValues.add(command);

            for(String key : form.asMap().keySet()) {
                String value = form.asMap().getFirst(key);

                tokenValues.add(value);
                res = res.queryParam(key, value);
            }

            String[] tokenArr = new String[tokenValues.size()];
            tokenValues.toArray(tokenArr);

            String token = buildToken( tokenArr );
            res = res.queryParam("token", token);

        }

        catch(Exception e) {
            log.error("failed to setup secure request", e);
        }

        Invocation.Builder builder = res.request();

        return builder;
    }



    private String buildToken(String... args) {

        String token = null;
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            String fullString = StringUtils.join(args, ";");

            token = Hex.encodeHexString(digest.digest(fullString.getBytes()));
        }
        catch(Exception e) {
            log.error("failed to generate token string", e);
        }

        return token;
    }


    @Override
    public List<MarketOrder> getOpenOrders() {
        // read_orders
        // we hardcode otype=1 for released orders (otype = 0 ... unreleased)

        // wordkey: readorders

        WebTarget tgt = baseTarget.path("/read_orders.json").queryParam("otype", "1");


        Form f = new Form();
        f.param("word", getPropertyString("word.readorders"));
        f.param("command", "read_orders");

        String raw = protectedGetRequest(tgt, String.class, Entity.form(f));

        JsonObject root = jsonFromString(raw);

        int status = root.getInt("status");
        if(status != 0) {
            log.info("failed to load order list, error: {}", root.getString("message"));
            return null;
        }

        int numOrders = root.getInt("numberorders");



        ArrayList<MarketOrder> orders = new ArrayList<>(numOrders);

        for(int i=1; i<=numOrders; i++) {

            JsonObject jsonOrder = root.getJsonObject("order-" + i);

            Currency c1 = Currency.valueOf(jsonOrder.getString("currency1"));
            Currency c2 = Currency.valueOf(jsonOrder.getString("currency2"));

            AssetPair asset = market.getAsset(c1, c2);

            MarketOrder order = new MarketOrder();
            order.setId(new OrderId(market, jsonOrder.getJsonNumber("orderid").toString()));
            order.setAsset(asset);

            switch(jsonOrder.getString("ordertype")) {
                case "SELL":
                    order.setType(OrderType.ASK);
                    break;

                case "BUY":
                    order.setType(OrderType.BID);
                    break;

                default:
                    order.setType(OrderType.UNDEF);
            }

            order.setVolume(new CurrencyValue(new BigDecimal(jsonOrder.getString("quantity")), c1));
            order.setPrice(new CurrencyValue(new BigDecimal(jsonOrder.getString("unitprice")), c2));

            orders.add(order);
        }

        return orders;
    }

    @Override
    public OrderId placeOrder(AssetPair asset, OrderType type, CurrencyValue volume, CurrencyValue price) {
        // create_released_order

        // Input token:
        // YourSecurityWord;YourUserName;Timestamp;ID;create_order;ordertype;amount;currency1;unitprice;currency2
        // Output token:
        // YourSecurityWord;YourUserName;Timestamp;create_order;order_id

        // wordkey: createorder

        WebTarget tgt = baseTarget.path("/create_released_order.json");

        Form f = new Form();
        f.param("word", getPropertyString("word.createorder"));
        f.param("command", "create_order");

        NumberFormat nf = NumberFormat.getNumberInstance(Locale.US);
        nf.setMinimumIntegerDigits(1);

        nf.setMinimumFractionDigits(4);
        nf.setMaximumFractionDigits(15);

        switch(type) {
            case ASK:
                f.param("ordertype", "SELL");
                break;

            case BID:
                f.param("ordertype", "BUY");
                break;
        }

        f.param("amount", nf.format(volume.getValue()));
        f.param("currency1", asset.getBase().name());
        f.param("unitprice", nf.format(price.getValue()));
        f.param("currency2", asset.getQuote().name());


        // FIXME: missing params

        String raw = protectedGetRequest(tgt, String.class, Entity.form(f));

        JsonObject root = jsonFromString(raw);
        int status = root.getInt("status");
        if(status != 0) {

            MarketOrder order = new MarketOrder();
            order.setAsset(asset);
            order.setType(type);
            order.setVolume(volume);
            order.setPrice(price);

            log.error("failed to place order: {} - ({})", order, root.getString("statustext"));
            return null;
        }

        OrderId id = new OrderId(market, root.getString("orderid"));
        return id;
    }

    @Override
    public boolean cancelOrder(OrderId order) {

        // delete_order
        // wordkey: deleteorder


        WebTarget tgt = baseTarget.path("/delete_order.json");

        Form f = new Form();
        f.param("word", getPropertyString("word.deleteorder"));
        f.param("command", "delete_order");

        //TODO: hopefully the map in Form keeps the params in order, this is needed for token generation
        f.param("orderid", order.getIdentifier());
        f.param("otype", "1");


        String raw = protectedGetRequest(tgt, String.class, Entity.form(f));

        JsonObject root = jsonFromString(raw);

        int status = root.getInt("status");
        if(status != 0) {
            log.info("failed to cancel order: {} (id: {})", root.getString("statustext"), order.getIdentifier());
            return false;
        }


        // at this point: status must be 0
        // if(status == 0 && order.getIdentifier().equalsIgnoreCase(root.getString("orderid"))) {
        //    return true;
        //}

        return true;
    }

}
