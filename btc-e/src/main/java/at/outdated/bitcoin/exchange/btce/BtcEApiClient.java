package at.outdated.bitcoin.exchange.btce;

import at.outdated.bitcoin.exchange.api.ExchangeApiClient;
import at.outdated.bitcoin.exchange.api.account.AccountInfo;
import at.outdated.bitcoin.exchange.api.currency.Currency;
import at.outdated.bitcoin.exchange.api.currency.CurrencyValue;
import at.outdated.bitcoin.exchange.api.market.MarketDepth;
import at.outdated.bitcoin.exchange.api.market.MarketOrder;
import at.outdated.bitcoin.exchange.api.market.TickerValue;
import at.outdated.bitcoin.exchange.api.market.TradeDecision;

import java.util.List;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import java.io.StringReader;

/**
 * Created with IntelliJ IDEA.
 * User: ebirn
 * Date: 26.05.13
 * Time: 23:43
 * To change this template use File | Settings | File Templates.
 */
public class BtcEApiClient extends ExchangeApiClient {

    @Override
    public AccountInfo getAccountInfo() {
        return new BtcEAccountInfo();  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public MarketDepth getMarketDepth(Currency base, Currency quote) {
        // (price, volume)

        WebTarget resource = client.target("https://btc-e.com/api/2/"+base.name().toLowerCase()+"_" + quote.name().toLowerCase() + "/depth");

        String response = super.simpleGetRequest(resource, String.class);

        JsonReader reader = Json.createReader(new StringReader(response));

        JsonObject root = reader.readObject();
        JsonArray asksArr = root.getJsonArray("asks");
        JsonArray bidsArr = root.getJsonArray("bids");

        MarketDepth depth = new MarketDepth();
        depth.setBaseCurrency(base);


        for(int i=0; i<asksArr.size(); i++ ) {
            float price = (float) asksArr.getJsonArray(i).getJsonNumber(0).doubleValue();
            float volume = (float) asksArr.getJsonArray(i).getJsonNumber(1).doubleValue();
            depth.getAsks().add(new MarketOrder(TradeDecision.BUY, volume, base, new CurrencyValue(price, quote)));
        }
        for(int i=0; i<bidsArr.size(); i++ ) {
            float price = (float) bidsArr.getJsonArray(i).getJsonNumber(0).doubleValue();
            float volume = (float) bidsArr.getJsonArray(i).getJsonNumber(1).doubleValue();
            depth.getBids().add(new MarketOrder(TradeDecision.SELL, volume, base, new CurrencyValue(price, quote)));
        }

        return depth;
    }

    @Override
    protected <R> R simpleGetRequest(WebTarget target, Class<R> resultClass) {

        R result = null;

        String resultStr = super.simpleGetRequest(target, String.class);

        log.debug("BTC-E raw: " + resultStr);

        result = BtcEJsonResolver.convertFromJson(resultStr, resultClass);

        return result;
    }

    @Override
    public TickerValue getTicker(Currency currency) {

        // https://btc-e.com/api/2/btc_usd/ticker

        WebTarget tickerResource = client.target("https://btc-e.com/api/2/btc_" + currency.name().toLowerCase() + "/ticker");

        TickerResponse response = simpleGetRequest(tickerResource, TickerResponse.class);

        BtcETickerValue btcETickerValue = response.getTicker();

        TickerValue value = btcETickerValue.getTickerValue();
        value.setCurrency(currency);

        return value;
    }



    @Override
    public Number getLag() {
        return 0.12345678910;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    protected Invocation.Builder setupProtectedResource(WebTarget res) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
