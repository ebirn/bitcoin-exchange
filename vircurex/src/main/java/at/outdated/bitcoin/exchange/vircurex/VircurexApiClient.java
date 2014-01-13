package at.outdated.bitcoin.exchange.vircurex;

import at.outdated.bitcoin.exchange.api.OrderId;
import at.outdated.bitcoin.exchange.api.client.ExchangeApiClient;
import at.outdated.bitcoin.exchange.api.market.Market;
import at.outdated.bitcoin.exchange.api.account.AccountInfo;
import at.outdated.bitcoin.exchange.api.account.Wallet;
import at.outdated.bitcoin.exchange.api.currency.Currency;
import at.outdated.bitcoin.exchange.api.currency.CurrencyValue;
import at.outdated.bitcoin.exchange.api.market.*;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.lang3.StringUtils;

import javax.json.JsonObject;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Form;
import java.security.MessageDigest;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.*;


/**
 * Created by ebirn on 11.10.13.
 */
public class VircurexApiClient extends ExchangeApiClient {

    private enum OType {
        UNRELEASED,
        RELEASED;
    }

    public VircurexApiClient(Market market) {
        super(market);
    }

    @Override
    public AccountInfo getAccountInfo() {

        /*
        get_balances		balance
        available_balance		Input token: YourSecurityWord;YourUserName;Timestamp;ID;get_balances

        Note: the security word of this function is the security word from function "get_balance".

        Output token: YourSecurityWord;YourUserName;Timestamp;get_balances
         */

        WebTarget balancesTgt = client.target("https://api.vircurex.com/api/get_balances.json");

        Form f = new Form();
        f.param("word", getPropertyString("word.balance"));
        f.param("command", "get_balances");

        String rawBalances = protectedGetRequest(balancesTgt, String.class, Entity.form(f));

        JsonObject jsonBalances = jsonFromString(rawBalances).getJsonObject("balances");

        AccountInfo info = new VircurexAccountInfo();

        for(String currKey : jsonBalances.keySet()) {

            try {
                Currency curr = Currency.valueOf(currKey);
                double balance = Double.parseDouble(jsonBalances.getJsonObject(currKey).getString("balance"));
                double available = Double.parseDouble(jsonBalances.getJsonObject(currKey).getString("availablebalance"));

                Wallet w = new Wallet(curr);

                w.setBalance(new CurrencyValue(available, curr));
                w.setOpenOrders(new CurrencyValue(balance - available, curr));

                info.addWallet(w);
            }
            catch(Exception e) {
                // log.info("unknown currency {}", currKey);
            }
        }

        return info;
    }

    @Override
    public TickerValue getTicker(AssetPair asset) {

        // get_info_for_1_currency
        WebTarget tickerTgt = client.target("https://api.vircurex.com/api/get_info_for_1_currency.json")
            .queryParam("base", asset.getBase())
            .queryParam("alt", asset.getQuote());
        // {"base":"BTC","alt":"LTC","lowest_ask":"62.30141425","highest_bid":"61.12503063","last_trade":"62.30529595","volume":"82.92624028"}%

        VircurexTicker ticker = simpleGetRequest(tickerTgt, VircurexTicker.class);

        return ticker.getValue();
    }

    @Override
    public Number getLag() {
        return 100.00;
    }

    @Override
    public MarketDepth getMarketDepth(AssetPair asset) {

        WebTarget depthTarget = client.target("https://api.vircurex.com/api/orderbook.json?base={base}&alt={quote}")
            .resolveTemplate("base", asset.getBase().name())
            .resolveTemplate("quote", asset.getQuote().name());


        String rawDepth = simpleGetRequest(depthTarget, String.class);

        MarketDepth depth = new MarketDepth();
        depth.setAsset(asset);

        JsonObject jsonDepth = jsonFromString(rawDepth);

        try {
            double[][] bids = this.parseNestedArray(jsonDepth.getJsonArray("bids"));
            for(double[] bid : bids) {
                double volume = bid[1];
                double price = bid[0];

                depth.addBid(volume, price);
            }

            double[][] asks = this.parseNestedArray(jsonDepth.getJsonArray("asks"));
            for(double[] ask : asks) {
                double volume = ask[1];
                double price = ask[0];

                depth.addAsk(volume, price);
            }

        }
        catch(ClassCastException cce) {
            log.info("canot parse depth, probably empty?", cce);
            return null;
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

        WebTarget tgt = client.target("https://api.vircurex.com/api/read_orders.json").queryParam("otype", "1");


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


        return orders;
    }

    @Override
    public OrderId placeOrder(AssetPair asset, TradeDecision decision, CurrencyValue volume, CurrencyValue price) {
        // create_released_order
        // wordkey: createorder

        WebTarget tgt = client.target("https://api.vircurex.com/api/read_orders.json");

        Form f = new Form();
        f.param("word", getPropertyString("word.createorder"));
        f.param("command", "create_released_order");

        NumberFormat nf = NumberFormat.getNumberInstance(Locale.US);
        nf.setMinimumIntegerDigits(1);

        nf.setMinimumFractionDigits(4);
        nf.setMaximumFractionDigits(15);

        f.param("ordertype", decision.name());
        f.param("amount", nf.format(volume.getValue()));
        f.param("currency1", asset.getBase().name());
        f.param("unitprice", nf.format(price.getValue()));
        f.param("currency2", asset.getBase().name());


        // FIXME: missing params

        String raw = protectedGetRequest(tgt, String.class, Entity.form(f));

        JsonObject root = jsonFromString(raw);


        return null;
    }

    @Override
    public boolean cancelOrder(OrderId order) {

        // delete_order
        // wordkey: deleteorder


        WebTarget tgt = client.target("https://api.vircurex.com/api/delete_order.json");

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
