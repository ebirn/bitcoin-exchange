package at.outdated.bitcoin.exchange.kraken;

import at.outdated.bitcoin.exchange.api.market.TickerValue;

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
public class TickerResponse {


    // {"error":[],"result":{"XXBTZEUR":{"a":["95.00000","2"],"b":["90.00000","1"],"c":["95.00000","0.00000000"],"v":["0.10526000","4.46315000"],"p":["95.00000","94.43984"],"t":[1,6],"l":["90.00000","90.00000"],"h":["95.00000","95.00000"],"o":"90.00000"}}}%

    @XmlElement
    private Object[] error;

    @XmlElement
    private TickerResponseResult result;


    public Object[] getError() {
        return error;
    }

    public TickerValue getValue() {
        TickerValue value = new TickerValue();


        KrakenTickerValue kv = result.getXXBTZEUR();

        value.setLast(Double.parseDouble(kv.l[0]));

        value.setAsk(Double.parseDouble(kv.a[0]));
        value.setBid(Double.parseDouble(kv.b[0]));

        value.setVolume(kv.v[0]);

        value.setHigh(Double.parseDouble(kv.h[0]));
        value.setLow(Double.parseDouble(kv.l[0]));

        return value;
    }
}
