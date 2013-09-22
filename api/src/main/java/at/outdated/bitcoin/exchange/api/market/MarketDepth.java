package at.outdated.bitcoin.exchange.api.market;

import at.outdated.bitcoin.exchange.api.currency.Currency;
import java.util.List;

import java.util.ArrayList;

/**
 * Created by ebirn on 22.09.13.
 */
public class MarketDepth {

    Currency baseCurrency;

    List<MarketOrder> bids = new ArrayList<>();
    List<MarketOrder> asks = new ArrayList<>();


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
}
