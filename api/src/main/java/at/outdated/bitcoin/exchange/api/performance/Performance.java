package at.outdated.bitcoin.exchange.api.performance;

import at.outdated.bitcoin.exchange.api.account.WalletTransaction;
import at.outdated.bitcoin.exchange.api.currency.Currency;
import at.outdated.bitcoin.exchange.api.currency.CurrencyValue;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Collection;

/**
 * Created with IntelliJ IDEA.
 * User: ebirn
 * Date: 17.05.13
 * Time: 01:02
 * To change this template use File | Settings | File Templates.
 */
public abstract class Performance {


    // throw Exception if transaction does not match performance currency
    abstract public boolean includeTransaction(WalletTransaction transaction);

    public boolean includeAllTransactions(Collection<WalletTransaction> transactions) {
        boolean didit = true;

        for(WalletTransaction t : transactions) {
            boolean single = includeTransaction(t);
            if(single == false) {
                didit = false;
            }
        }

        return didit;
    }

    abstract public BigDecimal getPercent();

    abstract public CurrencyValue getTotalDifference();

    abstract public CurrencyValue getEndBalance();

    abstract public CurrencyValue getStartBalance();

    abstract public Currency getCurrency();


    @Override
    public String toString() {

        String percentChange = NumberFormat.getPercentInstance().format(getPercent());

        return "Performance: " + getStartBalance() + " -> " + getEndBalance() + " = " + getTotalDifference() + " (" + percentChange + ")";
    }


}
