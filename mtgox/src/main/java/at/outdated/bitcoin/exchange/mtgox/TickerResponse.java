package at.outdated.bitcoin.exchange.mtgox;


import at.outdated.bitcoin.exchange.api.currency.Currency;
import at.outdated.bitcoin.exchange.api.currency.CurrencyValue;
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
    private CurrencyValue high;

    @XmlElement
    private CurrencyValue low;

    @XmlElement
    private CurrencyValue avg;

    @XmlElement
    private CurrencyValue vwap;

    @XmlElement
    private CurrencyValue vol;

    @XmlElement
    private CurrencyValue last_local;

    @XmlElement
    private CurrencyValue last_orig;

    @XmlElement
    private CurrencyValue last_all;

    @XmlElement
    private CurrencyValue last;

    @XmlElement
    private CurrencyValue buy;

    @XmlElement
    private CurrencyValue sell;


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
        return high;
    }

    public CurrencyValue getLow() {
        return low;
    }

    public CurrencyValue getAvg() {
        return avg;
    }

    public CurrencyValue getVwap() {
        return vwap;
    }

    public CurrencyValue getVol() {
        return vol;
    }

    public CurrencyValue getLast_local() {
        return last_local;
    }

    public CurrencyValue getLast_orig() {
        return last_orig;
    }

    public CurrencyValue getLast_all() {
        return last_all;
    }

    public CurrencyValue getLast() {
        return last;
    }

    public CurrencyValue getBuy() {
        return buy;
    }

    public CurrencyValue getSell() {
        return sell;
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

        // TODO this is a stupid workaround
        TickerResponse ticker = this;

        value.setTimestamp(ticker.getTimestamp());

        value.setLast(ticker.getLast().getValue());

        value.setBid(ticker.getSell().getValue());
        value.setAsk(ticker.getBuy().getValue());

        value.setLow(ticker.getLow().getValue());
        value.setHigh(ticker.getHigh().getValue());

        value.setVolume(ticker.getVol().getValue());

        value.setCurrency(ticker.getInCurrency());

        return value;
    }
}
