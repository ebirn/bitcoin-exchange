package at.outdated.bitcoin.exchange;

import at.outdated.bitcoin.exchange.api.client.ExchangeClient;
import at.outdated.bitcoin.exchange.api.currency.CurrencyValue;
import at.outdated.bitcoin.exchange.api.market.*;
import org.junit.*;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.Date;
import java.util.List;

/**
 * Created by ebirn on 20.01.14.
 */
@RunWith(value=Parameterized.class)
public class MarketTest extends BaseTest {

    @Parameterized.Parameters(name = "{0}MarketTest {2}")
    public static Collection<Object[]> getMarketParams() {

        //return BaseTest.getMarketParams(Markets.getMarket("cryptsy"), Markets.getMarket("bitstamp"));
        return BaseTest.getAssetMarketParams();
    }


    public MarketTest(String key, Market m, AssetPair asset, ExchangeClient client) {
        super(key, m, client);
        log = LoggerFactory.getLogger("test.market." + m.getKey());
        log.info("MarketTest: {} {}", m.getKey(), asset);
        this.asset = asset;
    }

    @Test
    public void testTicker() {
        TickerValue ticker = client.getTicker(asset);
        log.info("ticker {}: {}", asset, ticker);

        assertTicker(ticker);
    }

    @Test
    public void testDepth() {

        MarketDepth depth = client.getMarketDepth(asset);
        log.info("depth: {}: {}", asset, depth);

        assertDepth(depth);
    }

    @Test
    public void testTradeHistory() {

        List<MarketOrder> history = client.getTradeHistory(asset, new Date(0L));

        String msg = "history is NULL for " + asset + " @ " + market.getKey();
        notNull(msg, history);

        //Assert.assertFalse("history is empty", history.isEmpty());

        if(history != null) {
            log.info("history: {} #{}", asset, history.size());
            //TODO verify elements?
            for(MarketOrder order : history) {
                checkOrder(order);
            }
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



            // this can occur in the wild, may mess up later asserts
            Assume.assumeTrue("sell higher than buy?", askPrice.isMoreThan(bidPrice));


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

        Assume.assumeNotNull(ticker);

        notNull("invalid ticker timestamp", ticker.getTimestamp());
        notNull(ticker.getAsset());

        Assert.assertNotEquals(ticker.getBid(), 0.0, Double.MIN_NORMAL);
        Assert.assertNotEquals(ticker.getBid(), Double.NaN, 0.0);

        Assert.assertNotEquals(ticker.getAsk(), 0.0, Double.MIN_NORMAL);
        Assert.assertNotEquals(ticker.getAsk(), Double.NaN, 0.0);
    }

    protected void checkOrder(MarketOrder order) {

        notNull("whole order", order);

        notNull("order:id", order.getId());

        notNull("order:asset", order.getAsset());
        notNull("order:price", order.getPrice());
        notNull("order:volume", order.getVolume());

        notNull("order:timestamp", order.getTimestamp());
        notNull("order:type", order.getType());

    }

}
