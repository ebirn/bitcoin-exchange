package at.outdated.bitcoin.exchange.api.market;

import at.outdated.bitcoin.exchange.api.currency.Currency;
import at.outdated.bitcoin.exchange.api.currency.CurrencyValue;

import java.math.BigDecimal;
import java.util.Arrays;

/**
 * Created with IntelliJ IDEA.
 * User: ebirn
 * Date: 16.05.13
 * Time: 23:48
 * To change this template use File | Settings | File Templates.
 */
public class ExchangeRateCalculator {

    BigDecimal[][] rates;



    public ExchangeRateCalculator() {
        int currencyCount = Currency.values().length;

        // init exchange rate matrix
        rates = new BigDecimal[currencyCount][currencyCount];

        // set exchange rate for a currency with itself to 1
        BigDecimal selfRate = new BigDecimal(1);

        for(int i=0; i<currencyCount; i++) {
            // initialize rates with NaN
            Arrays.fill(rates[i], null);
            rates[i][i] = selfRate;
        }
    }

    public ExchangeRateCalculator(ExchangeRateCalculator other) {

        this.rates = Arrays.copyOf(other.rates, other.rates.length);

        for(int i=0; i<rates.length; i++) {
            rates[i] = Arrays.copyOf(other.rates[i], other.rates[i].length);
        }
    }

    public BigDecimal getRate(Currency from, Currency to) {
        return rates[from.ordinal()][to.ordinal()];
    }

    public void setRate(Currency from, Currency to, BigDecimal rate) {
        int fromIdx = from.ordinal();
        int toIdx = to.ordinal();
        rates[fromIdx][toIdx] = rate;
        rates[toIdx][fromIdx] = (new BigDecimal(1.0)).divide(rate, 5, BigDecimal.ROUND_HALF_UP);
    }

    public CurrencyValue calculate(CurrencyValue value, Currency in) {

        // FIXME: this must be all BigDecimal
        BigDecimal rate = getRate(value.getCurrency(), in);


        //FIXME: this is an incorrect hack: we must have a exchange path for all currencies
        if(rate == null || Double.isNaN(rate.doubleValue()) || Double.isInfinite(rate.doubleValue())) rate = new BigDecimal(0.0);

        return new CurrencyValue(value.getValue()*rate.doubleValue(), in);
    }
}
