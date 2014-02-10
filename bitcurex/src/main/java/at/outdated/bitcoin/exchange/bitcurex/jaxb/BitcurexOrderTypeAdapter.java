package at.outdated.bitcoin.exchange.bitcurex.jaxb;

import at.outdated.bitcoin.exchange.api.market.OrderType;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.adapters.XmlAdapter;

/**
 * Created by ebirn on 20.10.13.
 */

public class BitcurexOrderTypeAdapter extends XmlAdapter<Integer,OrderType> {
/*    UNKNOWN,
    ASK,
    BID

    */

    @Override
    public OrderType unmarshal(Integer v) throws Exception {

        switch(v) {
            case 1:
                return OrderType.ASK;

            case 2:
                return OrderType.BID;
        }

        return OrderType.UNDEF;
    }

    @Override
    public Integer marshal(OrderType v) throws Exception {

        switch(v) {
            case ASK:
                return 1;

            case BID:
                return 2;
        }

        return -1;
    }
}
