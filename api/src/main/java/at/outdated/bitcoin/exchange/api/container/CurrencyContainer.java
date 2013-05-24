package at.outdated.bitcoin.exchange.api.container;

import at.outdated.bitcoin.exchange.api.currency.Currency;

import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: ebirn
 * Date: 11.05.13
 * Time: 16:50
 * To change this template use File | Settings | File Templates.
 */
public class CurrencyContainer<T> extends EnumContainer<Currency, T> {


    public Set<Currency> getCurrencies() {

        return getKeys();
    }


}
