package at.outdated.bitcoin.exchange.api.performance;

import at.outdated.bitcoin.exchange.api.market.ExchangeRateCalculator;
import at.outdated.bitcoin.exchange.api.account.Wallet;
import at.outdated.bitcoin.exchange.api.account.WalletTransaction;
import at.outdated.bitcoin.exchange.api.container.CurrencyContainer;
import at.outdated.bitcoin.exchange.api.currency.Currency;
import at.outdated.bitcoin.exchange.api.currency.CurrencyValue;

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
    public void includeTransaction(WalletTransaction transaction) throws  IllegalArgumentException {
        Currency c = transaction.getValue().getCurrency();
        performances.get(c).includeTransaction(transaction);
    }


    public void includeWallet(Wallet wallet) {
        for(WalletTransaction t : wallet.getTransactions()) {
            includeTransaction(t);
        }
    }


    @Override
    public double getPercent() {

        //double onePercent = getStartBalance() / 100.0;
        //return 1.0 + (getTotalDifference() / onePercent);

        return (getTotalDifference() / getStartBalance());
    }

    @Override
    public double getTotalDifference() {

        return getEndBalance()-getStartBalance();
    }

    @Override
    public double getEndBalance() {

        double end = 0;
        for(Performance p : performances) {
            end += calculator.calculate(new CurrencyValue(p.getEndBalance(), p.getCurrency()), currency).getValue();
        }
        return end;
    }

    @Override
    public double getStartBalance() {

        double start = 0;
        for(Performance p : performances) {
            start += calculator.calculate(new CurrencyValue(p.getStartBalance(), p.getCurrency()), currency).getValue();
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
