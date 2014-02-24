package at.outdated.bitcoin.exchange.bter;

import at.outdated.bitcoin.exchange.api.currency.Currency;
import at.outdated.bitcoin.exchange.api.currency.CurrencyValue;
import at.outdated.bitcoin.exchange.api.market.AssetPair;
import at.outdated.bitcoin.exchange.api.market.TickerValue;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.math.BigDecimal;

/**
 * Created by ebirn on 11.10.13.
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class BterTicker {


    // {"result":"true","last":798,"high":806,"low":795.01,"avg":799.69,"sell":805.7,"buy":798,"vol_btc":738.7863,"vol_cny":590796.48}%

    @XmlElement(name="buy")
    BigDecimal ask;

    @XmlElement(name="sell")
    BigDecimal bid;

    @XmlElement
    double high;

    @XmlElement
    double low;

    @XmlElement
    BigDecimal last;

    /*
    @XmlElementRefs({
            @XmlElementRef(name = "vol_btc"),
            @XmlElementRef(name = "vol_ltc"),
            @XmlElementRef(name = "vol_ftc")
    })*/
    @XmlElement(name="vol_btc")
    BigDecimal volume;

    public TickerValue getValue(AssetPair asset) {

        Currency quote = asset.getQuote();
        TickerValue val = new TickerValue(asset);

        val.setAsk(new CurrencyValue(ask, quote));
        val.setBid(new CurrencyValue(bid, quote));

        val.setLast(new CurrencyValue(last, quote));

        val.setVolume(new CurrencyValue(volume, asset.getBase()));

        return val;
    }

}
