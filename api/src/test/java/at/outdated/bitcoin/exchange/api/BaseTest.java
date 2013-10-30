package at.outdated.bitcoin.exchange.api;

import at.outdated.bitcoin.exchange.api.market.AssetPair;
import at.outdated.bitcoin.exchange.api.market.MarketDepth;
import at.outdated.bitcoin.exchange.api.market.Markets;
import at.outdated.bitcoin.exchange.api.market.TickerValue;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by ebirn on 29.10.13.
 */
public abstract class BaseTest {

    protected ExchangeApiClient client;
    protected Market market;

    protected Logger log = LoggerFactory.getLogger(getClass());

    @Test
    public void testAllTickers() {
        for(AssetPair asset : market.getTradedAssets()) {

            TickerValue ticker = client.getTicker(asset);
            log.info("ticker {}: {}", asset, ticker);

            assertTicker(ticker);
        }
    }

    @Test
    public void testAllDepth() {
        for(AssetPair asset : market.getTradedAssets()) {
            MarketDepth depth = client.getMarketDepth(asset);
            log.info("depth: {}: {}", asset, depth);

            assertDepth(depth);
        }
    }

    protected void assertDepth(MarketDepth depth){
        Assert.assertNotNull(depth);
        Assert.assertNotNull(depth.getBaseCurrency());

        Assert.assertNotNull(depth.getAsks());
        Assert.assertNotNull(depth.getBids());
    }

    protected void assertTicker(TickerValue ticker) {
        Assert.assertNotNull(ticker.getBid());
        Assert.assertNotEquals(ticker.getBid(), Double.NaN, 0.0);

        Assert.assertNotNull(ticker.getAsk());
        Assert.assertNotEquals(ticker.getAsk(), Double.NaN, 0.0);

        Assert.assertNotNull(ticker.getCurrency());

    }
}
