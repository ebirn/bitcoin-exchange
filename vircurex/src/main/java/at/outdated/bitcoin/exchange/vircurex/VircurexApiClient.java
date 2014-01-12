package at.outdated.bitcoin.exchange.vircurex;

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
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
import java.util.TimeZone;


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

        WebTarget balancesTgt = client.target("https://vircurex.com/api/get_balances.json");

        Form f = new Form();
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
        WebTarget tickerTgt = client.target("https://vircurex.com/api/get_info_for_1_currency.json")
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

        WebTarget depthTarget = client.target("https://vircurex.com/api/orderbook.json?base={base}&alt={quote}")
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
         Order.call_https("https://vircurex.com","/api/create_order.json?account=#{user_name}&id=#{trx_id}&token=#{tok}&timestamp=#{t}&ordertype=sell&amount=10&currency1=btc&unitprice=50&currency2=nmc")
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

            String word = getPropertyString("words.balance");
            if(word == null) {
                throw new IllegalStateException("cannot setup secure request, missing secret word.");
            }


            Form form = (Form) entity.getEntity();

            res = res.queryParam("account", user)
                .queryParam("id",txId)
                .queryParam("token", buildToken(word, user, timestamp, txId, form.asMap().getFirst("command")))
                .queryParam("timestamp",timestamp);
        }

        catch(Exception e) {
            log.error("failed to setup secure request");
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


}
