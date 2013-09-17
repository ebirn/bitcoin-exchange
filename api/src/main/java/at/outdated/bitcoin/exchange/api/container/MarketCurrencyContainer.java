package at.outdated.bitcoin.exchange.api.container;

import at.outdated.bitcoin.exchange.api.Market;
import at.outdated.bitcoin.exchange.api.currency.Currency;
import at.outdated.bitcoin.exchange.api.market.MarketContainer;

/**
 * Created with IntelliJ IDEA.
 * User: ebirn
 * Date: 27.05.13
 * Time: 12:44
 * To change this template use File | Settings | File Templates.
 */
public class MarketCurrencyContainer<T> extends MarketContainer<CurrencyContainer<T>> {


    public T get(Market market, Currency currency) {
        return this.get(market).get(currency);
    }


    public void set(Market market, Currency currency, T value) {
        this.get(market).set(currency, value);
    }
}
