package at.outdated.bitcoin.exchange.api.market;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: ebirn
 * Date: 07.05.13
 * Time: 23:01
 * To change this template use File | Settings | File Templates.
 */
public class TickerValue extends TimedValue<double[]> {

    private BigDecimal last, bid, ask, high, low, volume;

    public static final int DIMENSIONS = 4;

    private AssetPair asset = null;

    public TickerValue() {
        this.timestamp = new Date();
    }

    public TickerValue(AssetPair asset) {
        this.timestamp = new Date();
        this.asset = asset;
    }

    public static final TickerValue createNanInstance(AssetPair curr) {
        TickerValue ticker = new TickerValue(null, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, curr);
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

    public TickerValue(Date timestamp, BigDecimal last, BigDecimal bid, BigDecimal ask, BigDecimal volume, BigDecimal high, BigDecimal low, AssetPair asset) {
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
        double[] value = {timestamp.getTime(), last.doubleValue(), bid.doubleValue(), ask.doubleValue(), volume.doubleValue()};
        return value;
    }


    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public BigDecimal getLast() {
        return last;
    }

    public void setLast(BigDecimal last) {
        this.last = last;
    }

    public BigDecimal getBid() {
        return bid;
    }

    public void setBid(BigDecimal bid) {
        this.bid = bid;
    }

    public BigDecimal getAsk() {
        return ask;
    }

    public void setAsk(BigDecimal ask) {
        this.ask = ask;
    }

    public BigDecimal getHigh() {
        return high;
    }

    public void setHigh(BigDecimal high) {
        this.high = high;
    }

    public BigDecimal getLow() {
        return low;
    }

    public void setLow(BigDecimal low) {
        this.low = low;
    }

    public BigDecimal getVolume() {
        return volume;
    }

    public void setVolume(BigDecimal volume) {
        this.volume = volume;
    }

    public void setAsset(AssetPair asset) {
        this.asset = asset;
    }

    public AssetPair getAsset() {
        return asset;
    }

    public BigDecimal getBidAskSpread() {
        return bid.subtract(ask);
    }

    public BigDecimal getMiddle() {
        return bid.add(ask).divide(new BigDecimal(2.0));
    }

    @Override
    public String toString() {

        String quote = "";
        if(asset != null) {
            quote = asset.getQuote().name();
        }

        return "Ticker: " + getMiddle() + " " + quote;
    }
}
