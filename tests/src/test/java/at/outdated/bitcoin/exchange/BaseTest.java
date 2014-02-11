package at.outdated.bitcoin.exchange;

import at.outdated.bitcoin.exchange.api.client.ExchangeClient;
import at.outdated.bitcoin.exchange.api.currency.Currency;
import at.outdated.bitcoin.exchange.api.currency.CurrencyAddress;
import at.outdated.bitcoin.exchange.api.market.AssetPair;
import at.outdated.bitcoin.exchange.api.market.Market;
import at.outdated.bitcoin.exchange.api.market.Markets;
import org.apache.commons.lang3.StringUtils;
import org.hamcrest.core.AnyOf;
import org.hamcrest.core.IsCollectionContaining;
import org.hamcrest.core.IsNull;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.rules.ErrorCollector;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * Created by ebirn on 29.10.13.
 */
public abstract class BaseTest {

    protected ExchangeClient client;

    protected Market market;
    protected AssetPair asset;

    protected Logger log = LoggerFactory.getLogger(getClass());

    protected static Object[] marketParams(Market m) {

        String niceKey = StringUtils.capitalize(m.getKey());

        return new Object[] { niceKey, m };
    }

    protected static Object[] marketParams(Market m, AssetPair asset) {

        String niceKey = StringUtils.capitalize(m.getKey());

        return new Object[] { niceKey, m, asset };
    }

    public static Collection<Object[]> getMarketParams() {

        Set<Market> allMarkets = Markets.allMarkets();
        Market[] arr = new Market[allMarkets.size()];
        allMarkets.toArray(arr);
        return getMarketParams(arr);
    }

    public static Collection<Object[]> getAssetMarketParams() {

        Set<Market> allMarkets = Markets.allMarkets();

        Market[] arr = new Market[allMarkets.size()];
        allMarkets.toArray(arr);
        return getAssetMarketParams(arr);
    }


    public static Collection<Object[]> getMarketParams(Market... markets ) {

        ArrayList<Object[]> params = new ArrayList<>(markets.length);

        for(Market m : markets) {
            params.add(marketParams(m));
        }

        return params;
    }

    public static Collection<Object[]> getAssetMarketParams(Market... markets ) {

        ArrayList<Object[]> params = new ArrayList<>(markets.length);

        for(Market m : markets) {
            for(AssetPair asset : m.getTradedAssets()) {
                params.add(marketParams(m, asset));
            }
        }

        return params;
    }

    public BaseTest(String key, Market m) {
        this.market = m;
        this.client = m.createClient();
        log = LoggerFactory.getLogger("test." + m.getKey());
    }



    protected void assertCurrencyAddress(CurrencyAddress address, Currency currency) {

        Assert.assertNotNull("currency address is NULL", address);
        Assert.assertEquals("currency mismatch", currency, address.getReference());
    }

    @Rule
    public ErrorCollector errorCollector = new ErrorCollector();


    protected void notNull(Object obj) {
        errorCollector.checkThat(obj, IsNull.notNullValue());
    }

    protected void notNull(String reason, Object obj) {
        errorCollector.checkThat(reason, obj, IsNull.notNullValue());
    }

    public void notEmpty(Collection coll) {
        //errorCollector.checkThat(coll, Is.);
    }
}
