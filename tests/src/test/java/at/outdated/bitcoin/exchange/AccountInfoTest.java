package at.outdated.bitcoin.exchange;

import at.outdated.bitcoin.exchange.api.account.Balance;
import at.outdated.bitcoin.exchange.api.account.WalletTransaction;
import at.outdated.bitcoin.exchange.api.client.ExchangeClient;
import at.outdated.bitcoin.exchange.api.client.TradeClient;
import at.outdated.bitcoin.exchange.api.currency.Currency;
import at.outdated.bitcoin.exchange.api.market.Market;
import at.outdated.bitcoin.exchange.api.market.MarketOrder;
import at.outdated.bitcoin.exchange.api.market.OrderType;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Collection;
import java.util.List;

/**
 * Created by ebirn on 25.01.14.
 */
@RunWith(value=Parameterized.class)
public class AccountInfoTest extends BaseTest {


    public AccountInfoTest(String key, Market m, ExchangeClient client) {
        super(key, m, client);
    }

    @Parameterized.Parameters(name = "{0}AccountInfoTest")
    public static Collection<Object[]> getMarketParams() {
        return BaseTest.getMarketParams();
        /*
            List<Object[]> params = new ArrayList<>();
            params.add(marketParams(Markets.getMarket("coinse")));
            return params;
        */
    }


    @Test
    public void balanceTest() {

        Balance balance = client.getBalance();

        Assert.assertNotNull("balance is null", balance);

        log.info("balance on {}: {}", market, balance);
        for(Currency c : market.getCurrencies()) {
            log.info("  available: {}   | open {}", balance.getAvailable(c), balance.getOpen(c));
        }

    }

    @Test
    public void listTransactions() {

        List<WalletTransaction> transactions = client.getTransactions();
        Assert.assertNotNull("transaction history", transactions);

        for(WalletTransaction wt : transactions) {
            log.info("transaction: {}", wt);
        }
    }

    @Test
    public void testOpenOrders() {
        List<MarketOrder> orders = client.getOpenOrders();
        Assert.assertNotNull("open orders", orders);
    }
}
