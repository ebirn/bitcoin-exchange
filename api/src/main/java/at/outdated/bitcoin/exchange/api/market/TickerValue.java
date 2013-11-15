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

    private double last, bid, ask, high, low, volume;

    public static final int DIMENSIONS = 4;

    private AssetPair asset = null;

    public TickerValue() {
        this.timestamp = new Date();
    }

    public static final TickerValue createNanInstance(AssetPair curr) {
        TickerValue ticker = new TickerValue(null, Double.NaN, Double.NaN, Double.NaN, Double.NaN, Double.NaN, Double.NaN, curr);
        return ticker;
    }

    public static final TickerValue createNanInstance() {
        return createNanInstance(null);
    }

    public TickerValue(TickerValue other) {
        this.timestamp = other.timestamp;
        this.last = other.last;
        this.bid = other.bid;
        this.ask = other.ask;
        this.low = other.low;
        this.high = other.high;

        this.volume = other.volume;
        this.asset = other.asset;
    }

    public TickerValue(Date timestamp, double last, double bid, double ask, double volume, double high, double low, AssetPair asset) {
        this.timestamp = timestamp;
        this.last = last;
        this.bid = bid;
        this.ask = ask;
        this.low = low;
        this.high = high;

        this.volume = volume;
        this.asset = asset;
    }

    @Override
    public double[] getValue() {
        double[] value = {timestamp.getTime(), last, bid, ask, volume};
        return value;
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

    public double getBid() {
        return bid;
    }

    public void setBid(double bid) {
        this.bid = bid;
    }

    public double getAsk() {
        return ask;
    }

    public void setAsk(double ask) {
        this.ask = ask;
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

    public void setAsset(AssetPair asset) {
        this.asset = asset;
    }

    public AssetPair getAsset() {
        return asset;
    }

    public double getBidAskSpread() {
        return bid-ask;
    }

    @Override
    public String toString() {

        String quote = "";
        if(asset != null) {
            quote = asset.getQuote().name();
        }

        return "Ticker: " + (bid+ask)/2.0 + " " + quote;
    }
}
