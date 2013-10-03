package at.outdated.bitcoin.exchange.api.market;

import at.outdated.bitcoin.exchange.api.currency.Currency;

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

    public MarketDepth(Currency base) {
        this.baseCurrency = base;
    }

    public List<MarketOrder> getBids() {
        return bids;
    }

    public List<MarketOrder> getAsks() {
        return asks;
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
}
