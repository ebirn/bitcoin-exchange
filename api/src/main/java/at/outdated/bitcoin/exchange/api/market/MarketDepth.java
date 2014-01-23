package at.outdated.bitcoin.exchange.api.market;

import at.outdated.bitcoin.exchange.api.currency.Currency;
import at.outdated.bitcoin.exchange.api.currency.CurrencyValue;

import java.util.*;

/**
 * Created by ebirn on 22.09.13.
 */
public class MarketDepth {

    Date timestamp = new Date();

    AssetPair asset;

    SortedSet<MarketOrder> bids = new TreeSet<>(new OrderComparator(OrderType.BID));
    SortedSet<MarketOrder> asks = new TreeSet<>(new OrderComparator(OrderType.ASK));

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

    public SortedSet<MarketOrder> getBids() {
        return Collections.unmodifiableSortedSet(bids);
    }

    public SortedSet<MarketOrder> getAsks() {
        return Collections.unmodifiableSortedSet(asks);
    }

    public void addAsk(MarketOrder ask) {
        ask.setAsset(asset);
        ask.setType(OrderType.ASK);
        asks.add(ask);
    }

    public void addAsk(double volume, double price) {
        MarketOrder order = new MarketOrder(OrderType.ASK, new CurrencyValue(volume, asset.getBase()), new CurrencyValue(price, asset.getQuote()));
        order.setAsset(asset);
        addAsk(order);
    }

    public void addBid(MarketOrder bid) {
        bid.setAsset(asset);
        bid.setType(OrderType.BID);
        this.bids.add(bid);
    }

    public void addBid(double volume, double price) {
        MarketOrder order = new MarketOrder(OrderType.BID, new CurrencyValue(volume, asset.getBase()), new CurrencyValue(price, asset.getQuote()));
        order.setAsset(asset);
        addBid(order);
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public int totalOrderCount() {
        return bids.size() + asks.size();
    }

    public CurrencyValue totalOrderVolume() {

        CurrencyValue total = new CurrencyValue(asset.getBase());

        for(MarketOrder order : asks)
            total.add(order.getVolume());

        for(MarketOrder order : bids)
            total.add(order.getVolume());

        return total;
    }

    public CurrencyValue totalOrderPriceVolume() {
        CurrencyValue total = new CurrencyValue(asset.getQuote());

        for(MarketOrder order : asks) {
            CurrencyValue priceVolume = new CurrencyValue(order.getPrice());
            priceVolume.multiply(order.getVolume().getValue());
            total.add(order.getVolume());
        }


        for(MarketOrder order : bids) {
            CurrencyValue priceVolume = new CurrencyValue(order.getPrice());
            priceVolume.multiply(order.getVolume().getValue());
            total.add(order.getVolume());
        }

        return total;
    }

    private String orderSummary(SortedSet<MarketOrder> orders) {

        if(orders == null || orders.isEmpty()) return "none";

        CurrencyValue volumeSum = new CurrencyValue(orders.first().getVolume().getCurrency());
        CurrencyValue priceSum = new CurrencyValue(orders.first().getPrice().getCurrency());

        for(MarketOrder order : orders) {
            volumeSum.add(order.getVolume());
            priceSum.add(order.getPriceVolume());
        }

        return orders.size() + " orders (vol: " + volumeSum + " @ " + priceSum + ")";
    }



    @Override
    public String toString() {
        String bidSummary = orderSummary(bids);
        String askSummary = orderSummary(asks);

        return "Depth: bids: " + bidSummary + ", asks: " + askSummary;
    }
}
