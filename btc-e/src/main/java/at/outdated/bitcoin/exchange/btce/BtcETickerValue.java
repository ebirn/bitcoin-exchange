package at.outdated.bitcoin.exchange.btce;

import at.outdated.bitcoin.exchange.api.currency.Currency;
import at.outdated.bitcoin.exchange.api.currency.CurrencyValue;
import at.outdated.bitcoin.exchange.api.market.AssetPair;
import at.outdated.bitcoin.exchange.api.market.TickerValue;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import java.math.BigDecimal;
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
    BigDecimal last;

    @XmlElement
    BigDecimal buy;

    @XmlElement
    BigDecimal sell;

    @XmlElement
    double avg;

    @XmlElement
    BigDecimal high;

    @XmlElement
    BigDecimal low;

    @XmlElement
    double vol;

    @XmlElement
    BigDecimal vol_cur;

    @XmlElement(name="server_time")
    protected long timestamp;

    @XmlElement
    protected long updated;

    public TickerValue getTickerValue(AssetPair asset) {

        Currency quote = asset.getQuote();

        TickerValue ticker = new TickerValue(asset);

        ticker.setTimestamp(new Date(updated*1000));
        ticker.setLast(new CurrencyValue(last, quote));

        ticker.setBid(new CurrencyValue(sell, quote));
        ticker.setAsk(new CurrencyValue(buy, quote));
        ticker.setHigh(new CurrencyValue(high, quote));
        ticker.setLow(new CurrencyValue(low, quote));

        ticker.setVolume(new CurrencyValue(vol_cur, asset.getBase()));

        return ticker;
    }
}
