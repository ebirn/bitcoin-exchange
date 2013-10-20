import at.outdated.bitcoin.exchange.api.account.AccountInfo;
import at.outdated.bitcoin.exchange.api.currency.Currency;
import at.outdated.bitcoin.exchange.api.market.AssetPair;
import at.outdated.bitcoin.exchange.api.market.MarketDepth;
import at.outdated.bitcoin.exchange.api.market.TickerValue;
import at.outdated.bitcoin.exchange.kraken.*;
import org.junit.Assert;
import org.junit.Test;

/**
 * Created with IntelliJ IDEA.
 * User: ebirn
 * Date: 17.09.13
 * Time: 18:46
 * To change this template use File | Settings | File Templates.
 */
public class KrakenTest {

    KrakenClient client = new KrakenClient(new KrakenMarket());

    @Test
    public void testTickerclient() {

        TickerValue ticker = client.getTicker(new AssetPair(Currency.BTC, Currency.EUR));

        System.out.println("ticker: " + ticker);

        Assert.assertNotNull(ticker.getBid());
        Assert.assertNotNull(ticker.getAsk());
        Assert.assertNotNull(ticker.getCurrency());
        Assert.assertNotNull(ticker.getCurrency());
    }

    @Test
    public void testDepthClient() {
        MarketDepth depth = client.getMarketDepth(Currency.BTC, Currency.EUR);

        System.out.println("depth: " + depth);

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
