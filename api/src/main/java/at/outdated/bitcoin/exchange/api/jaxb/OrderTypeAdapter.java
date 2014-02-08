package at.outdated.bitcoin.exchange.api.jaxb;

import at.outdated.bitcoin.exchange.api.market.OrderType;

import javax.xml.bind.annotation.adapters.XmlAdapter;

/**
 * Created by ebirn on 08.02.14.
 */
public class OrderTypeAdapter extends XmlAdapter<String,OrderType> {

    @Override
    public OrderType unmarshal(String v) throws Exception {

        String raw = v.toLowerCase();

        switch(raw) {
            case "bid":
            case "buy":
                return OrderType.BID;

            case "ask":
            case "sell":
                return OrderType.ASK;

        }

        return null;
    }

    @Override
    public String marshal(OrderType v) throws Exception {

        throw new UnsupportedOperationException("cannot convert");
    }
}
