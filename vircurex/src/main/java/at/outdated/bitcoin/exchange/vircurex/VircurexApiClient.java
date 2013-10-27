package at.outdated.bitcoin.exchange.vircurex;

import at.outdated.bitcoin.exchange.api.ExchangeApiClient;
import at.outdated.bitcoin.exchange.api.Market;
import at.outdated.bitcoin.exchange.api.account.AccountInfo;
import at.outdated.bitcoin.exchange.api.currency.Currency;
import at.outdated.bitcoin.exchange.api.currency.CurrencyValue;
import at.outdated.bitcoin.exchange.api.market.*;

import javax.json.JsonObject;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;

/**
 * Created by ebirn on 11.10.13.
 */
public class VircurexApiClient extends ExchangeApiClient {

    public VircurexApiClient(Market market) {
        super(market);
    }

    @Override
    public AccountInfo getAccountInfo() {



        return new VircurexAccountInfo();
    }

    @Override
    public TickerValue getTicker(AssetPair asset) {

        // get_info_for_1_currency
        WebTarget tickerTgt = client.target("https://vircurex.com/api/get_info_for_1_currency.json");
        tickerTgt.queryParam("base", asset.getBase());
        tickerTgt.queryParam("alt", asset.getQuote());
        // {"base":"BTC","alt":"LTC","lowest_ask":"62.30141425","highest_bid":"61.12503063","last_trade":"62.30529595","volume":"82.92624028"}%

        VircurexTicker ticker = simpleGetRequest(tickerTgt, VircurexTicker.class);

        return ticker.getValue();
    }

    @Override
    public Number getLag() {
        return null;
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
            CurrencyValue volume = new CurrencyValue(bid[0], asset.getBase());
            CurrencyValue price = new CurrencyValue(bid[1], asset.getQuote());
            depth.addBid(new MarketOrder(TradeDecision.BUY, volume, price));
        }

        double[][] asks = this.parseNestedArray(jsonDepth.getJsonArray("asks"));
        for(double[] ask : asks) {
            CurrencyValue volume = new CurrencyValue(ask[0], asset.getBase());
            CurrencyValue price = new CurrencyValue(ask[1], asset.getQuote());
            depth.addAsk(new MarketOrder(TradeDecision.BUY, volume, price));
        }

        }
        catch(ClassCastException cce) {
            log.info("canot parse depth, probably empty?");
            return null;
        }
        return depth;
    }

    @Override
    protected <T> Invocation.Builder setupProtectedResource(WebTarget res, Entity<T> entity) {
        return null;
    }
}
