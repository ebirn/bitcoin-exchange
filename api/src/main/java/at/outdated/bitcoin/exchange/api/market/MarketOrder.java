package at.outdated.bitcoin.exchange.api.market;

import at.outdated.bitcoin.exchange.api.currency.Currency;
import at.outdated.bitcoin.exchange.api.currency.CurrencyValue;

/**
 * Created by ebirn on 22.09.13.
 */
public class MarketOrder {

    protected Currency baseCurrency;

    protected CurrencyValue price;

    protected float volume;


    public MarketOrder() {

    }

    public MarketOrder(float volume, Currency base,  CurrencyValue price) {
        this.volume = volume;
        this.baseCurrency = base;
        this.price = price;
    }

    public Currency getBaseCurrency() {
        return baseCurrency;
    }

    public void setBaseCurrency(Currency baseCurrency) {
        this.baseCurrency = baseCurrency;
    }

    public CurrencyValue getPrice() {
        return price;
    }

    public void setPrice(CurrencyValue price) {
        this.price = price;
    }

    public float getVolume() {
        return volume;
    }

    public void setVolume(float volume) {
        this.volume = volume;
    }
}
