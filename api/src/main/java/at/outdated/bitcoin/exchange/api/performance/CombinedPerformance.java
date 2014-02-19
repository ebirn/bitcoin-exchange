package at.outdated.bitcoin.exchange.api.performance;

import at.outdated.bitcoin.exchange.api.account.Wallet;
import at.outdated.bitcoin.exchange.api.account.WalletTransaction;
import at.outdated.bitcoin.exchange.api.container.CurrencyContainer;
import at.outdated.bitcoin.exchange.api.currency.Currency;
import at.outdated.bitcoin.exchange.api.currency.CurrencyValue;
import at.outdated.bitcoin.exchange.api.market.ExchangeRateCalculator;

import java.math.BigDecimal;

/**
 * Created with IntelliJ IDEA.
 * User: ebirn
 * Date: 16.05.13
 * Time: 23:46
 * To change this template use File | Settings | File Templates.
 */
public class CombinedPerformance extends Performance {

    ExchangeRateCalculator calculator;
    CurrencyContainer<Performance> performances;
    private Currency currency;

    public CombinedPerformance(Currency curr, ExchangeRateCalculator calculator) {
        this.currency = curr;

        //ExchangeRateClient exClient = new ExchangeRateClient();
        //exClient.update();

        //this.calculator = exClient.getCurentCalculator();

        this.calculator = calculator;
        performances = new CurrencyContainer<>();

        for(Currency c : Currency.values()) {
            performances.set(c, new CurrencyPerformance(c));
        }
    }

    @Override
    public boolean includeTransaction(WalletTransaction transaction) {
        Currency c = transaction.getValue().getCurrency();
        performances.get(c).includeTransaction(transaction);

        return true;
    }


    public void includeWallet(Wallet wallet) {
        for(WalletTransaction t : wallet.getTransactions()) {
            includeTransaction(t);
        }
    }


    @Override
    public BigDecimal getPercent() {

        //double onePercent = getStartBalance() / 100.0;
        //return 1.0 + (getTotalDifference() / onePercent);

        BigDecimal percent = BigDecimal.ZERO;

        if(getStartBalance().getValue().signum() != 0) {
            percent = getTotalDifference().divide(getStartBalance()).getValue();
        }

        return percent;
    }

    @Override
    public CurrencyValue getTotalDifference() {

        return getEndBalance().subtract(getStartBalance());
    }

    @Override
    public CurrencyValue getEndBalance() {

        CurrencyValue end = new CurrencyValue(currency);
        for(Performance p : performances) {
            end.add(calculator.calculate(p.getEndBalance(), currency));
        }
        return end;
    }

    @Override
    public CurrencyValue getStartBalance() {

        CurrencyValue start = new CurrencyValue(currency);
        for(Performance p : performances) {
            start.add(calculator.calculate(p.getStartBalance(), currency));
        }

        return start;
    }

    public void setPerformance(Performance p) {
        performances.set(p.getCurrency(), p);
    }

    @Override
    public Currency getCurrency() {
        return currency;
    }

    public Performance getPerformance(Currency curr) {
        return performances.get(curr);
    }


}
