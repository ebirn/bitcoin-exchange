

import at.outdated.bitcoin.exchange.api.BaseTest;
import at.outdated.bitcoin.exchange.api.OrderId;
import at.outdated.bitcoin.exchange.api.currency.Currency;
import at.outdated.bitcoin.exchange.api.currency.CurrencyValue;
import at.outdated.bitcoin.exchange.api.market.AssetPair;
import at.outdated.bitcoin.exchange.api.market.MarketOrder;
import at.outdated.bitcoin.exchange.api.market.Markets;
import at.outdated.bitcoin.exchange.api.market.TradeDecision;

import at.outdated.bitcoin.exchange.cryptsy.CryptsyApiClient;
import org.junit.Test;

import java.util.List;

/**
 * Created by ebirn on 11.10.13.
 */
public class CryptsyTest extends BaseTest {


    @Override
    public void init() {
        market = Markets.getMarket("cryptsy");
        client = new CryptsyApiClient(market);
    }

    @Test
    public void testListOrders() {

        List<MarketOrder> openOrders = client.getOpenOrders();

        for(MarketOrder order : openOrders) {
            log.info("order: {}", order.getId());
        }
    }

    @Test
    public void  testPlaceOrder() {

        AssetPair asset = market.getAsset(Currency.QRK, Currency.LTC);

        CurrencyValue volume = new CurrencyValue(5000, Currency.QRK);
        CurrencyValue price = new CurrencyValue(0.0001, Currency.LTC);

        OrderId id = client.placeOrder(asset, TradeDecision.BUY, volume, price);

        log.info("placed order: {}", id);

    }

    @Test
    public void testOpenOrders() {

        List<MarketOrder> openOrders = client.getOpenOrders();

        for(MarketOrder order : openOrders) {

            boolean success = client.cancelOrder(order.getId());

            log.info("cancel order: {} - success: {}", order.getId(), success);
        }
    }

}
