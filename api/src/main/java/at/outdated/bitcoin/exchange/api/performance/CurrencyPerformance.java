package at.outdated.bitcoin.exchange.api.performance;

import at.outdated.bitcoin.exchange.api.account.TransactionType;
import at.outdated.bitcoin.exchange.api.account.WalletTransaction;
import at.outdated.bitcoin.exchange.api.currency.Currency;
import at.outdated.bitcoin.exchange.api.currency.CurrencyValue;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: ebirn
 * Date: 11.05.13
 * Time: 15:23
 * To change this template use File | Settings | File Templates.
 */
public class CurrencyPerformance extends Performance {

    protected CurrencyValue startBalance = null;

    protected CurrencyValue endBalance = null;

    Currency currency;

    boolean started = false;

    private List<WalletTransaction> transactions = new ArrayList<>();

    //private Set<TransactionType> ignoredTransactions = new HashSet<>();

    public CurrencyPerformance(Currency currency) {
        this.currency = currency;
    }

    @Override
    public void includeTransaction(WalletTransaction transaction) throws IllegalArgumentException {

        if(transaction.getValue().getCurrency() != currency) {
            throw new IllegalArgumentException("Currency mismatch: only " + currency + " is allowed.");
        }

        transactions.add(transaction);

        TransactionType type = transaction.getType();
        //if(ignoredTransactions.contains(type)) return;



        CurrencyValue balance = transaction.getBalance();

        //System.out.println( transaction.getTimestamp() + ", " + type + ": balance: " + balance + ", transaction:" + transaction.getValue().getValue());

        if(!started) {
            startBalance = balance;
            started = true;
        }

        endBalance = balance;
    }

    @Override
    public BigDecimal getPercent() {
        return new CurrencyValue(getTotalDifference()).divide(startBalance).getValue();
    }

    @Override
    public CurrencyValue getTotalDifference() {
        return new CurrencyValue(endBalance).subtract(startBalance);
    }

    @Override
    public CurrencyValue getEndBalance() {
        return endBalance;
    }

    @Override
    public CurrencyValue getStartBalance() {
        return startBalance;
    }

    @Override
    public Currency getCurrency() {
        return currency;
    }
}
