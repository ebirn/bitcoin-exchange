package at.outdated.bitcoin.exchange.api;

import at.outdated.bitcoin.exchange.api.market.Market;

/**
 * Created by ebirn on 05.01.14.
 */
public class OrderId {

    String identifier;

    Market market;

    public OrderId(Market market, String identifier) {
        this.identifier = identifier;
        this.market = market;
    }

    public OrderId(Market market) {
        this.market = market;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public Market getMarket() {
        return market;
    }

    @Override
    public String toString() {
        return market + ":" + identifier;
    }
}
