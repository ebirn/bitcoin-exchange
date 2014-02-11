package at.outdated.bitcoin.exchange;

import at.outdated.bitcoin.exchange.api.client.MarketClient;
import at.outdated.bitcoin.exchange.api.market.AssetPair;
import at.outdated.bitcoin.exchange.api.market.Market;
import at.outdated.bitcoin.exchange.api.market.Markets;
import at.outdated.bitcoin.exchange.api.market.TickerValue;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.theories.DataPoints;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;

import java.util.Set;

/**
 * Created by ebirn on 11.02.14.
 */

@RunWith(Theories.class)
public class TickerTest extends  BaseTest {


    public TickerTest(Market m) {
        super(m.getKey(), m, m.createClient());
    }


    @DataPoints
    public static TickerValue[] loadTickers() {

        Market m = Markets.getMarket("kraken");
        MarketClient client = m.createClient();

        AssetPair[] assets = new AssetPair[m.getTradedAssets().size()];
        m.getTradedAssets().toArray(assets);

        TickerValue[] values = new TickerValue[assets.length];



        for(int i =0; i< assets.length; i++) {
            values[i] = client.getTicker(assets[i]);
        }

        return values;
    }

    /*
    @Test
    public void testTicker() {
        TickerValue ticker = client.getTicker(asset);
        log.info("ticker {}: {}", asset, ticker);

        assertTicker(ticker);
    }
*/

    @Theory
    public void assertTicker(TickerValue ticker) {

        Assert.assertNotNull(ticker);

        notNull("invalid ticker timestamp", ticker.getTimestamp());
        notNull(ticker.getAsset());

        Assert.assertNotEquals(ticker.getBid(), 0.0, Double.MIN_NORMAL);
        Assert.assertNotEquals(ticker.getBid(), Double.NaN, 0.0);

        Assert.assertNotEquals(ticker.getAsk(), 0.0, Double.MIN_NORMAL);
        Assert.assertNotEquals(ticker.getAsk(), Double.NaN, 0.0);
    }
}
