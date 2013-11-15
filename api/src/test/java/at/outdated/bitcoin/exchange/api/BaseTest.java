package at.outdated.bitcoin.exchange.api;

import at.outdated.bitcoin.exchange.api.account.AccountInfo;
import at.outdated.bitcoin.exchange.api.client.ExchangeApiClient;
import at.outdated.bitcoin.exchange.api.currency.Currency;
import at.outdated.bitcoin.exchange.api.currency.CurrencyAddress;
import at.outdated.bitcoin.exchange.api.market.AssetPair;
import at.outdated.bitcoin.exchange.api.market.Market;
import at.outdated.bitcoin.exchange.api.market.MarketDepth;
import at.outdated.bitcoin.exchange.api.market.TickerValue;
import at.outdated.bitcoin.exchange.api.market.transfer.TransferMethod;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by ebirn on 29.10.13.
 */
public abstract class BaseTest {

    protected ExchangeApiClient client;
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

                log.info("deposit: {}", transferCurrency, address);
            }
        }
    }

    protected void assertAccountInfo(AccountInfo info) {

        Assert.assertNotNull(info);
        // TODO: more detailed checks
    }

    protected void assertDepth(MarketDepth depth){
        Assert.assertNotNull(depth);
        Assert.assertNotNull(depth.getBaseCurrency());

        Assert.assertNotNull(depth.getAsks());
        Assert.assertNotNull(depth.getBids());
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
