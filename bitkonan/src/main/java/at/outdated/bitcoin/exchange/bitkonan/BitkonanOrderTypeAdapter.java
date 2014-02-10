package at.outdated.bitcoin.exchange.bitkonan;

import at.outdated.bitcoin.exchange.api.market.OrderType;

import javax.xml.bind.annotation.adapters.XmlAdapter;

/**
 * Created by ebirn on 09.02.14.
 */
public class BitkonanOrderTypeAdapter extends XmlAdapter<Integer,OrderType> {
    @Override
    public OrderType unmarshal(Integer v) throws Exception {

        switch(v) {
            case 1:
                return OrderType.BID;

            case 2:
                return OrderType.ASK;
        }

        return OrderType.UNDEF;
    }

    @Override
    public Integer marshal(OrderType v) throws Exception {

        switch(v) {
            case BID:
                return 1;

            case ASK:
               return 2;
        }

        return -1;
    }
}
