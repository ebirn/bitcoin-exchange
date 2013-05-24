package at.outdated.bitcoin.exchange.api;

import at.outdated.bitcoin.exchange.api.currency.Currency;
import at.outdated.bitcoin.exchange.api.currency.CurrencyValue;

import java.util.Arrays;

/**
 * Created with IntelliJ IDEA.
 * User: ebirn
 * Date: 16.05.13
 * Time: 23:48
 * To change this template use File | Settings | File Templates.
 */
public class ExchangeRateCalculator {

    double[][] rates;

    public ExchangeRateCalculator() {
        int currencyCount = Currency.values().length;

        // init exchange rate matrix
        rates = new double[currencyCount][currencyCount];

        // set exchange rate for a currency with itself to 1
        for(int i=0; i<currencyCount; i++) {
            // initialize rates with 0
            Arrays.fill(rates[i], 0.0);
            rates[i][i] = 1.0;
        }
    }

    public double getRate(Currency from, Currency to) {
        return rates[from.ordinal()][to.ordinal()];
    }

    public void setRate(Currency from, Currency to, double rate) {
        int fromIdx = from.ordinal();
        int toIdx = to.ordinal();
        rates[fromIdx][toIdx] = rate;
        rates[toIdx][fromIdx] = 1.0 / rate;
    }

    public CurrencyValue calculate(CurrencyValue value, Currency in) {

        double rate = getRate(value.getCurrency(), in);

        return new CurrencyValue(value.getValue()*rate, in);
    }
}
