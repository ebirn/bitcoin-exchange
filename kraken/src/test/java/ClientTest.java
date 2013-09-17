import at.outdated.bitcoin.exchange.api.currency.Currency;
import at.outdated.bitcoin.exchange.api.market.TickerValue;
import at.outdated.bitcoin.exchange.kraken.KrakenClient;
import org.junit.Assert;
import org.junit.Test;

/**
 * Created with IntelliJ IDEA.
 * User: ebirn
 * Date: 17.09.13
 * Time: 18:46
 * To change this template use File | Settings | File Templates.
 */
public class ClientTest {



    @Test
    public void testclient() {


        KrakenClient client = new KrakenClient();

        TickerValue ticker = client.getTicker(Currency.EUR);

        Assert.assertNotNull(ticker.getBid());
        Assert.assertNotNull(ticker.getAsk());
        Assert.assertNotNull(ticker.getCurrency());


        System.out.println(ticker);

    }
}
