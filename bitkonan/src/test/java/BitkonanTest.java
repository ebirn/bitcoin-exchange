import at.outdated.bitcoin.exchange.api.BaseTest;
import at.outdated.bitcoin.exchange.api.account.AccountInfo;
import at.outdated.bitcoin.exchange.api.currency.Currency;
import at.outdated.bitcoin.exchange.api.market.AssetPair;
import at.outdated.bitcoin.exchange.api.market.MarketDepth;
import at.outdated.bitcoin.exchange.api.market.Markets;
import at.outdated.bitcoin.exchange.api.market.TickerValue;
import at.outdated.bitcoin.exchange.bitkonan.BitkonanApiClient;
import at.outdated.bitcoin.exchange.bitkonan.BitkonanMarket;
import org.junit.Assert;
import org.junit.Test;

/**
 * Created with IntelliJ IDEA.
 * User: ebirn
 * Date: 27.05.13
 * Time: 00:00
 * To change this template use File | Settings | File Templates.
 */
public class BitkonanTest extends BaseTest {


    @Override
    public void init() {

        market = Markets.getMarket("bitkonan");
        client = new BitkonanApiClient(market);
    }

    @Test
    public void testTicker() {


        TickerValue ticker = client.getTicker(new AssetPair(Currency.BTC, Currency.USD));

        Assert.assertNotNull(ticker);
        Assert.assertNotNull(ticker.getTimestamp());

        System.out.println("ticker: "+ ticker.getTimestamp() +"  "  + ticker);


    }


}
