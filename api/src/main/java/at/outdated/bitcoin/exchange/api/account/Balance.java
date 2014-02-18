package at.outdated.bitcoin.exchange.api.account;

import at.outdated.bitcoin.exchange.api.currency.Currency;
import at.outdated.bitcoin.exchange.api.currency.CurrencyValue;
import at.outdated.bitcoin.exchange.api.market.Market;

import java.util.*;

/**
 * Created by ebirn on 25.01.14.
 */
public class Balance {

    Map<Currency,CurrencyValue> available = new HashMap<>();
    Map<Currency,CurrencyValue> open = new HashMap<>();

    public Balance() {
        initCurrencies(Arrays.asList(Currency.values()));
    }

    public Balance(Market market) {
        initCurrencies(market.getCurrencies());
    }

    private void initCurrencies(Collection<Currency> currencies) {
        for(Currency c : currencies) {
            this.available.put(c, new CurrencyValue(c));
            this.open.put(c, new CurrencyValue(c));
        }
    }

    public CurrencyValue getOpen(Currency c) {
        return this.open.get(c);
    }

    public CurrencyValue getAvailable(Currency c) {
        return available.get(c);
    }

    public void setOpen(CurrencyValue cv) {
        this.open.put(cv.getCurrency(), cv);
    }

    public void setAvailable(CurrencyValue cv) {
        this.available.put(cv.getCurrency(), cv);
    }

    public boolean isAvailable(Currency c) {
        return (this.available.containsKey(c) && this.available.get(c).isPositive());
    }

    public CurrencyValue getTotal(Currency c) {

        CurrencyValue openValue = this.open.get(c);
        CurrencyValue availValue = this.available.get(c);


        CurrencyValue total = new CurrencyValue(c);

        if(openValue != null) total.add(openValue);
        if(availValue != null) total.add(availValue);

        return total;
    }

    public String longString() {
        StringBuilder builder = new StringBuilder();

        boolean hasFunds = false;
        for(Currency c : this.available.keySet()) {
            CurrencyValue currencyBalance = getTotal(c);
            if(currencyBalance != null && currencyBalance.isPositive()) {
                builder.append(c.name());
                builder.append("=");
                builder.append(currencyBalance.getValue().toString());
                builder.append(" ");
                hasFunds = true;
            }
        }

        if(!hasFunds) {
            return "none.";
        }

        return builder.toString();
    }

    public boolean isZero() {

        for(Currency c : this.available.keySet()) {
            if(this.available.get(c).isPositive()) return false;
            if(this.open.get(c).isPositive()) return false;
        }

        return true;
    }

    public boolean hasFunds() {
        return !isZero();
    }

    public Set<Currency> currencies() {
        return new HashSet<>(this.available.keySet());
    }

    @Override
    public String toString() {
        return "Balance: " + longString();
    }
}
