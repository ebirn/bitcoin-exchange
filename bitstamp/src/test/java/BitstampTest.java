import at.outdated.bitcoin.exchange.api.BaseTest;
import at.outdated.bitcoin.exchange.api.OrderId;
import at.outdated.bitcoin.exchange.api.account.AccountInfo;
import at.outdated.bitcoin.exchange.api.currency.Currency;
import at.outdated.bitcoin.exchange.api.currency.CurrencyValue;
import at.outdated.bitcoin.exchange.api.market.*;
import at.outdated.bitcoin.exchange.bitstamp.BitstampClient;
import at.outdated.bitcoin.exchange.bitstamp.BitstampMarket;
import at.outdated.bitcoin.exchange.bitstamp.BitstampOrder;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: ebirn
 * Date: 25.05.13
 * Time: 14:03
 * To change this template use File | Settings | File Templates.
 */
public class BitstampTest extends BaseTest {


    @Override
    public void init() {
        market = Markets.getMarket("bitstamp");
        client = new BitstampClient(new BitstampMarket());
    }

    @Test
    public void testOpenOrders() {

        List<MarketOrder> orders = client.getOpenOrders();

        for(MarketOrder order : orders) {
            log.info("{}", order);
        }
    }

    @Test
    public void testDeleteOrder() {

        OrderId id = new OrderId(market, "15281268");

        boolean didIt = client.cancelOrder(id);

        log.info("removed order {}: {}", id.getIdentifier(), didIt);
    }


    @Test
    public void testPlaceOrder() {

        AssetPair asset = market.getAsset(Currency.BTC, Currency.USD);
        CurrencyValue volume = new CurrencyValue(0.01, Currency.BTC);
        CurrencyValue price = new CurrencyValue(10000, Currency.USD);
        TradeDecision decision = TradeDecision.SELL;

        MarketOrder order = new MarketOrder();
        order.setAsset(asset);
        order.setVolume(volume);
        order.setPrice(price);
        order.setDecision(decision);

        OrderId id = client.placeOrder(asset, decision, volume, price);

        Assert.assertNotNull("order id is NULL", id);
        Assert.assertNotNull("order identifier is NULL", id.getIdentifier());
        Assert.assertFalse("order id identifier is empty", id.getIdentifier().isEmpty());

        log.info("order placed, id: {}", id);

        boolean success = client.cancelOrder(id);

        log.info("removed order: {}", success);

    }

}
