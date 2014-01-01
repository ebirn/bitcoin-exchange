package at.outdated.bitcoin.exchange.bter;

import at.outdated.bitcoin.exchange.api.client.ExchangeApiClient;
import at.outdated.bitcoin.exchange.api.market.Market;
import at.outdated.bitcoin.exchange.api.account.AccountInfo;
import at.outdated.bitcoin.exchange.api.account.Wallet;
import at.outdated.bitcoin.exchange.api.currency.Currency;
import at.outdated.bitcoin.exchange.api.currency.CurrencyValue;
import at.outdated.bitcoin.exchange.api.market.AssetPair;
import at.outdated.bitcoin.exchange.api.market.MarketDepth;
import at.outdated.bitcoin.exchange.api.market.TickerValue;
import org.apache.commons.codec.binary.Hex;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.json.JsonObject;
import javax.json.JsonValue;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Form;
import java.util.Date;

/**
 * Created by ebirn on 11.10.13.
 */
public class BterApiClient extends ExchangeApiClient {

    public BterApiClient(Market market) {
        super(market);
    }

    @Override
    public AccountInfo getAccountInfo() {


        WebTarget fundsTarget = client.target("https://bter.com/api/1/private/getfunds");


        String rawFunds = protectedPostRequest(fundsTarget, String.class, Entity.form(new Form()));

        JsonObject jsonFunds = jsonFromString(rawFunds);

        BterAccountInfo info = new BterAccountInfo();


        if(jsonFunds.get("available_funds").getValueType() == JsonValue.ValueType.OBJECT) {
            JsonObject funds = jsonFunds.getJsonObject("available_funds");
            JsonObject lockedFunds = jsonFunds.getJsonObject("locked_funds");
            for(String key : funds.keySet()) {

                try {
                    Currency c = Currency.valueOf(key);

                    Wallet w = new Wallet(c);
                    w.setBalance(new CurrencyValue(Double.valueOf(funds.getString(key)), c));

                    if(lockedFunds != null && lockedFunds.containsKey(key)) {
                        w.setOpenOrders(new CurrencyValue(Double.valueOf(lockedFunds.getString(key)), c));
                    }

                    info.addWallet(w);
                }
                catch(Exception e) {
                    log.info("Currency '{}' unhandled, cannot create wallet", key);
                }
            }
        }
        else {
            log.info("no funds available");
        }


        return info;
    }

    @Override
    public TickerValue getTicker(AssetPair asset) {

        WebTarget tickerTgt = client.target("https://bter.com/api/1/ticker/" + asset.getBase().name().toLowerCase() + "_" + asset.getQuote().name().toLowerCase());

        BterTicker ticker = simpleGetRequest(tickerTgt, BterTicker.class);

        TickerValue tickerValue = ticker.getValue();

        tickerValue.setAsset(asset);

        return tickerValue;
    }

    @Override
    public Number getLag() {
        return 0.0;
    }

    @Override
    public MarketDepth getMarketDepth(AssetPair asset) {


        String base = asset.getBase().name().toLowerCase();
        String quote = asset.getQuote().name().toLowerCase();

        WebTarget depthTarget = client.target("https://bter.com/api/1/depth/" + base + "_" + quote);

        String rawDepth = depthTarget.request().get(String.class);

        JsonObject jsonDepth = jsonFromString(rawDepth);

        double[][] asks = parseNestedArray(jsonDepth.getJsonArray("asks"));
        double[][] bids = parseNestedArray(jsonDepth.getJsonArray("bids"));


        MarketDepth depth = new MarketDepth(asset);

        for(double[] ask : asks) {
            depth.addAsk(ask[1], ask[0]);
        }

        for(double[] bid : bids) {
            depth.addBid(bid[1], bid[0]);
        }

        return depth;
    }

    @Override
    protected <T> Invocation.Builder setupProtectedResource(WebTarget res, Entity<T> entity) {

        long nonce = ((new Date()).getTime());

        Form form = ((Entity<Form>) entity).getEntity();
        form.param("nonce", Long.toString(nonce));

        String hexSignature = null;
        try {
            Mac mac = Mac.getInstance("HmacSHA512");
            SecretKeySpec secret_spec = new SecretKeySpec(getSecret().getBytes(), "HmacSHA512");
            mac.init(secret_spec);
            mac.update(formData2String(form).getBytes());

            hexSignature = Hex.encodeHexString(mac.doFinal()).toLowerCase();
        }
        catch(Exception e) {
            log.error("failed to setup hashing");
        }

        Invocation.Builder builder = res.request();

        builder.header("KEY", getUserId());
        builder.header("SIGN", hexSignature);

        return builder;
    }
}
