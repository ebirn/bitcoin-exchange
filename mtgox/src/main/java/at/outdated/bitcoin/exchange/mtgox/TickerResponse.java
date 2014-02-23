package at.outdated.bitcoin.exchange.mtgox;


import at.outdated.bitcoin.exchange.api.currency.Currency;
import at.outdated.bitcoin.exchange.api.currency.CurrencyValue;
import at.outdated.bitcoin.exchange.api.market.AssetPair;
import at.outdated.bitcoin.exchange.api.market.TickerValue;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: ebirn
 * Date: 02.05.13
 * Time: 20:17
 * To change this template use File | Settings | File Templates.
 */
@XmlRootElement(name = "data")
@XmlAccessorType(XmlAccessType.FIELD)
public class TickerResponse {

    public static final long TIME_MULTIPLICATOR = 1000L;

    @XmlElement
    private long now;

    @XmlElement
    private Currency item;

    private Currency inCurrency;

    @XmlElement
    private MtGoxCurrencyValue high;

    @XmlElement
    private MtGoxCurrencyValue low;

    @XmlElement
    private MtGoxCurrencyValue avg;

    @XmlElement
    private MtGoxCurrencyValue vwap;

    @XmlElement
    private MtGoxCurrencyValue vol;

    @XmlElement
    private MtGoxCurrencyValue last_local;

    @XmlElement
    private MtGoxCurrencyValue last_orig;

    @XmlElement
    private MtGoxCurrencyValue last_all;

    @XmlElement
    private MtGoxCurrencyValue last;

    @XmlElement
    private MtGoxCurrencyValue buy;

    @XmlElement
    private MtGoxCurrencyValue sell;


    public Date getTimestamp() {
        return new Date(now/TIME_MULTIPLICATOR);
    }

    public Currency getItemCurrency() {
        return item;
    }

    public void setItemCurrency(Currency curr) {
        this.item = curr;
    }

    public CurrencyValue getHigh() {
        return high.convert();
    }

    public CurrencyValue getLow() {
        return low.convert();
    }

    public CurrencyValue getAvg() {
        return avg.convert();
    }

    public CurrencyValue getVwap() {
        return vwap.convert();
    }

    public CurrencyValue getVol() {
        return vol.convert();
    }

    public CurrencyValue getLast_local() {
        return last_local.convert();
    }

    public CurrencyValue getLast_orig() {
        return last_orig.convert();
    }

    public CurrencyValue getLast_all() {
        return last_all.convert();
    }

    public CurrencyValue getLast() {
        return last.convert();
    }

    public CurrencyValue getBuy() {
        return buy.convert();
    }

    public CurrencyValue getSell() {
        return sell.convert();
    }

    public Currency getInCurrency() {
        return inCurrency;
    }

    public void setInCurrency(Currency curr) {
        this.inCurrency = curr;
    }


    public String toString() {
        return item + "-" + inCurrency + ", last:" + last.getValue() + ", buy:" + buy.getValue() + ", sell:" + sell.getValue();
    }



    public double[] getValueArray() {
        return new double[] {getTimestamp().getTime() , last.getValue(), buy.getValue(), sell.getValue(), vol.getValue(), high.getValue(), avg.getValue(), low.getValue(), vwap.getValue()};
    }

    public TickerValue getTickerValue() {
        TickerValue value = new TickerValue();

        value.setTimestamp(getTimestamp());

        value.setLast(getLast().getValue());

        value.setAsk(getSell().getValue());
        value.setBid(getBuy().getValue());

        value.setLow(getLow().getValue());
        value.setHigh(getHigh().getValue());

        value.setVolume(getVol().getValue());

        value.setAsset(new AssetPair(item, inCurrency));

        return value;
    }
}
