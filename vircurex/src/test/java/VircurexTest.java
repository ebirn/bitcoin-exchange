import at.outdated.bitcoin.exchange.api.BaseTest;
import at.outdated.bitcoin.exchange.api.OrderId;
import at.outdated.bitcoin.exchange.api.account.AccountInfo;
import at.outdated.bitcoin.exchange.api.market.MarketOrder;
import at.outdated.bitcoin.exchange.api.market.Markets;
import org.junit.Test;

import java.util.List;

/**
 * Created by ebirn on 11.10.13.
 */
public class VircurexTest extends BaseTest {

    @Override
    public void init() {
        market = Markets.getMarket("vircurex");
        client = market.getApiClient();
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

        OrderId id = new OrderId(market, "asdf1234");

        boolean didIt = client.cancelOrder(id);

        log.info("removed order {}: {}", id.getIdentifier(), didIt);

    }
}
