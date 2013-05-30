package at.outdated.bitcoin.exchange.bitcurex;

import at.outdated.bitcoin.exchange.api.market.TickerValue;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: ebirn
 * Date: 30.05.13
 * Time: 12:50
 * To change this template use File | Settings | File Templates.
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class BitcurexTickerValue  {


    //https://eur.bitcurex.com/data/ticker.json
    // {"high":99.98,"low":99.85,"avg":99.91499999,"vwap":99.96944474,"vol":2.68977951,"last":99.98,"buy":97.15,"sell":100.98,"time":1369911319}

    @XmlElement
    double high;

    @XmlElement
    double low;

    @XmlElement
    double avg;

    @XmlElement
    double vwap;

    @XmlElement
    double vol;

    @XmlElement
    double last;

    @XmlElement
    double sell;

    @XmlElement
    double buy;

    @XmlElement(name="time")
    //@XmlJavaTypeAdapter(UnixTimeDateAdapter.class)
    //Date time;
    long unixTime;


    public TickerValue getTickerValue() {
        TickerValue ticker = new TickerValue();

        ticker.setTimestamp(new Date(unixTime * 1000L));
        ticker.setLast(last);

        ticker.setHigh(high);
        ticker.setLow(low);

        ticker.setAsk(sell);
        ticker.setBid(buy);

        ticker.setVolume(vol);

        return ticker;
    }
}
