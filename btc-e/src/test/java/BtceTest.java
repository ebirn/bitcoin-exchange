import at.outdated.bitcoin.exchange.api.BaseTest;
import at.outdated.bitcoin.exchange.api.OrderId;
import at.outdated.bitcoin.exchange.api.account.AccountInfo;
import at.outdated.bitcoin.exchange.api.currency.Currency;
import at.outdated.bitcoin.exchange.api.currency.CurrencyValue;
import at.outdated.bitcoin.exchange.api.market.*;
import at.outdated.bitcoin.exchange.btce.BtcEApiClient;
import at.outdated.bitcoin.exchange.btce.BtcEMarket;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: ebirn
 * Date: 27.05.13
 * Time: 00:00
 * To change this template use File | Settings | File Templates.
 */
public class BtceTest extends BaseTest {


    @Override
    public void init() {
        market = Markets.getMarket("btce");
        client = new BtcEApiClient(market);
    }

    @Test
    public void  testPlaceOrder() {

        AssetPair asset = new AssetPair(Currency.LTC, Currency.BTC);

        CurrencyValue volume = new CurrencyValue(0.1, Currency.LTC);
        CurrencyValue price = new CurrencyValue(1.0, Currency.BTC);

        OrderId order = client.placeOrder(asset, TradeDecision.SELL, volume, price);

        log.info("placed order: {}", order);

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
