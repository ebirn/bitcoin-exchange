package at.outdated.bitcoin.exchange.bitkonan;

import at.outdated.bitcoin.exchange.api.OrderId;
import at.outdated.bitcoin.exchange.api.account.Balance;
import at.outdated.bitcoin.exchange.api.account.WalletTransaction;
import at.outdated.bitcoin.exchange.api.client.RestExchangeClient;
import at.outdated.bitcoin.exchange.api.currency.Currency;
import at.outdated.bitcoin.exchange.api.currency.CurrencyValue;
import at.outdated.bitcoin.exchange.api.market.*;
import at.outdated.bitcoin.exchange.api.market.fee.SimplePercentageFee;
import org.apache.commons.codec.binary.Hex;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: ebirn
 * Date: 26.05.13
 * Time: 23:43
 * To change this template use File | Settings | File Templates.
 */
public class BitkonanApiClient extends RestExchangeClient {

    WebTarget baseTarget = null;

    public BitkonanApiClient(Market market) {
        super(market);

        baseTarget = client.target("https://bitkonan.com/api/");

        tradeFee = new SimplePercentageFee("0.0029");
    }


    @Override
    public Balance getBalance() {

        WebTarget balanceTarget = baseTarget.path("/balance/");
        String rawBalance = protectedGetRequest(balanceTarget, String.class);

        // JsonObject jsonBalance = jsonFromString(rawBalance);

        log.error("not implemented yet!");

        Balance balance = new Balance(market);


        return balance;
    }

    @Override
    public List<WalletTransaction> getTransactions() {
        log.error("not implemented yet!");


        return null;
    }

    @Override
    public MarketDepth getMarketDepth(AssetPair asset) {
        Currency base = asset.getBase();
        Currency quote = asset.getQuote();

        // https://bitkonan.com/api/orderbook/?group=0

        WebTarget orderbook = baseTarget.path("/{prefix}orderbook/").resolveTemplate("prefix", prefixfor(base));;

        String obString = super.simpleGetRequest(orderbook, String.class);

        JsonObject konanDepth = jsonFromString(obString);

        double[][] asks = null;
        double[][] bids = null;

        if(base == Currency.BTC) {
            asks = parseNestedArray(konanDepth.getJsonArray("asks"));
            bids = parseNestedArray(konanDepth.getJsonArray("bids"));
        }
        else if(base == Currency.LTC) {
            asks = parseOtherDepth(konanDepth.getJsonArray("ask"));
            bids = parseOtherDepth(konanDepth.getJsonArray("bid"));
        }

        MarketDepth depth = new MarketDepth(asset);

        for(double[] bid : bids) {
            double price = bid[0];
            double volume = bid[1];
            depth.addBid(volume, price);
        }

        for(double[] ask : asks) {
            double price = ask[0];
            double volume = ask[1];
            depth.addAsk(volume, price);
        }

        return depth;
    }

    private double[][] parseOtherDepth(JsonArray jsonArray) {

        int size = jsonArray.size();
        double[][] orders = new double[size][2];

        for(int i=0; i<size; i++) {
            JsonObject entry = jsonArray.getJsonObject(i);

            orders[i][0] = entry.getJsonNumber("usd").doubleValue();
            orders[i][1] = entry.getJsonNumber("btc").doubleValue();
        }
        return orders;
    }

    @Override
    public TickerValue getTicker(AssetPair asset) {


        WebTarget tickerTarget = baseTarget.path("/{prefix}ticker/").resolveTemplate("prefix", prefixfor(asset.getBase()));

        BitkonanTickerValue response = simpleGetRequest(tickerTarget, BitkonanTickerValue.class);

        TickerValue value = null;
        if(response != null) {
            value = response.getTickerValue();
            value.setAsset(asset);
        }

        return value;
    }

    @Override
    public List<MarketOrder> getTradeHistory(AssetPair asset, Date since) {

        // https://bitkonan.com/api/transactions
        // Parameters:

        //FIXME use these parameters
        // offset - skip that many transactions before beginning to return results. Default: 0.
        // limit - limit result to that many transactions. Default: 25.
        // sort - sorting by date and time (asc - ascending; desc - descending). Default: desc.


        WebTarget tradesTgt = baseTarget.path("/{prefix}transactions/").resolveTemplate("prefix", prefixfor(asset.getBase())).queryParam("limit", 1000);

        GenericType<List<BitkonanOrder>> orderType = new GenericType<List<BitkonanOrder>>() {};
        List<BitkonanOrder> trades = tradesTgt.request().get(orderType);
        // {"total":111.75,"btc":0.15,"usd":745,"time":"2014-02-08T21:07:48.000Z","tradetype":1}

        List<MarketOrder> history = new ArrayList<>();

        for(BitkonanOrder bo : trades) {

            if(since.before(bo.time)) {
                history.add(bo.getOrder(market, asset));
            }
        }

        return history;
    }


    private String prefixfor(Currency baseCurrency) {

        switch(baseCurrency) {

            case LTC:
                return "ltc_";

            case BTC:
                return "";
        }

        return null;
    }

    @Override
    protected <Form> Invocation.Builder setupProtectedResource(WebTarget res, Entity<Form> entity) {

        Invocation.Builder builder = res.request();

        // Api-Key: The same as generated by our system when you created the API.
        // Api-Secret: message digest as lowercase hexits, generated using HMAC-SHA256 algorithm. Constructed from the the following concatenated strings: [POST Parameters]:[Timestamp].
        // Api-Timestamp: current timestamp in UNIX format.

        String apiKey = getUserId();
        String apiSecret = getSecret();
        long apiTimestamp = (new Date()).getTime()/1000L;

        try {
            Mac mac = Mac.getInstance("HmacSHA256");

            SecretKeySpec secret_spec = new SecretKeySpec(apiSecret.getBytes("UTF-8"), "HmacSHA256");
            mac.init(secret_spec);

            // path + NUL + POST (incl. nonce)
            String content = entity == null ? "" : entity.getEntity().toString();

            String payload = content + ":" + Long.toString(apiTimestamp);

            byte[] rawSignature = mac.doFinal(payload.getBytes());

            String signature = new String(Hex.encodeHex(rawSignature, true));

            builder.header("Api-Key", apiKey);
            //log.debug("Api-Key: {}", apiKey);

            builder.header("Api-Sign", signature);
            //log.debug("Api-Sign: {}", signature);

            builder.header("Api-Timestamp", apiTimestamp);
            //log.debug("Api-Timestamp: {}", apiTimestamp);
        }
        catch(Exception e) {
           log.error("failed to setup secure request", e);
        }

        return builder;  //To change body of implemented methods use File | Settings | File Templates.
    }


    /**
     * At the time of writing: trading api is unavailable, no documentation
     * @return null
     */
    // FIXME: implement these
    @Override
    public List<MarketOrder> getOpenOrders() {
        log.error("not implemented");
        return null;
    }

    /**
     * At the time of writing: trading api is unavailable, no documentation
     * @return null
     */
    @Override
    public OrderId placeOrder(AssetPair asset, OrderType type, CurrencyValue volume, CurrencyValue price) {
        log.error("not implemented");
        return null;
    }

    /**
     * At the time of writing: trading api is unavailable, no documentation
     * @return null
     */
    @Override
    public boolean cancelOrder(OrderId order) {
        log.error("not implemented");
        return false;
    }
}
