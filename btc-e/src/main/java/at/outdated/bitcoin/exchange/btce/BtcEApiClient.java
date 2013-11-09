package at.outdated.bitcoin.exchange.btce;

import at.outdated.bitcoin.exchange.api.client.ExchangeApiClient;
import at.outdated.bitcoin.exchange.api.market.Market;
import at.outdated.bitcoin.exchange.api.account.AccountInfo;
import at.outdated.bitcoin.exchange.api.currency.Currency;
import at.outdated.bitcoin.exchange.api.currency.CurrencyValue;
import at.outdated.bitcoin.exchange.api.market.*;
import org.apache.commons.codec.binary.Hex;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;
import java.io.StringReader;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: ebirn
 * Date: 26.05.13
 * Time: 23:43
 * To change this template use File | Settings | File Templates.
 */
public class BtcEApiClient extends ExchangeApiClient {

    public BtcEApiClient(Market market) {
        super(market);
    }

    @Override
    public AccountInfo getAccountInfo() {



        //https://btc-e.com/tapi/
        WebTarget tgt = client.target("https://btc-e.com/tapi");

        MultivaluedMap<String,String> data = null;

        data = new MultivaluedHashMap<>();
        data.add("method", "getInfo");
        String raw = syncRequest(tgt, String.class, "POST", Entity.form(data), true);
        //log.debug("raw info: {}", raw);
        InfoResponse infoRes = BtcEJsonResolver.convertFromJson(raw, InfoResponse.class);

        AccountInfo info = infoRes.result;

        data = new MultivaluedHashMap<>();
        data.add("method", "TransHistory");
        raw = syncRequest(tgt, String.class, "POST", Entity.form(data), true);
        //log.debug("raw transactions: {}", raw);
        JsonObject transResponse = jsonFromString(raw);
        if(transResponse.getInt("success") == 1) {
            /*
            {
                "success":1,
                "return":{
                    "1081672":{
                        "type":1,
                        "amount":1.00000000,
                        "currency":"BTC",
                        "desc":"BTC Payment",
                        "status":2,
                        "timestamp":1342448420
                    }
                }
            }
            */

            JsonObject transResult = transResponse.getJsonObject("result");
            for(String key : transResult.keySet()) {
                JsonObject jt = transResult.getJsonObject(key);
                Currency curr = Currency.valueOf(jt.getString("currency"));

                double volume = jt.getJsonNumber("amount").doubleValue();
                Date timestamp = new Date(jt.getJsonNumber("timestamp").longValue() * 1000L);
                String desc = jt.getString("desc");

            }
        }



        data = new MultivaluedHashMap<>();
        data.add("method", "TradeHistory");
        raw = syncRequest(tgt, String.class, "POST", Entity.form(data), true);
        //log.debug("raw trades: {}", raw);
        JsonObject tradeResponse = jsonFromString(raw);
        if(tradeResponse.getInt("success") == 1) {
        /*
                {
            "success":1,
            "return":{
                "166830":{
                    "pair":"btc_usd",
                    "type":"sell",
                    "amount":1,
                    "rate":1,
                    "order_id":343148,
                    "is_your_order":1,
                    "timestamp":1342445793
                }
            }
        }  */


        }


        return info;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public MarketDepth getMarketDepth(AssetPair asset) {
        Currency base = asset.getBase();
        Currency quote = asset.getQuote();

        // (price, volume)

        WebTarget resource = client.target("https://btc-e.com/api/2/{base}_{quote}/depth")
            .resolveTemplate("base", base.name().toLowerCase())
            .resolveTemplate("quote", quote.name().toLowerCase());

        String response = super.simpleGetRequest(resource, String.class);

        JsonReader reader = Json.createReader(new StringReader(response));

        JsonObject root = reader.readObject();
        JsonArray asksArr = root.getJsonArray("asks");
        JsonArray bidsArr = root.getJsonArray("bids");

        MarketDepth depth = new MarketDepth();
        depth.setBaseCurrency(base);


        for(int i=0; i<asksArr.size(); i++ ) {
            double price = asksArr.getJsonArray(i).getJsonNumber(0).doubleValue();
            double volume = asksArr.getJsonArray(i).getJsonNumber(1).doubleValue();
            depth.getAsks().add(new MarketOrder(TradeDecision.BUY, new CurrencyValue(volume, base), new CurrencyValue(price, quote)));
        }
        for(int i=0; i<bidsArr.size(); i++ ) {
            double price = bidsArr.getJsonArray(i).getJsonNumber(0).doubleValue();
            double volume = bidsArr.getJsonArray(i).getJsonNumber(1).doubleValue();
            depth.getBids().add(new MarketOrder(TradeDecision.SELL, new CurrencyValue(volume, base), new CurrencyValue(price, quote)));
        }

        return depth;
    }


    @Override
    protected <R> R simpleGetRequest(WebTarget target, Class<R> resultClass) {

        R result = null;

        String resultStr = super.simpleGetRequest(target, String.class);

        //log.debug("BTC-E raw: " + resultStr);
        result = BtcEJsonResolver.convertFromJson(resultStr, resultClass);

        return result;
    }

    @Override
    public TickerValue getTicker(AssetPair asset) {

        // https://btc-e.com/api/2/btc_usd/ticker

        WebTarget tickerResource = client.target("https://btc-e.com/api/2/" + asset.getBase().name().toLowerCase() + "_" + asset.getQuote().name().toLowerCase() + "/ticker");

        TickerResponse response = simpleGetRequest(tickerResource, TickerResponse.class);

        BtcETickerValue btcETickerValue = response.getTicker();

        TickerValue value = btcETickerValue.getTickerValue();
        value.setCurrency(asset.getQuote());

        return value;
    }



    @Override
    public Number getLag() {
        return 0.12345678910;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    protected <T> Invocation.Builder setupProtectedResource(WebTarget tgt, Entity<T> entity) {

        String apiKey = getUserId();
        String apiSecret = getSecret();

        Invocation.Builder builder = null;

        try {
            Mac mac = Mac.getInstance("HmacSHA512");
            SecretKeySpec secret_spec = new SecretKeySpec(apiSecret.getBytes("UTF-8"), "HmacSHA512");
            mac.init(secret_spec);
            //    1.381.050.637
            long nonce = (new Date()).getTime()/1000L; //max:2.147.483.647

            Entity<Form> e = (Entity<Form>) entity;
            e.getEntity().param("nonce", Long.toString(nonce));

            String payload = formData2String(e.getEntity());
            log.debug("encoded payload: {}", payload);

            byte[] rawSignature = mac.doFinal(payload.getBytes("UTF-8"));
            String signature = new String(Hex.encodeHex(rawSignature));

            builder = tgt.request();

            builder.header("Key", apiKey);
            log.debug("Key: {}", apiKey);

            builder.header("Sign", signature);
            log.debug("Sign: {}", signature);
        }
        catch (Exception e) {
            log.error("error: {}", e);
        }

        return builder;
    }
}
