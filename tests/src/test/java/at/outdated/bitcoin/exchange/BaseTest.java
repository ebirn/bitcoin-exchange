package at.outdated.bitcoin.exchange;

import at.outdated.bitcoin.exchange.api.client.ExchangeClient;
import at.outdated.bitcoin.exchange.api.currency.Currency;
import at.outdated.bitcoin.exchange.api.currency.CurrencyAddress;
import at.outdated.bitcoin.exchange.api.market.Market;
import at.outdated.bitcoin.exchange.api.market.Markets;
import org.junit.Assert;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by ebirn on 29.10.13.
 */
@RunWith(value=Parameterized.class)
public abstract class BaseTest {

    protected ExchangeClient client;
    protected Market market;

    protected Logger log = LoggerFactory.getLogger(getClass());

    @Parameterized.Parameters
    public static Collection<Object[]> getPlugins() {

        ArrayList<Object[]> params = new ArrayList<>();
        for(Market m : Markets.allMarkets()) {
            params.add(new Object[]{ m });
        }

        return params;
    }

    public BaseTest(Market m) {
        this.market = m;
        this.client = m.createClient();
        log = LoggerFactory.getLogger("test." + m.getKey());
    }



    protected void assertCurrencyAddress(CurrencyAddress address, Currency currency) {

        Assert.assertNotNull("currency address is NULL", address);
        Assert.assertEquals("currency mismatch", currency, address.getReference());
    }
}
