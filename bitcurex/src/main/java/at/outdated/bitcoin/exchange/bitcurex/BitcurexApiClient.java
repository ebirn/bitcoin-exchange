package at.outdated.bitcoin.exchange.bitcurex;

import at.outdated.bitcoin.exchange.api.ExchangeApiClient;
import at.outdated.bitcoin.exchange.api.account.AccountInfo;
import at.outdated.bitcoin.exchange.api.currency.Currency;
import at.outdated.bitcoin.exchange.api.currency.CurrencyValue;
import at.outdated.bitcoin.exchange.api.market.MarketDepth;
import at.outdated.bitcoin.exchange.api.market.MarketOrder;
import at.outdated.bitcoin.exchange.api.market.TickerValue;
import at.outdated.bitcoin.exchange.api.market.TradeDecision;

import javax.json.*;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import java.io.StringReader;

/**
 * Created with IntelliJ IDEA.
 * User: ebirn
 * Date: 30.05.13
 * Time: 12:50
 * To change this template use File | Settings | File Templates.
 */
public class BitcurexApiClient extends ExchangeApiClient {
    @Override
    public AccountInfo getAccountInfo() {

        /// see https://bitcurex.com/en-pages,eurapi.html

        // getOrders
        // getFunds
        // getTransactions



        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public MarketDepth getMarketDepth(Currency base, Currency quote) {

        WebTarget depthTarget = client.target("https://" + quote.name().toLowerCase() + ".bitcurex.com/data/trades.json");

        String raw = super.simpleGetRequest(depthTarget, String.class);

        JsonArray rawDepth =  Json.createReader(new StringReader(raw)).readArray();

        MarketDepth depth = new MarketDepth();
        depth.setBaseCurrency(base);
        for(JsonValue v : rawDepth) {
            JsonObject trade = (JsonObject) v;

            double price = trade.getJsonNumber("price").doubleValue();
            double volume = trade.getJsonNumber("amount").doubleValue();
            int type = trade.getJsonNumber("type").intValue();
            //trade.getJsonNumber("date");
            //trade.getJsonNumber("tid");

            // sell
            if(type == 1) {
                depth.getAsks().add(new MarketOrder(TradeDecision.SELL, new CurrencyValue(volume, base), new CurrencyValue(price, quote)));
            }
            // buy
            else {
                depth.getBids().add(new MarketOrder(TradeDecision.BUY, new CurrencyValue(volume, base), new CurrencyValue(price, quote)));
            }
        }


        return depth;
    }

    @Override
    protected <R> R simpleGetRequest(WebTarget resource, Class<R> resultClass) {
        String resultStr =  super.simpleGetRequest(resource, String.class);

        log.debug("BITCUREX raw: " + resultStr);

        R result = null;

        result = BitcurexJsonResolver.convertFromJson(resultStr, resultClass);

        return result;
    }

    @Override
    public TickerValue getTicker(Currency currency) {


        WebTarget tickerResource = client.target("https://" + currency.name().toLowerCase() + ".bitcurex.com/data/ticker.json");

        BitcurexTickerValue bTicker = simpleGetRequest(tickerResource, BitcurexTickerValue.class);

        if(bTicker == null) return null;

        TickerValue ticker = bTicker.getTickerValue();
        ticker.setCurrency(currency);
        return ticker;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Number getLag() {
        return 1.0;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    protected Invocation.Builder setupProtectedResource(WebTarget res) {
        return res.request();  //To change body of implemented methods use File | Settings | File Templates.
    }
}
