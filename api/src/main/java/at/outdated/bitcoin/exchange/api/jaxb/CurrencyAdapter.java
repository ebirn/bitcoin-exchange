package at.outdated.bitcoin.exchange.api.jaxb;

import at.outdated.bitcoin.exchange.api.currency.Currency;

import javax.xml.bind.annotation.adapters.XmlAdapter;

/**
 * Created by ebirn on 17.02.14.
 */
public class CurrencyAdapter extends XmlAdapter<String,Currency> {

    @Override
    public Currency unmarshal(String v) throws Exception {
        return Currency.valueOf(v);
    }

    @Override
    public String marshal(Currency v) throws Exception {
        return v.name();
    }
}
