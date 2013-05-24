package at.outdated.bitcoin.exchange.api.market;

import at.outdated.bitcoin.exchange.api.currency.Currency;

import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: ebirn
 * Date: 07.05.13
 * Time: 23:01
 * To change this template use File | Settings | File Templates.
 */
public class TickerValue extends TimedValue<double[]> {

    private Date timestamp = new Date();

    private double last, buy, sell, high, low, volume, avg, volumeWeightedAvg;

    public static final int DIMENSIONS = 8;

    private Currency currency = Currency.EUR;

    public TickerValue() {

    }

    public static final TickerValue createNanInstance(Currency curr) {
        TickerValue ticker = new TickerValue(null, Double.NaN, Double.NaN, Double.NaN, Double.NaN, Double.NaN, Double.NaN, Double.NaN, Double.NaN, curr);
        return ticker;
    }

    public static final TickerValue createNanInstance() {
        return createNanInstance(Currency.BTC);
    }

    public TickerValue(TickerValue other) {
        this.timestamp = other.timestamp;
        this.last = other.last;
        this.buy = other.buy;
        this.sell = other.sell;
        this.low = other.low;
        this.avg = other.avg;
        this.high = other.high;

        this.volume = other.volume;
        this.volumeWeightedAvg = other.volumeWeightedAvg;
        this.currency = other.currency;
    }

    public TickerValue(Date timestamp, double last, double buy, double sell, double volume, double high, double avg, double low, double volumeWeightedAvg, Currency curr) {
        this.timestamp = timestamp;
        this.last = last;
        this.buy = buy;
        this.sell = sell;
        this.low = low;
        this.avg = avg;
        this.high = high;

        this.volume = volume;
        this.volumeWeightedAvg = volumeWeightedAvg;
        this.currency = curr;
    }

    @Override
    public double[] getValue() {
        double[] value = {timestamp.getTime(), last, buy, sell, volume, high, avg, low, volumeWeightedAvg};
        return value;
    }


    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public double getLast() {
        return last;
    }

    public void setLast(double last) {
        this.last = last;
    }

    public double getBuy() {
        return buy;
    }

    public void setBuy(double buy) {
        this.buy = buy;
    }

    public double getSell() {
        return sell;
    }

    public void setSell(double sell) {
        this.sell = sell;
    }

    public double getHigh() {
        return high;
    }

    public void setHigh(double high) {
        this.high = high;
    }

    public double getLow() {
        return low;
    }

    public void setLow(double low) {
        this.low = low;
    }

    public double getVolume() {
        return volume;
    }

    public void setVolume(double volume) {
        this.volume = volume;
    }

    public double getAvg() {
        return avg;
    }

    public void setAvg(double avg) {
        this.avg = avg;
    }

    public double getVolumeWeightedAvg() {
        return volumeWeightedAvg;
    }

    public void setVolumeWeightedAvg(double volumeWeightedAvg) {
        this.volumeWeightedAvg = volumeWeightedAvg;
    }

    public void setCurrency(Currency curr) {
        this.currency = curr;
    }

    public Currency getCurrency() {
        return currency;
    }
}
