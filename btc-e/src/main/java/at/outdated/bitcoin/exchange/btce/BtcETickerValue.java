package at.outdated.bitcoin.exchange.btce;

import at.outdated.bitcoin.exchange.api.market.TickerValue;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: ebirn
 * Date: 26.05.13
 * Time: 23:50
 * To change this template use File | Settings | File Templates.
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class BtcETickerValue  {

    // {"ticker":{
    //      "high":130.96001,
    //      "low":127.031,
    //      "avg":128.995505,
    //      "vol":363027.57475,
    //      "vol_cur":2817.3477,
    //      "last":130.4,
    //      "buy":130.4,
    //      "sell":130.07,
    //      "server_time":1369604800}}


    @XmlElement
    double last;

    @XmlElement
    double buy;

    @XmlElement
    double sell;

    @XmlElement
    double avg;

    @XmlElement
    double high;

    @XmlElement
    double low;

    @XmlElement
    double vol;

    @XmlElement
    double vol_cur;

    @XmlElement(name="server_time")
    protected long timestamp;

    public TickerValue getTickerValue() {

        TickerValue ticker = new TickerValue();

        ticker.setTimestamp(new Date(timestamp*1000));
        ticker.setLast(last);
        ticker.setVolume(vol_cur);
        ticker.setBid(sell);
        ticker.setAsk(buy);
        ticker.setHigh(high);
        ticker.setLow(low);



        return ticker;
    }
}
