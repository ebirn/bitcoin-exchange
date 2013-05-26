package at.outdated.bitcoin.exchange.api.performance;

import at.outdated.bitcoin.exchange.api.account.WalletTransaction;
import at.outdated.bitcoin.exchange.api.currency.Currency;

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
    abstract public void includeTransaction(WalletTransaction transaction) throws IllegalArgumentException;

    abstract public double getPercent();

    abstract public double getTotalDifference();

    abstract public double getEndBalance();

    abstract public double getStartBalance();

    abstract public Currency getCurrency();


    @Override
    public String toString() {

        String percentChange = NumberFormat.getPercentInstance().format(getPercent());

        /*
        NumberFormat currencyFormat = NumberFormat.getInstance();
        currencyFormat.setMinimumFractionDigits(2);
        currencyFormat.setMaximumFractionDigits(2);

        currencyFormat.setMinimumIntegerDigits(2);

        String startBalance = currencyFormat.format(getStartBalance()) + " " + getCurrency();
        String endBalance = currencyFormat.format(getEndBalance()) + " " + getCurrency();
        String diff = currencyFormat.format(getTotalDifference()) + " " + getCurrency();
        */

        return "Performance: " + getStartBalance() + " -> " + getEndBalance() + " = " + getTotalDifference() + " (" + percentChange + ")";
    }
}
