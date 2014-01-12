import at.outdated.bitcoin.exchange.api.BaseTest;
import at.outdated.bitcoin.exchange.api.currency.Currency;
import at.outdated.bitcoin.exchange.api.currency.CurrencyValue;
import at.outdated.bitcoin.exchange.api.market.*;
import at.outdated.bitcoin.exchange.bter.BterApiClient;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import java.util.List;

/**
 * Created by ebirn on 11.10.13.
 */
public class BterTest extends BaseTest {


    @Override
    public void init() {
        market = Markets.getMarket("bter");
        client = new BterApiClient(market);
    }

    @Ignore
    @Test
    public void  testPlaceOrder() {

        AssetPair asset = new AssetPair(Currency.LTC, Currency.BTC);

        CurrencyValue volume = new CurrencyValue(0.1, Currency.LTC);
        CurrencyValue price = new CurrencyValue(100.0, Currency.BTC);

        client.placeOrder(asset, TradeDecision.SELL, volume, price);


    }

    @Test
    public void testListOpenOrders() {
        List<MarketOrder> openOrders = client.getOpenOrders();

        for(MarketOrder order : openOrders) {
            log.info("open: {}", order);
        }

    }

    @Ignore
    @Test
    public void testMultiCall() {

        for(int i=0; i< 100; i++) {
            try {
                log.info("run: {}", (i+1));
                testListOpenOrders();
            }
            catch(Exception e) {
                log.warn("failed request", e);
            }
        }

    }

    @Ignore
    @Test
    public void testOpenOrders() {

        List<MarketOrder> openOrders = client.getOpenOrders();

        for(MarketOrder order : openOrders) {


            boolean success = client.cancelOrder(order.getId());

            log.info("cancel order: {} - success: {}", order.getId(), success);
        }

    }
}
