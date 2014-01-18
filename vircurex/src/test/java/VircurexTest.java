import at.outdated.bitcoin.exchange.api.BaseTest;
import at.outdated.bitcoin.exchange.api.OrderId;
import at.outdated.bitcoin.exchange.api.account.AccountInfo;
import at.outdated.bitcoin.exchange.api.currency.Currency;
import at.outdated.bitcoin.exchange.api.currency.CurrencyValue;
import at.outdated.bitcoin.exchange.api.market.AssetPair;
import at.outdated.bitcoin.exchange.api.market.MarketOrder;
import at.outdated.bitcoin.exchange.api.market.Markets;
import at.outdated.bitcoin.exchange.api.market.TradeDecision;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

/**
 * Created by ebirn on 11.10.13.
 */
public class VircurexTest extends BaseTest {

    @Override
    public void init() {
        market = Markets.getMarket("vircurex");
        client = market.createClient();
    }

    @Test
    public void testAccountInfo() {

        AccountInfo info  = client.getAccountInfo();
        log.info("account: {}", info);


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

        OrderId id = new OrderId(market, "xxx7135167xxx");

        boolean didIt = client.cancelOrder(id);

        log.info("removed order {}: {}", id.getIdentifier(), didIt);
    }


    @Test
    public void testPlaceOrder() {

        AssetPair asset = market.getAsset(Currency.BTC, Currency.LTC);
        CurrencyValue volume = new CurrencyValue(10, Currency.BTC);
        CurrencyValue price = new CurrencyValue(0.001, Currency.LTC);
        TradeDecision decision = TradeDecision.BUY;

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
