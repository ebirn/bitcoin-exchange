package at.outdated.bitcoin.exchange.bitcurex;

import at.outdated.bitcoin.exchange.api.OrderId;
import at.outdated.bitcoin.exchange.api.account.Balance;
import at.outdated.bitcoin.exchange.api.client.RestExchangeClient;
import at.outdated.bitcoin.exchange.api.jaxb.JsonEnforcingFilter;
import at.outdated.bitcoin.exchange.api.market.Market;
import at.outdated.bitcoin.exchange.api.account.AccountInfo;
import at.outdated.bitcoin.exchange.api.account.Wallet;
import at.outdated.bitcoin.exchange.api.currency.Currency;
import at.outdated.bitcoin.exchange.api.currency.CurrencyValue;
import at.outdated.bitcoin.exchange.api.market.*;
import at.outdated.bitcoin.exchange.api.market.OrderType;
import at.outdated.bitcoin.exchange.bitcurex.jaxb.*;
import org.apache.commons.codec.binary.Base64;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.json.JsonObject;
import javax.ws.rs.HttpMethod;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Form;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * Created with IntelliJ IDEA.
 * User: ebirn
 * Date: 30.05.13
 * Time: 12:50
 * To change this template use File | Settings | File Templates.
 */
public class BitcurexApiClient extends RestExchangeClient {

    WebTarget tradeTarget, publicTarget;

    public BitcurexApiClient(Market market) {
        super(market);

        client.register(JsonEnforcingFilter.class);

        tradeTarget = client.target("https://{quote}.bitcurex.com/api/0/");
        publicTarget = client.target("https://{quote}.bitcurex.com/data");
    }

    // FIXME complete implementation!
    @Override
    public AccountInfo getAccountInfo() {

        /// see https://bitcurex.com/en-pages,eurapi.html

        // getOrders
        // getFunds
        // getTransactions

        WebTarget fundsTarget = tradeTarget.path("/getFunds").resolveTemplate("quote", Currency.EUR);
        Entity entity = Entity.form(new Form());

        Invocation.Builder builder = setupProtectedResource(fundsTarget, entity);
        String rawFunds = builder.post(entity, String.class);

        entity = Entity.form(new Form());
        WebTarget ordersTarget = tradeTarget.path("/getOrders").resolveTemplate("quote", Currency.EUR);

        String rawOrders =  setupProtectedResource(ordersTarget, entity).post(entity, String.class);

        WebTarget transactionsTarget = tradeTarget.path("/getTransactions").resolveTemplate("quote", Currency.EUR);
        Form form = new Form();

        form.param("type", "" + BitcurexTransactionType.BTC_DEPOST.ordinal());
        entity = Entity.form(form);
        String rawTransactions =  setupProtectedResource(transactionsTarget, entity).post(entity, String.class);


        JsonObject jsonFunds = jsonFromString(rawFunds);
        JsonObject jsonOrders = jsonFromString(rawOrders);
        JsonObject jsonTransactions = jsonFromString(rawTransactions);

        BitcurexAccountInfo info = new BitcurexAccountInfo();

        Wallet eurWallet = new Wallet(Currency.EUR);
        info.addWallet(eurWallet);
        eurWallet.setBalance(new CurrencyValue(Double.parseDouble(jsonFunds.getString("eurs")), Currency.EUR));

        Wallet btcWallet = new Wallet(Currency.BTC);
        info.addWallet(btcWallet);
        btcWallet.setBalance(new CurrencyValue(Double.parseDouble(jsonFunds.getString("btcs")), Currency.BTC));

        return info;
    }

    @Override
    public Balance getBalance() {

        WebTarget fundsTarget = tradeTarget.path("/getFunds").resolveTemplate("quote", Currency.EUR);
        Entity entity = Entity.form(new Form());

        Invocation.Builder builder = setupProtectedResource(fundsTarget, entity);
        String rawFunds = builder.post(entity, String.class);

        JsonObject jsonFunds = jsonFromString(rawFunds);

        Balance balance = new Balance(market);

        balance.setAvailable(new CurrencyValue(new BigDecimal(jsonFunds.getString("eurs")), Currency.EUR));
        balance.setAvailable(new CurrencyValue(new BigDecimal(jsonFunds.getString("btcs")), Currency.BTC));

        return balance;
    }

    @Override
    public MarketDepth getMarketDepth(AssetPair asset) {
        Currency base = asset.getBase();
        Currency quote = asset.getQuote();

        WebTarget depthTarget = publicTarget.path("/orderbook.json").resolveTemplate("quote", asset.getQuote())
                .resolveTemplate("curr", quote.name().toLowerCase());

        String raw = super.simpleGetRequest(depthTarget, String.class);

        JsonObject root =  jsonFromString(raw);

        MarketDepth depth = new MarketDepth(asset);


        double[][] bids = parseNestedArray(root.getJsonArray("bids"));

        for(double[] bid : bids) {
            depth.addBid(bid[1], bid[0]);
        }

        double[][] asks = parseNestedArray(root.getJsonArray("asks"));
        for(double[] ask : asks) {
            depth.addAsk(ask[1], ask[0]);
        }
        /*
        for(JsonValue v : rawDepth) {
            JsonObject trade = (JsonObject) v;

            double price = Double.parseDouble(trade.getString("price"));
            double volume = Double.parseDouble(trade.getString("amount"));
            int type = trade.getJsonNumber("type").intValue();


            // sell
            if(type == 1) {
                depth.addAsk(volume, price);
            }
            // buy
            else {
                depth.addBid(volume, price);
            }
        }
*/
        return depth;
    }

