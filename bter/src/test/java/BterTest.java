import at.outdated.bitcoin.exchange.api.currency.Currency;
import at.outdated.bitcoin.exchange.api.market.Markets;
import at.outdated.bitcoin.exchange.api.market.TickerValue;
import at.outdated.bitcoin.exchange.bter.BterApiClient;
import org.junit.Assert;
import org.junit.Test;

/**
 * Created by ebirn on 11.10.13.
 */
public class BterTest {

    BterApiClient client = new BterApiClient(Markets.getMarket("bter"));

    @Test
    public void testTicker() {

        TickerValue ticker = client.getTicker(Currency.LTC, Currency.EUR);

        Assert.assertNotNull(ticker);
    }

}
