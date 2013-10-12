package at.outdated.bitcoin.exchange.api.account;

import at.outdated.bitcoin.exchange.api.currency.Currency;
import at.outdated.bitcoin.exchange.api.currency.CurrencyValue;
import at.outdated.bitcoin.exchange.api.performance.CurrencyPerformance;
import at.outdated.bitcoin.exchange.api.performance.Performance;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
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

public class Wallet {


    protected CurrencyValue balance;
    protected CurrencyValue openOrders;

    protected Currency currency;

    protected List<WalletTransaction> transactions = new ArrayList<>();


    public Wallet() {
    }

    public Wallet(Currency c) {
        this.currency = c;
        this.balance = new CurrencyValue(0.0, c);
    }

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

    public void addTransaction(WalletTransaction trans) {

        switch(trans.getType()) {
            case DEPOSIT:
            case IN:
                balance.add(trans.getValue());
                break;

            case FEE:
            case OUT:
            case WITHDRAW:
            case SPENT:
                balance.subtract(trans.getValue());
                break;

            default:
                throw new IllegalArgumentException("transaction type not implemented in wallet");
        }

        if(trans.getBalance() != null) {
            this.balance = new CurrencyValue(trans.getBalance());
        }
        else {
            // copy value, as this.balance reference changes when adding transactions
            trans.setBalance(new CurrencyValue(this.balance));
        }

        transactions.add(trans);
    }

    public void setTransactions(List<WalletTransaction> transactions) {
        this.transactions = transactions;
    }

    public List<WalletTransaction> getTransactions() {
        return transactions;
    }

    public Performance getPerformance() {
        return getPerformance(new Date(0L));
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