    @Override
    public TickerValue getTicker(AssetPair asset) {

        if(asset.getBase() != Currency.BTC) {
            throw new IllegalArgumentException("unsupported currency");
        }


        WebTarget tickerResource = publicTarget.path("/ticker.json").resolveTemplate("quote", asset.getQuote());

        BitcurexTickerValue bTicker = simpleGetRequest(tickerResource, BitcurexTickerValue.class);

        if(bTicker == null) return null;

        TickerValue ticker = bTicker.getTickerValue();
        ticker.setAsset(asset);
        return ticker;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Number getLag() {
        return 1.0;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    protected <T> Invocation.Builder setupProtectedResource(WebTarget res, Entity<T> entity) {

        /*
        headers = array(
                'Rest-Key: ' . key,
                'Rest-Sign: ' . base64_encode(hash_hmac('sha512', post_data, base64_decode(secret), true)),
                );
        */


        try {

            String secret = getSecret();
            Mac mac = Mac.getInstance("HmacSHA512");
            SecretKeySpec secret_spec = new SecretKeySpec(Base64.decodeBase64(secret), "HmacSHA512");
            mac.init(secret_spec);



            String nonce = Long.toString((new Date()).getTime());

            Form form = ((Entity<Form>) entity).getEntity();
            form.param("nonce", nonce);

            mac.update(formData2String(form).getBytes("UTF-8"));

            String sign = new String(Base64.encodeBase64(mac.doFinal(), false));


            Invocation.Builder builder = res.request();
            builder.header("Rest-Sign", sign);
            builder.header("Rest-Key", getUserId());


            return builder;  //To c
        }
        catch(Exception e) {
            e.printStackTrace();
        }

        return null;
    }


    @Override
    public boolean cancelOrder(OrderId order) {

        /*
        cancelOrder - cancels sell/buy offer
        POST: nonce=#&oid=#&type=#, returns: eurs, btcs, orders
         */

        WebTarget orderTgt = tradeTarget.path("cancelOrder").resolveTemplate("quote", Currency.EUR);


        Form form = new Form();

        form.param("oid", order.getIdentifier());

        //TODO: to require this parameter is STUPID
        form.param("type", "???");


        boolean stillExisting = false;

        String raw = protectedPostRequest(orderTgt, String.class, Entity.form(form));
        log.info("raw cancel: {}", raw);
        /*
        Orders orders = protectedPostRequest(orderTgt, Orders.class, Entity.form(form));

        for(BitcurexOrder o : orders.getOrders()) {
            if(order.getIdentifier().equalsIgnoreCase(o.getOid())) {
                stillExisting = true;
                log.error("failed to delete order: {}", order.getIdentifier());
                break;
            }
        }
*/
        return stillExisting;
    }

    @Override
    public OrderId placeOrder(AssetPair asset, OrderType type, CurrencyValue volume, CurrencyValue price) {



        /*
        buyBTC - sets a buy offer BTC (BID)
        POST: nonce=#&amount=#&price=#, returns: eurs, btcs, orders

        sellBTC - sets a sell offer BTC (ASK)
        POST: nonce=#&amount=#&price=#, returns: eurs, btcs, orders
        * */


        WebTarget orderTgt = tradeTarget.path(type.verb() + "BTC").resolveTemplate("quote", asset.getQuote());


        Form form = new Form();

        form.param("amount", volume.valueToString());
        form.param("price", volume.valueToString());

        String raw = protectedPostRequest(orderTgt, String.class, Entity.form(form));



        return null;
    }

    @Override
    public List<MarketOrder> getOpenOrders() {
        /*
        getOrders - gets current active offers and balance
        POST: nonce=#, returns: eurs, btcs, orders
        */

        WebTarget ordersTgtEur = tradeTarget.path("/getOrders").resolveTemplate("quote", Currency.EUR);
        WebTarget ordersTgtPln = tradeTarget.path("/getOrders").resolveTemplate("quote", Currency.PLN);

        Form form = new Form();

        Future<Orders> ordersEur = asyncRequest(ordersTgtEur, Orders.class, HttpMethod.POST, Entity.form(form));
        Future<Orders> ordersPln = asyncRequest(ordersTgtPln, Orders.class, HttpMethod.POST, Entity.form(form));


        List<BitcurexOrder> allOrders = new ArrayList<>();
        try {
            allOrders.addAll(ordersEur.get().getOrders());
            allOrders.addAll(ordersPln.get().getOrders());
        }
        catch(ExecutionException | InterruptedException e) {
            log.error("failed to load orders", e);
            return null;
        }

        List<MarketOrder> orders = new ArrayList<>();

        for(BitcurexOrder o : allOrders) {

            orders.add(convert(o));
        }

        return orders;
    }

    private MarketOrder convert(BitcurexOrder o) {

        MarketOrder order = new MarketOrder();
        order.setId(new OrderId(market, o.getOid()));


        switch(o.getType()) {
            case ASK:
                order.setType(OrderType.ASK);
                break;

            case BID:
                order.setType(OrderType.ASK);
                break;
        }

        order.setVolume(new CurrencyValue(o.getAmount().doubleValue(), Currency.BTC));

        return order;
    }
}
