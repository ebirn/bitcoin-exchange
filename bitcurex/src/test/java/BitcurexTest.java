import at.outdated.bitcoin.exchange.api.ExchangeApiClient;
import at.outdated.bitcoin.exchange.api.account.AccountInfo;
import at.outdated.bitcoin.exchange.api.currency.Currency;
import at.outdated.bitcoin.exchange.api.market.MarketDepth;
import at.outdated.bitcoin.exchange.api.market.TickerValue;
import at.outdated.bitcoin.exchange.bitcurex.BitcurexApiClient;
import org.junit.Assert;
import org.junit.Test;

/**
 * Created with IntelliJ IDEA.
 * User: ebirn
 * Date: 30.05.13
 * Time: 13:11
 * To change this template use File | Settings | File Templates.
 */
public class BitcurexTest {

    ExchangeApiClient client = new  BitcurexApiClient();

    @Test
    public void testTicker() {




        TickerValue ticker = client.getTicker(Currency.EUR);

        Assert.assertNotNull("ticker value", ticker);

    }

    @Test
    public void testDepth() {

        MarketDepth depth = client.getMarketDepth(Currency.BTC, Currency.EUR);

        Assert.assertNotNull(depth);

    }


    @Test
    public void testAccountInfo() {

        AccountInfo info = client.getAccountInfo();

        Assert.assertNotNull(info);

    }


}
