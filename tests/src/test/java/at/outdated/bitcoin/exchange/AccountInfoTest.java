package at.outdated.bitcoin.exchange;

import at.outdated.bitcoin.exchange.api.account.AccountInfo;
import at.outdated.bitcoin.exchange.api.account.Balance;
import at.outdated.bitcoin.exchange.api.currency.Currency;
import at.outdated.bitcoin.exchange.api.market.Market;
import at.outdated.bitcoin.exchange.api.market.Markets;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by ebirn on 25.01.14.
 */
@RunWith(value=Parameterized.class)
public class AccountInfoTest extends BaseTest {


    public AccountInfoTest(String key, Market m) {
        super(key, m);
    }

    @Parameterized.Parameters(name = "{0}AccountInfoTest")
    public static Collection<Object[]> getMarketParams() {
        return BaseTest.getMarketParams();

        /*
        List<Object[]> params = new ArrayList<>();
        params.add(marketParams(Markets.getMarket("bitkonan")));
        params.add(marketParams(Markets.getMarket("bitcurex")));
        params.add(marketParams(Markets.getMarket("btce")));

        return params;
        */
    }

    @Test
    public void simpleTest() {
        log.info("testing the testing test: {}", this.market);
    }

    @Test
    public void balanceTest() {

        Balance balance = client.getBalance();

        log.info("balance on {}", market);
        for(Currency c : market.getCurrencies()) {
            log.info("  available: {}   | open {}", balance.getAvailable(c), balance.getOpen(c));
        }

    }

    @Ignore
    @Test
    public void testAccountInfo() {
        AccountInfo info = client.getAccountInfo();

        assertAccountInfo(info);
    }

    protected void assertAccountInfo(AccountInfo info) {

        Assert.assertNotNull(info);
        // FIXME: more detailed checks
    }
}
