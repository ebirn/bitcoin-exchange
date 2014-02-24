package at.outdated.bitcoin.exchange.api.market;

import at.outdated.bitcoin.exchange.api.currency.Currency;
import at.outdated.bitcoin.exchange.api.currency.CurrencyValue;

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

    private CurrencyValue last, bid, ask, high, low, volume;

    public static final int DIMENSIONS = 4;

    private AssetPair asset = null;

    public TickerValue(AssetPair asset) {
        this.timestamp = new Date();
        this.asset = asset;
        last = new CurrencyValue(asset.getQuote());
        bid = new CurrencyValue(asset.getQuote());
        ask = new CurrencyValue(asset.getQuote());
        high = new CurrencyValue(asset.getQuote());
        low = new CurrencyValue(asset.getQuote());

        volume = new CurrencyValue(asset.getBase());
    }

    public static final TickerValue createNanInstance(AssetPair asset) {

        Currency quote = asset.getQuote();

        TickerValue ticker = new TickerValue(null, new CurrencyValue(quote), new CurrencyValue(quote), new CurrencyValue(quote), new CurrencyValue(quote), new CurrencyValue(quote), new CurrencyValue(quote), asset);
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

    public TickerValue(Date timestamp, CurrencyValue last, CurrencyValue bid, CurrencyValue ask, CurrencyValue volume, CurrencyValue high, CurrencyValue low, AssetPair asset) {
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

    public CurrencyValue getLast() {
        return last;
    }

    public void setLast(CurrencyValue last) {
        this.last = last;
    }

    public CurrencyValue getBid() {
        return bid;
    }

    public void setBid(CurrencyValue bid) {
        this.bid = bid;
    }

    public CurrencyValue getAsk() {
        return ask;
    }

    public void setAsk(CurrencyValue ask) {
        this.ask = ask;
    }

    public CurrencyValue getHigh() {
        return high;
    }

    public void setHigh(CurrencyValue high) {
        this.high = high;
    }

    public CurrencyValue getLow() {
        return low;
    }

    public void setLow(CurrencyValue low) {
        this.low = low;
    }

    public CurrencyValue getVolume() {
        return volume;
    }

    public void setVolume(CurrencyValue volume) {
        this.volume = volume;
    }

    public void setAsset(AssetPair asset) {
        this.asset = asset;
    }

    public AssetPair getAsset() {
        return asset;
    }

    public CurrencyValue getBidAskSpread() {
        return bid.subtract(ask);
    }

    public CurrencyValue getMiddle() {
        CurrencyValue middle = new CurrencyValue(bid);

        return middle.add(ask).divide(new BigDecimal(2.0));
    }

    @Override
    public String toString() {
        return "Ticker("+asset.getBase()+"): " + getMiddle();
    }
}
