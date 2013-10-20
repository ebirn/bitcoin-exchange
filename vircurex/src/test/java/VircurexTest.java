import at.outdated.bitcoin.exchange.api.currency.Currency;
import at.outdated.bitcoin.exchange.api.market.AssetPair;
import at.outdated.bitcoin.exchange.api.market.Markets;
import at.outdated.bitcoin.exchange.api.market.TickerValue;
import at.outdated.bitcoin.exchange.vircurex.VircurexApiClient;
import org.junit.Assert;
import org.junit.Test;

/**
 * Created by ebirn on 11.10.13.
 */
public class VircurexTest {

    VircurexApiClient client = new VircurexApiClient(Markets.getMarket("vircurex"));

    @Test
    public void testTicker() {

        TickerValue ticker = client.getTicker(new AssetPair(Currency.BTC, Currency.EUR));

        Assert.assertNotNull(ticker);
    }

}
