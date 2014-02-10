package at.outdated.bitcoin.exchange.bitstamp;

import at.outdated.bitcoin.exchange.api.market.OrderType;

import javax.xml.bind.annotation.adapters.XmlAdapter;

/**
 * Created by ebirn on 09.02.14.
 */
public class BitstampOrderTypeAdapter extends XmlAdapter<Integer,OrderType> {

    @Override
    public OrderType unmarshal(Integer v) throws Exception {

        switch(v) {
            case 0:
                return OrderType.BID;

            case 1:
                return OrderType.ASK;
        }

        return OrderType.UNDEF;
    }

    @Override
    public Integer marshal(OrderType v) throws Exception {

        switch(v) {
            case BID:
                return 0;

            case ASK:
                return 1;
        }
        return -1;
    }
}
