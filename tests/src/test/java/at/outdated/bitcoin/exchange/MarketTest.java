package at.outdated.bitcoin.exchange;

import at.outdated.bitcoin.exchange.api.currency.CurrencyValue;
import at.outdated.bitcoin.exchange.api.market.*;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.slf4j.LoggerFactory;

import java.util.Collection;

/**
 * Created by ebirn on 20.01.14.
 */
@RunWith(value=Parameterized.class)
public class MarketTest extends BaseTest {

    @Parameterized.Parameters(name = "{0}MarketTest")
    public static Collection<Object[]> getMarketParams() {
        return BaseTest.getMarketParams();
    }


    public MarketTest(String key, Market m) {
        super(key, m);
        log = LoggerFactory.getLogger("test.market." + m.getKey());
        log.info("MarketTest: {}", m.getKey());
    }

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
        Assert.assertNotNull(depth.getAsset());

        Assert.assertNotNull(depth.getAsks());
        for(MarketOrder order : depth.getAsks()) {
            Assert.assertEquals(depth.getAsset(), order.getAsset());
            Assert.assertEquals(OrderType.ASK, order.getType());
        }

        Assert.assertNotNull(depth.getBids());
        for(MarketOrder order : depth.getBids()) {
            Assert.assertEquals(depth.getAsset(), order.getAsset());
            Assert.assertEquals(OrderType.BID, order.getType());
        }

        if(!depth.getAsks().isEmpty() && !depth.getBids().isEmpty()) {
            Assume.assumeFalse("asks empty", depth.getAsks().isEmpty());
            Assume.assumeFalse("bids empty", depth.getBids().isEmpty());


            MarketOrder firstAsk = depth.getAsks().first();
            MarketOrder firstBid = depth.getBids().first();

            CurrencyValue askPrice = firstAsk.getPrice();
            CurrencyValue bidPrice = firstBid.getPrice();

            Assert.assertTrue(" sell higher than buy? ", askPrice.isMoreThan(bidPrice));

            for(MarketOrder order : depth.getAsks()) {
                Assert.assertTrue("ask price not ascending", askPrice.doubleValue() <= order.getPrice().doubleValue());
                askPrice = order.getPrice();
            }

            for(MarketOrder order : depth.getBids()) {
                Assert.assertTrue("bid price not descending", bidPrice.doubleValue() >= order.getPrice().doubleValue());
                bidPrice = order.getPrice();
            }
        }
        else {
            log.info("market depth is empty, skipping bid/ask testing");
        }
    }

    protected void assertTicker(TickerValue ticker) {
        Assert.assertNotNull(ticker);

        Assert.assertNotNull(ticker.getBid());
        Assert.assertNotEquals(ticker.getBid(), Double.NaN, 0.0);

        Assert.assertNotNull(ticker.getAsk());
        Assert.assertNotEquals(ticker.getAsk(), Double.NaN, 0.0);

        Assert.assertNotNull(ticker.getAsset());

    }
}
