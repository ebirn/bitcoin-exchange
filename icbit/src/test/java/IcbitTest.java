import at.outdated.bitcoin.exchange.api.currency.Currency;
import at.outdated.bitcoin.exchange.api.market.AssetPair;
import at.outdated.bitcoin.exchange.api.market.Markets;
import at.outdated.bitcoin.exchange.icbit.IcbitClient;
import org.junit.Test;

/**
 * Created by ebirn on 31.10.13.
 */
public class IcbitTest {

    IcbitClient client = new IcbitClient(Markets.getMarket("icbit"));

    @Test
    public void myTest() {
        //FIXME: cleanup
        //client.getTicker(new AssetPair(Currency.BTC, Currency.LTC));

    }
}
