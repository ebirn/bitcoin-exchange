package at.outdated.bitcoin.exchange.bter;

import at.outdated.bitcoin.exchange.api.market.TickerValue;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by ebirn on 11.10.13.
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class BterTicker {


    // {"result":"true","last":798,"high":806,"low":795.01,"avg":799.69,"sell":805.7,"buy":798,"vol_btc":738.7863,"vol_cny":590796.48}%

    @XmlElement(name="buy")
    double ask;

    @XmlElement(name="sell")
    double bid;

    @XmlElement
    double high;

    @XmlElement
    double low;

    @XmlElement
    double last;

    /*
    @XmlElementRefs({
            @XmlElementRef(name = "vol_btc"),
            @XmlElementRef(name = "vol_ltc"),
            @XmlElementRef(name = "vol_ftc")
    })*/
    @XmlElement(name="vol_btc")
    double volume;

    public TickerValue getValue() {

        TickerValue val = new TickerValue();

        val.setAsk(ask);
        val.setBid(bid);
        val.setVolume(volume);
        val.setLast(last);
        return val;
    }

}
