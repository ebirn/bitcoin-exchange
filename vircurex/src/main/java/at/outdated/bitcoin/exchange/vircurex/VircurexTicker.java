package at.outdated.bitcoin.exchange.vircurex;

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
public class VircurexTicker {

    // {"base":"BTC","alt":"LTC","lowest_ask":"62.30141425","highest_bid":"61.12503063","last_trade":"62.30529595","volume":"82.92624028"}%

    @XmlElement
    Currency base;

    @XmlElement(name="alt")
    Currency quote;

    @XmlElement(name="lowest_ask")
    BigDecimal ask;

    @XmlElement(name="highest_bid")
    BigDecimal bid;

    @XmlElement(name="last_trade")
    BigDecimal last;

    @XmlElement
    BigDecimal volume;

    public TickerValue getValue() {

        AssetPair asset = new AssetPair(base, quote);

        TickerValue val = new TickerValue(asset);

        val.setLast(new CurrencyValue(last, quote));

        val.setAsk(new CurrencyValue(ask, quote));
        val.setBid(new CurrencyValue(bid, quote));

        val.setVolume(new CurrencyValue(volume, base));

        return val;
    }

}
