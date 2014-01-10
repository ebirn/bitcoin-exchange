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
        ask.setAsset(asset);
        asks.add(ask);
    }

    public void addAsk(double volume, double price) {
        MarketOrder order = new MarketOrder(TradeDecision.BUY, new CurrencyValue(volume, asset.getBase()), new CurrencyValue(price, asset.getQuote()));
        order.setAsset(asset);
        addAsk(order);
    }

    public void addBid(MarketOrder bid) {
        bid.setAsset(asset);
        this.bids.add(bid);
    }

    public void addBid(double volume, double price) {
        MarketOrder order = new MarketOrder(TradeDecision.SELL, new CurrencyValue(volume, asset.getBase()), new CurrencyValue(price, asset.getQuote()));
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

        CurrencyValue total = new CurrencyValue(0.0, asset.getBase());

        for(MarketOrder order : asks)
            total.add(order.getVolume());

        for(MarketOrder order : bids)
            total.add(order.getVolume());

        return total;
    }

    public CurrencyValue totalOrderPriceVolume() {
        CurrencyValue total = new CurrencyValue(0.0, asset.getQuote());

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

    private String orderSummary(List<MarketOrder> orders) {

        if(orders == null || orders.isEmpty()) return "none";

        CurrencyValue volumeSum = new CurrencyValue(0.0, orders.get(0).getVolume().getCurrency());
        CurrencyValue priceSum = new CurrencyValue(0.0, orders.get(0).getPrice().getCurrency());

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
