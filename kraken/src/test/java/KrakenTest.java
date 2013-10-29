import at.outdated.bitcoin.exchange.api.BaseTest;
import at.outdated.bitcoin.exchange.api.account.AccountInfo;
import at.outdated.bitcoin.exchange.api.currency.Currency;
import at.outdated.bitcoin.exchange.api.market.AssetPair;
import at.outdated.bitcoin.exchange.api.market.MarketDepth;
import at.outdated.bitcoin.exchange.api.market.Markets;
import at.outdated.bitcoin.exchange.api.market.TickerValue;
import at.outdated.bitcoin.exchange.kraken.*;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created with IntelliJ IDEA.
 * User: ebirn
 * Date: 17.09.13
 * Time: 18:46
 * To change this template use File | Settings | File Templates.
 */
public class KrakenTest extends BaseTest {


    @Before
    public void init() {
        market = Markets.getMarket("kraken");
        client = new KrakenClient(market);
        log = LoggerFactory.getLogger(getClass());
    }


    @Test
    public void testTickerclient() {

        TickerValue ticker = client.getTicker(new AssetPair(Currency.BTC, Currency.EUR));
        System.out.println("ticker: " + ticker);

        assertTicker(ticker);
    }

    @Test
    public void testDepthClient() {
        MarketDepth depth = client.getMarketDepth(new AssetPair(Currency.BTC, Currency.EUR));

        assertDepth(depth);
    }



}
