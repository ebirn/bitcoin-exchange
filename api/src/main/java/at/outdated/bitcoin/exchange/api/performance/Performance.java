package at.outdated.bitcoin.exchange.api.performance;

import at.outdated.bitcoin.exchange.api.account.WalletTransaction;
import at.outdated.bitcoin.exchange.api.currency.Currency;
import at.outdated.bitcoin.exchange.api.currency.CurrencyValue;

import java.math.BigDecimal;
import java.text.NumberFormat;

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
