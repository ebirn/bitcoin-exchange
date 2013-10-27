package at.outdated.bitcoin.exchange.api.market;

import at.outdated.bitcoin.exchange.api.currency.Currency;
import at.outdated.bitcoin.exchange.api.currency.CurrencyValue;

import java.util.Date;
import java.util.List;

import java.util.ArrayList;

/**
 * Created by ebirn on 22.09.13.
 */
public class MarketDepth {

    Date timestamp = new Date();

    Currency baseCurrency;

    List<MarketOrder> bids = new ArrayList<>();
    List<MarketOrder> asks = new ArrayList<>();


    public MarketDepth() {

    }

    public MarketDepth(AssetPair asset) {
        setAsset(asset);
    }

    public MarketDepth(Currency base) {
        this.baseCurrency = base;
    }

    public void setAsset(AssetPair asset) {
        this.baseCurrency = asset.getBase();
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

    public void addBid(MarketOrder bid) {
        this.bids.add(bid);
    }

    public Currency getBaseCurrency() {
        return baseCurrency;
    }

    public void setBaseCurrency(Currency baseCurrency) {
        this.baseCurrency = baseCurrency;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    private String orderSummary(List<MarketOrder> orders) {

        if(orders == null || orders.isEmpty()) return "none";

        CurrencyValue sum = new CurrencyValue(orders.get(0).getVolume());

        for(MarketOrder order : orders) {
            sum.add(order.getVolume());
        }

        return orders.size() + " orders (vol: " + sum.getValue() + ")";
    }

    @Override
    public String toString() {
        String bidSummary = orderSummary(bids);
        String askSummary = orderSummary(asks);

        return "Depth: bids: " + bidSummary + ", asks: " + askSummary;
    }
}
