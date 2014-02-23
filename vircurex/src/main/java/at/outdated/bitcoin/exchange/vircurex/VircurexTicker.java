package at.outdated.bitcoin.exchange.vircurex;

import at.outdated.bitcoin.exchange.api.currency.Currency;
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
public class VircurexTicker {

    // {"base":"BTC","alt":"LTC","lowest_ask":"62.30141425","highest_bid":"61.12503063","last_trade":"62.30529595","volume":"82.92624028"}%

    @XmlElement
    Currency base;

    @XmlElement
    Currency alt;

    @XmlElement(name="lowest_ask")
    BigDecimal ask;

    @XmlElement(name="highest_bid")
    BigDecimal bid;

    @XmlElement(name="last_trade")
    double low;

    @XmlElement
    BigDecimal last;

    @XmlElement
    BigDecimal volume;

    public TickerValue getValue() {

        TickerValue val = new TickerValue();

        val.setAsk(ask);
        val.setBid(bid);
        val.setVolume(volume);
        val.setLast(last);

        val.setAsset(new AssetPair(base, alt));
        return val;
    }

}
