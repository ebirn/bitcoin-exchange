package at.outdated.bitcoin.exchange.coinse;

import at.outdated.bitcoin.exchange.api.OrderId;
import at.outdated.bitcoin.exchange.api.account.AccountInfo;
import at.outdated.bitcoin.exchange.api.account.Balance;
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
import javax.ws.rs.core.Form;
import javax.ws.rs.core.Request;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * Created by ebirn on 06.01.14.
 */
public class CoinseApiClient extends RestExchangeClient {

    WebTarget baseTarget = client.target("https://www.coins-e.com/api/v2/");

    public CoinseApiClient(Market market) {
        super(market);

        tradeFee = new SimplePercentageFee("0.002");
    }

    @Override
    public Number getLag() {
        return 0.01234;
    }

    @Override
    protected <T> Invocation.Builder setupProtectedResource(WebTarget res, Entity<T> entity) {



        Form form = ((Entity<Form>) entity).getEntity();
        form.param("nonce", Long.toString((new Date()).getTime()));

        if(form.asMap().get("method") == null) {
            log.warn("form value 'method' is not set");
        }

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

        builder.header("key", getUserId());
        builder.header("sign", hexSignature);

        return builder;
    }

    @Override
    public TickerValue getTicker(AssetPair asset) {

        // https://www.coins-e.com/api/v2/markets/data/

        String raw = simpleGetRequest(baseTarget.path("markets/data/"), String.class);

        JsonObject root = jsonFromString(raw);

        JsonObject jsonMarketList = root.getJsonObject("markets");

        String marketKey = asset.getBase() + "_" + asset.getQuote();
        JsonObject jsonMarket = jsonMarketList.getJsonObject(marketKey);

        JsonObject marketStat = jsonMarket.getJsonObject("marketstat");
        JsonObject stat24 = marketStat.getJsonObject("24h");

        TickerValue ticker = new TickerValue();

        ticker.setAsset(asset);
        ticker.setLast(Double.parseDouble(marketStat.getString("ltp")));
        ticker.setAsk(Double.parseDouble(marketStat.getString("ask")));
        ticker.setBid(Double.parseDouble(marketStat.getString("bid")));

        ticker.setHigh(Double.parseDouble(stat24.getString("h")));
        ticker.setLow(Double.parseDouble(stat24.getString("l")));
        ticker.setVolume(Double.parseDouble(stat24.getString("volume")));

        return ticker;
    }

    @Override
    public MarketDepth getMarketDepth(AssetPair asset) {

        // https://www.coins-e.com/api/v2/market/WDC_BTC/depth/

        String marketKey = asset.getBase() + "_" + asset.getQuote();

        String raw = simpleGetRequest(baseTarget.path("/market/"+marketKey+"/depth/"), String.class);
        JsonObject root = jsonFromString(raw);

        // {"status": true, "ltq": "31.07186789", "ltp": "0.00052500", "marketdepth": {"bids": [{"q": "67.61970

        JsonObject jsonDepth = root.getJsonObject("marketdepth");




        MarketDepth depth = new MarketDepth(asset);

        // are these mixed up in the api / exchange site?

        JsonArray jsonAsks = jsonDepth.getJsonArray("asks");
        // beginning lowest ask
        for(int i=0; i<jsonAsks.size(); i++) {
            JsonObject obj = jsonAsks.getJsonObject(i);
            double price = Double.parseDouble(obj.getString("r"));
            double volume = Double.parseDouble(obj.getString("q"));

            if(obj.getInt("n") > 0) {
                depth.addAsk(volume, price);
            }
        }

        JsonArray jsonBids = jsonDepth.getJsonArray("bids");
        // beginning with highest bid
        for(int i=0; i<jsonBids.size(); i++) {
            JsonObject obj = jsonBids.getJsonObject(i);
            double price = Double.parseDouble(obj.getString("r"));
            double volume = Double.parseDouble(obj.getString("q"));

            if(obj.getInt("n") > 0) {
                depth.addBid(volume, price);
            }
        }

        return depth;
    }



    //FIXME actually implement this
    @Override
    public AccountInfo getAccountInfo() {

        return new CoinseAccountInfo();
    }

    @Override
    public Balance getBalance() {

        WebTarget balanceTarget = baseTarget.path("wallet/all/");

        Form form = new Form();
        form.param("method", "getwallets");

        String rawBalance = protectedPostRequest(balanceTarget, String.class, Entity.form(form));

        JsonObject jsonBalance = jsonFromString(rawBalance);

        if(jsonBalance.getBoolean("status")) {

            JsonObject jsonWallets = jsonBalance.getJsonObject("wallets");

            Balance balance = new Balance(market);

            for(Currency curr : market.getCurrencies()) {

                String key = curr.name();

                if(jsonWallets.containsKey(key)) {
                    JsonObject jsonWallet = jsonWallets.getJsonObject(key);

                    BigDecimal a = new BigDecimal(jsonWallet.getString("a"), CurrencyValue.CURRENCY_MATH_CONTEXT);
                    BigDecimal h = new BigDecimal(jsonWallet.getString("h"), CurrencyValue.CURRENCY_MATH_CONTEXT);
                    BigDecimal u = new BigDecimal(jsonWallet.getString("u"), CurrencyValue.CURRENCY_MATH_CONTEXT);

                    balance.setAvailable(new CurrencyValue(a, curr));
                    balance.setOpen((new CurrencyValue(h.add(u), curr)));
                }
            }

            return balance;
        }

        log.error("failed to load balance: {}", jsonBalance.getString("message"));

        return null;
    }

    // FIXME
    @Override
    public List<MarketOrder> getOpenOrders() {
        return null;
    }

    @Override
    public OrderId placeOrder(AssetPair asset, OrderType type, CurrencyValue volume, CurrencyValue price) {
        return null;
    }

    @Override
    public boolean cancelOrder(OrderId order) {
        return false;
    }
}
