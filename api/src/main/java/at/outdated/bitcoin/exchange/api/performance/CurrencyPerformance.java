package at.outdated.bitcoin.exchange.api.performance;

import at.outdated.bitcoin.exchange.api.account.TransactionType;
import at.outdated.bitcoin.exchange.api.account.WalletTransaction;
import at.outdated.bitcoin.exchange.api.currency.Currency;

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

    protected double startBalance = 0.0;

    protected double endBalance = 0.0;

    at.outdated.bitcoin.exchange.api.currency.Currency currency;

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

        TransactionType type = transaction.getType();
        //if(ignoredTransactions.contains(type)) return;

        double balance = transaction.getBalance().getValue();

        //System.out.println( transaction.getTimestamp() + ", " + type + ": balance: " + balance + ", transaction:" + transaction.getValue().getValue());

        if(!started) {
            startBalance = balance;
            started = true;
        }

        endBalance = balance;
    }

    @Override
    public double getPercent() {
        double onePercent = startBalance / 100.0;

        return 1.0 + (getTotalDifference() / onePercent);
    }

    @Override
    public double getTotalDifference() {
        return endBalance-startBalance;
    }

    @Override
    public double getEndBalance() {
        return endBalance;
    }

    @Override
    public double getStartBalance() {
        return startBalance;
    }

    @Override
    public Currency getCurrency() {
        return currency;
    }
}
