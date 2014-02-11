package at.outdated.bitcoin.exchange;

import at.outdated.bitcoin.exchange.api.account.AccountInfo;
import at.outdated.bitcoin.exchange.api.account.Balance;
import at.outdated.bitcoin.exchange.api.account.WalletTransaction;
import at.outdated.bitcoin.exchange.api.currency.Currency;
import at.outdated.bitcoin.exchange.api.market.Market;
import at.outdated.bitcoin.exchange.api.market.Markets;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by ebirn on 25.01.14.
 */
@RunWith(value=Parameterized.class)
public class AccountInfoTest extends BaseTest {


    public AccountInfoTest(String key, Market m) {
        super(key, m);
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

        log.info("balance on {}", market);
        for(Currency c : market.getCurrencies()) {
            log.info("  available: {}   | open {}", balance.getAvailable(c), balance.getOpen(c));
        }

    }

    @Test
    public void listTransactions() {

        List<WalletTransaction> transactions = client.getTransactions();

        for(WalletTransaction wt : transactions) {
            log.info("transaction: {}", wt);
        }

    }

}
