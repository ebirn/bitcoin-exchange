import at.outdated.bitcoin.exchange.api.account.AccountInfo;
import at.outdated.bitcoin.exchange.api.currency.Currency;
import at.outdated.bitcoin.exchange.api.market.MarketDepth;
import at.outdated.bitcoin.exchange.api.market.TickerValue;
import at.outdated.bitcoin.exchange.bitstamp.BitstampClient;
import at.outdated.bitcoin.exchange.bitstamp.BitstampMarket;
import org.junit.Assert;
import org.junit.Test;

/**
 * Created with IntelliJ IDEA.
 * User: ebirn
 * Date: 25.05.13
 * Time: 14:03
 * To change this template use File | Settings | File Templates.
 */
public class BitstampTest {

    BitstampClient bitstampClient = new BitstampClient(new BitstampMarket());

    @Test
    public void testTicker() {

        TickerValue ticker = bitstampClient.getTicker(Currency.USD);
        Assert.assertNotNull("ticker value null", ticker);

    }

    @Test
    public void testAccountInfo() {

        AccountInfo info = bitstampClient.getAccountInfo();

        Assert.assertNotNull(info);
        Assert.assertNotNull(info.getWallet(Currency.USD));
        Assert.assertNotNull(info.getWallet(Currency.BTC));
    }


    @Test
    public void testMarketDepth() {

        MarketDepth d = bitstampClient.getMarketDepth(Currency.BTC, Currency.USD);
        Assert.assertNotNull(d);
    }
}
