package at.outdated.bitcoin.exchange.api.account;

import at.outdated.bitcoin.exchange.api.currency.Currency;
import at.outdated.bitcoin.exchange.api.currency.CurrencyValue;
import at.outdated.bitcoin.exchange.api.performance.CurrencyPerformance;
import at.outdated.bitcoin.exchange.api.performance.Performance;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: ebirn
 * Date: 26.05.13
 * Time: 14:54
 * To change this template use File | Settings | File Templates.
 */
public abstract class Wallet {

    protected CurrencyValue balance;

    protected CurrencyValue openOrders;

    protected Currency currency;

    protected List<WalletTransaction> transactions = new ArrayList<>();



    public Currency getCurrency() {
        return currency;
    }

    public void setBalance(CurrencyValue balance) {
        this.balance = balance;
    }

    public CurrencyValue getBalance() {
        return balance;
    }

    public CurrencyValue getOpenOrders() {
        return openOrders;
    }

    public void setTransactions(List<WalletTransaction> transactions) {
        this.transactions = transactions;
    }

    public List<WalletTransaction> getTransactions() {
        return transactions;
    }

    public Performance getPerformance(Date since) {

        Performance perf = new CurrencyPerformance(getCurrency());
        for(WalletTransaction trans : getTransactions()) {
            if(since.before(trans.getTimestamp())) {
                perf.includeTransaction(trans);
            }
        }

        return perf;
    }


    public String toString() {
        return "Wallet: " + getCurrency() + ": " + getBalance();
    }
}
