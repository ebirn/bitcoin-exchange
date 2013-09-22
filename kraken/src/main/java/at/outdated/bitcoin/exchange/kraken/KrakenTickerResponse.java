package at.outdated.bitcoin.exchange.kraken;

import at.outdated.bitcoin.exchange.api.currency.Currency;
import at.outdated.bitcoin.exchange.api.market.TickerValue;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created with IntelliJ IDEA.
 * User: ebirn
 * Date: 17.09.13
 * Time: 18:31
 * To change this template use File | Settings | File Templates.
 */

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class KrakenTickerResponse extends KrakenResponse<KrakenTickerValue> {


    // {"error":[],"result":{"XXBTZEUR":{"a":["95.00000","2"],"b":["90.00000","1"],"c":["95.00000","0.00000000"],"v":["0.10526000","4.46315000"],"p":["95.00000","94.43984"],"t":[1,6],"l":["90.00000","90.00000"],"h":["95.00000","95.00000"],"o":"90.00000"}}}%
    // {"error":[],"result":{"XXBTZEUR":{"a":["94.00000","1"],"b":["90.00000","1"],"c":["93.20000","2.00000000"],"v":["2.50000000","2.50000000"],"p":["93.28000","93.28000"],"t":[2,2],"l":["93.20000","93.20000"],"h":["94.50000","93.60000"],"o":"94.50000"}}}


    public TickerValue getValue() {
        TickerValue value = new TickerValue();


        KrakenTickerValue kv = result.getXXBTZEUR();

        value.setCurrency(Currency.EUR);
        value.setLast(Double.parseDouble(kv.l[0]));

        value.setAsk(Double.parseDouble(kv.a[0]));
        value.setBid(Double.parseDouble(kv.b[0]));

        value.setVolume(Double.parseDouble(kv.v[0]));

        value.setHigh(Double.parseDouble(kv.h[0]));
        value.setLow(Double.parseDouble(kv.l[0]));

        return value;
    }
}
