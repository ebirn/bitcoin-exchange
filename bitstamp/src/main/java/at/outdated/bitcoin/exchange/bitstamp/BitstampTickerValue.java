package at.outdated.bitcoin.exchange.bitstamp;

import at.outdated.bitcoin.exchange.api.currency.Currency;
import at.outdated.bitcoin.exchange.api.currency.CurrencyValue;
import at.outdated.bitcoin.exchange.api.jaxb.StringBigDecimalAdapter;
import at.outdated.bitcoin.exchange.api.market.AssetPair;
import at.outdated.bitcoin.exchange.api.market.TickerValue;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.math.BigDecimal;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: ebirn
 * Date: 25.05.13
 * Time: 13:06
 * To change this template use File | Settings | File Templates.
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class BitstampTickerValue {

    // {"high": "129.94", "last": "128.48", "bid": "128.30", "volume": "12260.32110601", "low": "124.00", "ask": "128.48"}

    @XmlElement @XmlJavaTypeAdapter(StringBigDecimalAdapter.class)
    BigDecimal last;

    @XmlElement @XmlJavaTypeAdapter(StringBigDecimalAdapter.class)
    BigDecimal high;

    @XmlElement @XmlJavaTypeAdapter(StringBigDecimalAdapter.class)
    BigDecimal low;

    // what you need to pay for buying
    @XmlElement @XmlJavaTypeAdapter(StringBigDecimalAdapter.class)
    BigDecimal bid;

    // what you get for selling
    @XmlElement @XmlJavaTypeAdapter(StringBigDecimalAdapter.class)
    BigDecimal ask;

    @XmlElement @XmlJavaTypeAdapter(StringBigDecimalAdapter.class)
    BigDecimal volume;

    Date timeStamp = new Date();

    public TickerValue getTickerValue(AssetPair asset) {

        Currency quote = asset.getQuote();
        TickerValue ticker = new TickerValue(asset);
        ticker.setTimestamp(timeStamp);

        ticker.setLast(new CurrencyValue(last, quote));
        ticker.setHigh(new CurrencyValue(high, quote));
        ticker.setLow(new CurrencyValue(low, quote));

        ticker.setAsk(new CurrencyValue(ask, quote));
        ticker.setBid(new CurrencyValue(bid, quote));

        ticker.setVolume(new CurrencyValue(volume, asset.getBase()));

        return ticker;
    }
}
