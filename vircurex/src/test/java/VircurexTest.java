import at.outdated.bitcoin.exchange.api.BaseTest;
import at.outdated.bitcoin.exchange.api.market.Markets;

/**
 * Created by ebirn on 11.10.13.
 */
public class VircurexTest extends BaseTest {

    @Override
    public void init() {
        market = Markets.getMarket("vircurex");
        client = market.getApiClient();
    }
}
