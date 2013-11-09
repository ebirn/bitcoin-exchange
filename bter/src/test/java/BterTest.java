import at.outdated.bitcoin.exchange.api.BaseTest;
import at.outdated.bitcoin.exchange.api.currency.Currency;
import at.outdated.bitcoin.exchange.api.market.AssetPair;
import at.outdated.bitcoin.exchange.api.market.Markets;
import at.outdated.bitcoin.exchange.api.market.TickerValue;
import at.outdated.bitcoin.exchange.bter.BterApiClient;
import org.junit.Assert;
import org.junit.Test;

/**
 * Created by ebirn on 11.10.13.
 */
public class BterTest extends BaseTest {


    @Override
    public void init() {
        market = Markets.getMarket("bter");
        client = new BterApiClient(market);
    }
}
