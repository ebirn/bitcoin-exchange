package at.outdated.bitcoin.exchange.bitkonan;

import at.outdated.bitcoin.exchange.api.currency.Currency;
import at.outdated.bitcoin.exchange.api.currency.CurrencyValue;
import at.outdated.bitcoin.exchange.api.market.AssetPair;
import at.outdated.bitcoin.exchange.api.market.TickerValue;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import java.math.BigDecimal;

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
    BigDecimal last;

    @XmlElement
    BigDecimal bid;

    @XmlElement
    BigDecimal ask;

    @XmlElement
    BigDecimal high;

    @XmlElement
    BigDecimal low;

    @XmlElement
    BigDecimal volume;

    @XmlElement
    double open;


    public TickerValue getTickerValue(AssetPair asset) {

        Currency quote = asset.getQuote();
        TickerValue ticker = new TickerValue(asset);

        ticker.setLast(new CurrencyValue(last, quote));
        ticker.setBid(new CurrencyValue(bid, quote));
        ticker.setAsk(new CurrencyValue(ask, quote));
        ticker.setHigh(new CurrencyValue(high, quote));
        ticker.setLow(new CurrencyValue(low, quote));

        ticker.setVolume(new CurrencyValue(volume, asset.getBase()));


        return ticker;
    }
}
