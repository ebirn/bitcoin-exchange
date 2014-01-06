

import at.outdated.bitcoin.exchange.api.BaseTest;
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

    /*
    @Test
    public void  testPlaceOrder() {

        AssetPair asset = new AssetPair(Currency.LTC, Currency.BTC);

        CurrencyValue volume = new CurrencyValue(0.1, Currency.LTC);
        CurrencyValue price = new CurrencyValue(100.0, Currency.BTC);

        client.placeOrder(asset, TradeDecision.SELL, volume, price);


    }

    @Test
    public void testOpenOrders() {

        List<MarketOrder> openOrders = client.getOpenOrders();

        for(MarketOrder order : openOrders) {


            boolean success = client.cancelOrder(order.getId());

            log.info("cancel order: {} - success: {}", order.getId(), success);
        }
    }
    */
}
