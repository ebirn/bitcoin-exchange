import at.outdated.bitcoin.exchange.api.BaseTest;
import at.outdated.bitcoin.exchange.api.Market;
import at.outdated.bitcoin.exchange.api.currency.Currency;
import at.outdated.bitcoin.exchange.api.market.AssetPair;
import at.outdated.bitcoin.exchange.api.market.MarketDepth;
import at.outdated.bitcoin.exchange.api.market.Markets;
import at.outdated.bitcoin.exchange.api.market.TickerValue;
import at.outdated.bitcoin.exchange.vircurex.VircurexApiClient;
import org.junit.Assert;
import org.junit.Test;

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
