package at.outdated.bitcoin.exchange.bitkonan;

import at.outdated.bitcoin.exchange.api.market.TickerValue;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

/**
 * Created with IntelliJ IDEA.
 * User: ebirn
 * Date: 26.05.13
 * Time: 23:50
 * To change this template use File | Settings | File Templates.
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class BitkonanTickerValue {


    @XmlElement
    double last;

    @XmlElement
    double bid;

    @XmlElement
    double ask;

    @XmlElement
    double high;

    @XmlElement
    double low;

    @XmlElement
    double volume;

    @XmlElement
    double open;


    public TickerValue getTickerValue() {

        TickerValue ticker = new TickerValue();

        ticker.setLast(last);
        ticker.setVolume(volume);
        ticker.setBid(bid);
        ticker.setAsk(ask);
        ticker.setHigh(high);
        ticker.setLow(low);

        return ticker;
    }
}
