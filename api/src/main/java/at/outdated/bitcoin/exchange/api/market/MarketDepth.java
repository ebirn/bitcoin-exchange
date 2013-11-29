package at.outdated.bitcoin.exchange.api.market;

import at.outdated.bitcoin.exchange.api.currency.Currency;
import at.outdated.bitcoin.exchange.api.currency.CurrencyValue;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by ebirn on 22.09.13.
 */
public class MarketDepth {

    Date timestamp = new Date();

    AssetPair asset;

    List<MarketOrder> bids = new ArrayList<>();
    List<MarketOrder> asks = new ArrayList<>();

    public MarketDepth() {

    }

    public MarketDepth(AssetPair asset) {
        setAsset(asset);
    }

    public MarketDepth(Currency base, Currency quote) {
        this.asset = new AssetPair(base, quote);
    }

    public void setAsset(AssetPair asset) {
        this.asset = asset;
    }

    public AssetPair getAsset() {
        return asset;
    }

    public List<MarketOrder> getBids() {
        return bids;
    }

    public List<MarketOrder> getAsks() {
        return asks;
    }

    public void addAsk(MarketOrder ask) {
        asks.add(ask);
    }

    public void addAsk(double volume, double price) {
        addAsk(new MarketOrder(TradeDecision.BUY, new CurrencyValue(volume, asset.getBase()), new CurrencyValue(price, asset.getQuote())));
    }

    public void addBid(MarketOrder bid) {
        this.bids.add(bid);
    }

    public void addBid(double volume, double price) {
        addBid(new MarketOrder(TradeDecision.SELL, new CurrencyValue(volume, asset.getBase()), new CurrencyValue(price, asset.getQuote())));
    }


    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    private String orderSummary(List<MarketOrder> orders) {

        if(orders == null || orders.isEmpty()) return "none";

        CurrencyValue volumeSum = new CurrencyValue(0.0, orders.get(0).getVolume().getCurrency());
        CurrencyValue priceSum = new CurrencyValue(0.0, orders.get(0).getPrice().getCurrency());


        for(MarketOrder order : orders) {
            volumeSum.add(order.getVolume());
            priceSum.add(order.getPrice().multiply(order.getVolume().getValue()));
        }

        return orders.size() + " orders (vol: " + volumeSum + " @ " + priceSum + ")";
    }

    public CurrencyValue getPrice(TradeDecision decision, CurrencyValue volume) throws IllegalStateException {

        List<MarketOrder> orders = null;
        switch(decision) {
            case BUY:
                orders = getAsks();
                break;

            case SELL:
                orders = getBids();
                break;

            default:
                throw new IllegalArgumentException("cannot process TradeDecision " + decision);
        }


        CurrencyValue total = new CurrencyValue(0.0, asset.getBase());

        double remaining = volume.getValue();

        for(MarketOrder order : orders) {
            double orderVol = order.getVolume().getValue();
            double orderPrice = order.getPrice().getValue();

            double additiveVol = orderVol;
            if(orderVol < remaining) {
                additiveVol = remaining;
            }

            total.add(new CurrencyValue(additiveVol * orderPrice, total.getCurrency()));

            remaining -= additiveVol;
            if(remaining < 0.000001) break;
        }

        // FIXME: this should actually be exact 0
        // TODO: use my own exception, return missing difference in exception to recalculate with less volume (find maximum tradeable volume)
        if(remaining > 0.000001) {
            throw new IllegalStateException("Insufficient market depth");
        }

        return total;
    }

    @Override
    public String toString() {
        String bidSummary = orderSummary(bids);
        String askSummary = orderSummary(asks);

        return "Depth: bids: " + bidSummary + ", asks: " + askSummary;
    }
}
