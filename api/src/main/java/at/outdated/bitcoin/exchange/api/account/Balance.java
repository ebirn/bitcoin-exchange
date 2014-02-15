package at.outdated.bitcoin.exchange.api.account;

import at.outdated.bitcoin.exchange.api.currency.Currency;
import at.outdated.bitcoin.exchange.api.currency.CurrencyValue;
import at.outdated.bitcoin.exchange.api.market.Market;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by ebirn on 25.01.14.
 */
public class Balance {

    Map<Currency,CurrencyValue> available = new HashMap<>();
    Map<Currency,CurrencyValue> open = new HashMap<>();

    public Balance() {
    }

    public Balance(Market market) {
        for(Currency c : market.getCurrencies()) {
            available.put(c, new CurrencyValue(c));
            open.put(c, new CurrencyValue(c));
        }
    }

    public CurrencyValue getOpen(Currency c) {
        return open.get(c);
    }

    public CurrencyValue getAvailable(Currency c) {
        return available.get(c);
    }

    public void setOpen(CurrencyValue cv) {
        open.put(cv.getCurrency(), cv);
    }

    public void setAvailable(CurrencyValue cv) {
        available.put(cv.getCurrency(), cv);
    }


    public CurrencyValue getTotal(Currency c) {

        CurrencyValue openValue = open.get(c);
        CurrencyValue availValue = available.get(c);


        CurrencyValue total = new CurrencyValue(c);

        if(openValue != null) total.add(openValue);
        if(availValue != null) total.add(availValue);

        return total;
    }

    public String longString() {
        StringBuilder builder = new StringBuilder();

        for(Currency c : Currency.values()) {
            CurrencyValue currencyBalance = getTotal(c);
            if(currencyBalance != null && currencyBalance.isPositive()) {
                builder.append(c.name());
                builder.append("=");
                builder.append(currencyBalance.getValue().toString());
                builder.append(" ");
            }
        }

        return builder.toString();
    }

    @Override
    public String toString() {
        return "Balance: " + longString();
    }
}
