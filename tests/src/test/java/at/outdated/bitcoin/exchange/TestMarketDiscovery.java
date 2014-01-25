package at.outdated.bitcoin.exchange;

import at.outdated.bitcoin.exchange.api.market.Market;
import at.outdated.bitcoin.exchange.api.market.Markets;
import org.junit.Test;
import org.slf4j.LoggerFactory;

/**
 * Created with IntelliJ IDEA.
 * User: ebirn
 * Date: 23.05.13
 * Time: 21:41
 * To change this template use File | Settings | File Templates.
 */
public class TestMarketDiscovery {

    @Test
    public void testLoadMarkets() {

        for(Market m : Markets.allMarkets()) {
            //System.out.println("market: " + m);
            //TickerValue ticker = m.createClient().getUpdate(Currency.USD);
            LoggerFactory.getLogger(TestMarketDiscovery.class).info("check: {}", m);
        }

    }

}
