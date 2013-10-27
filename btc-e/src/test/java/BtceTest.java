import at.outdated.bitcoin.exchange.api.account.AccountInfo;
import at.outdated.bitcoin.exchange.api.currency.Currency;
import at.outdated.bitcoin.exchange.api.market.AssetPair;
import at.outdated.bitcoin.exchange.api.market.MarketDepth;
import at.outdated.bitcoin.exchange.api.market.TickerValue;
import at.outdated.bitcoin.exchange.btce.BtcEApiClient;
import at.outdated.bitcoin.exchange.btce.BtcEMarket;
import org.junit.Assert;
import org.junit.Test;

/**
 * Created with IntelliJ IDEA.
 * User: ebirn
 * Date: 27.05.13
 * Time: 00:00
 * To change this template use File | Settings | File Templates.
 */
public class BtceTest {

    BtcEApiClient client = new BtcEApiClient(new BtcEMarket());

    @Test
    public void testTicker() {

        TickerValue ticker = client.getTicker(new AssetPair(Currency.BTC, Currency.EUR));

        Assert.assertNotNull(ticker);
        Assert.assertNotNull(ticker.getTimestamp());

        System.out.println("ticker: "+ ticker.getTimestamp() +"  " + ticker);

        ticker = client.getTicker(new AssetPair(Currency.LTC, Currency.EUR));

        Assert.assertNotNull(ticker);
        Assert.assertNotNull(ticker.getTimestamp());

        System.out.println("ticker: "+ ticker.getTimestamp() +"  "  + ticker);
    }

    @Test
    public void testDepth() {
        MarketDepth depth = client.getMarketDepth(new AssetPair(Currency.BTC, Currency.EUR));

        Assert.assertNotNull(depth);
        Assert.assertNotNull(depth.getBaseCurrency());
        Assert.assertFalse(depth.getAsks().isEmpty());
        Assert.assertFalse(depth.getBids().isEmpty());
    }


    @Test
    public void testAccountInfo() {

        AccountInfo info = client.getAccountInfo();
        Assert.assertNotNull(info);
    }
}
