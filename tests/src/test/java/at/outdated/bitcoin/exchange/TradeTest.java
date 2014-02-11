package at.outdated.bitcoin.exchange;

import at.outdated.bitcoin.exchange.api.OrderId;
import at.outdated.bitcoin.exchange.api.account.AccountInfo;
import at.outdated.bitcoin.exchange.api.client.ExchangeClient;
import at.outdated.bitcoin.exchange.api.currency.Currency;
import at.outdated.bitcoin.exchange.api.currency.CurrencyAddress;
import at.outdated.bitcoin.exchange.api.market.Market;
import at.outdated.bitcoin.exchange.api.market.MarketOrder;
import at.outdated.bitcoin.exchange.api.market.Markets;
import at.outdated.bitcoin.exchange.api.market.transfer.TransferMethod;
import org.apache.commons.lang3.StringUtils;
import org.junit.Assert;
import org.junit.Assume;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.*;

/**
 * Created by ebirn on 20.01.14.
 */
@RunWith(value=Parameterized.class)
public class TradeTest extends BaseTest {

    protected OrderId invalidOrderId;
    protected OrderId validOrderId;

    protected MarketOrder validOrder;
    protected MarketOrder invalidOrder;

    @Parameterized.Parameters(name = "{0}TradeTest")
    public static Collection<Object[]> getMarketParams() {
        //return BaseTest.getMarketParams();

        List<Object[]> params = new ArrayList<>();
        params.add(marketParams(Markets.getMarket("coinse")));
        //params.add(marketParams(Markets.getMarket("bitcurex")));
        //params.add(marketParams(Markets.getMarket("bitkonan")));

        return params;
    }


    public TradeTest(String key, Market m, ExchangeClient c) {
        super(key, m, c);

        invalidOrderId = new OrderId(m, "xxx111222333xxx");
        validOrderId = new OrderId(m, "S/100.00010101/6431215947087872");
    }


    @Test
    public void testOpenOrders() {

        List<MarketOrder> orders = client.getOpenOrders();

        Assert.assertNotNull("returned order list is NULL", orders);

        for(MarketOrder order : orders) {
            log.info("order: {}", order);
        }

    }


    @Test(expected=AssertionError.class)
    public void testCancelInvalidOrderId() {

        testCancelOrder(invalidOrderId);
    }

    @Test
    public void testCancelOrder() {
        testCancelOrder(validOrderId);
    }


    public void testCancelOrder(OrderId id) {

        //OrderId id = new OrderId(market, "xxx7135167xxx");

        boolean didIt = client.cancelOrder(id);

        Assert.assertTrue("faied to cancel order " + id, didIt);

        log.info("removed order {}: {}", id.getIdentifier(), didIt);
    }


    @Test
    public void testPlaceValidOrder() {
        testPlaceOrder(validOrder);
    }


    public void  testPlaceOrder(MarketOrder order) {

        OrderId id = client.placeOrder(order);

        Assert.assertNotNull("result object", id);
        Assert.assertNotNull("order id identifier", id.getIdentifier());
        Assert.assertNotNull("no market set", id.getMarket());

        log.info("placed order: {}", id);
    }


    @Test
    public void testDepositAddress() {

        for(TransferMethod method : market.getDepositMethods()) {
            Currency transferCurrency = method.getCurrency();

            if(transferCurrency.isCrypto()) {

                CurrencyAddress address = client.getDepositAddress(transferCurrency);
                assertCurrencyAddress(address, transferCurrency);

                log.info("deposit: {} - {}", transferCurrency, address);
            }
        }
    }

    @Test(expected = IllegalArgumentException.class)
    public void testInvalidDepositCurrency() {

        Set<Currency> invalidCurrencies = new HashSet();
        invalidCurrencies.addAll(Arrays.asList(Currency.values()));


        for(TransferMethod m : market.getDepositMethods()) {
            invalidCurrencies.remove(m.getCurrency());
        }

        Iterator<Currency> ci = invalidCurrencies.iterator();
        while(ci.hasNext()) {
            Currency c = ci.next();
            if(!c.isCrypto())
                ci.remove();
        }

        boolean hasInvalids = !invalidCurrencies.isEmpty();
        Assume.assumeTrue("can deposit all currencies, cannot test invalid curr", hasInvalids);

        //TODO: is this if necessary at all?
        if(hasInvalids) {
            Currency invalid = invalidCurrencies.iterator().next();

            // this must throw up
            client.getDepositAddress(invalid);
        }

    }

}
