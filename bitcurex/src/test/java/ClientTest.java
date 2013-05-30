import at.outdated.bitcoin.exchange.api.ExchangeApiClient;
import at.outdated.bitcoin.exchange.api.currency.Currency;
import at.outdated.bitcoin.exchange.api.market.TickerValue;
import at.outdated.bitcoin.exchange.bitcurex.BitcurexApiClient;
import junit.framework.Assert;
import org.junit.Test;

/**
 * Created with IntelliJ IDEA.
 * User: ebirn
 * Date: 30.05.13
 * Time: 13:11
 * To change this template use File | Settings | File Templates.
 */
public class ClientTest {


    @Test
    public void testTicker() {


        ExchangeApiClient client = new  BitcurexApiClient();

        TickerValue ticker = client.getTicker(Currency.EUR);

        Assert.assertNotNull("ticker value", ticker);

    }


}
