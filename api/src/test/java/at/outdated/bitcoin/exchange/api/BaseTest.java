package at.outdated.bitcoin.exchange.api;

import at.outdated.bitcoin.exchange.api.account.AccountInfo;
import at.outdated.bitcoin.exchange.api.client.ExchangeClient;
import at.outdated.bitcoin.exchange.api.currency.Currency;
import at.outdated.bitcoin.exchange.api.currency.CurrencyAddress;
import at.outdated.bitcoin.exchange.api.currency.CurrencyValue;
import at.outdated.bitcoin.exchange.api.market.*;
import at.outdated.bitcoin.exchange.api.market.transfer.TransferMethod;
import org.junit.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * Created by ebirn on 29.10.13.
 */
public abstract class BaseTest {

    protected ExchangeClient client;
    protected Market market;

    protected Logger log = LoggerFactory.getLogger(getClass());

    @Before
    public abstract void init();

    @Test
    public void testAllTickers() {
        for(AssetPair asset : market.getTradedAssets()) {

            TickerValue ticker = client.getTicker(asset);
            log.info("ticker {}: {}", asset, ticker);

            assertTicker(ticker);
        }
    }

    @Test
    public void testAllDepth() {
        for(AssetPair asset : market.getTradedAssets()) {
            MarketDepth depth = client.getMarketDepth(asset);
            log.info("depth: {}: {}", asset, depth);

            assertDepth(depth);
        }
    }


    @Test
    public void testAccountInfo() {
        AccountInfo info = client.getAccountInfo();

        assertAccountInfo(info);
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

    protected void assertAccountInfo(AccountInfo info) {

        Assert.assertNotNull(info);
        // FIXME: more detailed checks
    }

    protected void assertDepth(MarketDepth depth){
        Assert.assertNotNull(depth);
        Assert.assertNotNull(depth.getAsset());

        Assert.assertNotNull(depth.getAsks());
        for(MarketOrder order : depth.getAsks()) {
            Assert.assertEquals(order.getAsset(), depth.getAsset());
            Assert.assertEquals(order.getDecision(), TradeDecision.BUY);
        }

        Assert.assertNotNull(depth.getBids());
        for(MarketOrder order : depth.getBids()) {
            Assert.assertEquals(order.getAsset(), depth.getAsset());
            Assert.assertEquals(order.getDecision(), TradeDecision.SELL);
        }

        if(!depth.getAsks().isEmpty() && !depth.getBids().isEmpty()) {
            Assume.assumeFalse("asks empty", depth.getAsks().isEmpty());
            Assume.assumeFalse("bids empty", depth.getBids().isEmpty());


            MarketOrder firstAsk = depth.getAsks().first();
            MarketOrder firstBid = depth.getBids().first();

            CurrencyValue askPrice = firstAsk.getPrice();
            CurrencyValue bidPrice = firstBid.getPrice();

            Assert.assertTrue(" sell higher than buy? ", askPrice.isMoreThan(bidPrice));

            for(MarketOrder order : depth.getAsks()) {
                Assert.assertTrue("ask price not ascending", askPrice.getValue() <= order.getPrice().getValue());
                askPrice = order.getPrice();
            }

            for(MarketOrder order : depth.getBids()) {
                Assert.assertTrue("bid price not descending", bidPrice.getValue() >= order.getPrice().getValue());
                bidPrice = order.getPrice();
            }
        }
        else {
            log.info("market depth is empty, skipping bid/ask testing");
        }
    }

    protected void assertTicker(TickerValue ticker) {
        Assert.assertNotNull(ticker);

        Assert.assertNotNull(ticker.getBid());
        Assert.assertNotEquals(ticker.getBid(), Double.NaN, 0.0);

        Assert.assertNotNull(ticker.getAsk());
        Assert.assertNotEquals(ticker.getAsk(), Double.NaN, 0.0);

        Assert.assertNotNull(ticker.getAsset());

    }


    protected void assertCurrencyAddress(CurrencyAddress address, Currency currency) {

        Assert.assertNotNull("currency address is NULL", address);
        Assert.assertEquals("currency mismatch", currency, address.getReference());
    }
}
