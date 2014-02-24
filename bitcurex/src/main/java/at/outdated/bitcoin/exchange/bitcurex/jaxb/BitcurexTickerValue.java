package at.outdated.bitcoin.exchange.bitcurex.jaxb;

import at.outdated.bitcoin.exchange.api.currency.Currency;
import at.outdated.bitcoin.exchange.api.currency.CurrencyValue;
import at.outdated.bitcoin.exchange.api.market.AssetPair;
import at.outdated.bitcoin.exchange.api.market.TickerValue;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.math.BigDecimal;
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
    BigDecimal high;

    @XmlElement
    BigDecimal low;

    @XmlElement
    double avg;

    @XmlElement
    double vwap;

    @XmlElement
    BigDecimal vol;

    @XmlElement
    BigDecimal last;

    @XmlElement
    BigDecimal sell;

    @XmlElement
    BigDecimal buy;

    @XmlElement(name="time")
    //@XmlJavaTypeAdapter(UnixTimeDateAdapter.class)
    //Date time;
    long unixTime;


    public TickerValue getTickerValue(AssetPair asset) {

        Currency quote = asset.getQuote();

        TickerValue ticker = new TickerValue(asset);

        ticker.setTimestamp(new Date(unixTime * 1000L));
        ticker.setLast(new CurrencyValue(last, quote));

        ticker.setHigh(new CurrencyValue(high, quote));
        ticker.setLow(new CurrencyValue(low, quote));

        ticker.setAsk(new CurrencyValue(sell, quote));
        ticker.setBid(new CurrencyValue(buy, quote));

        ticker.setVolume(new CurrencyValue(vol, asset.getBase()));

        return ticker;
    }
}
