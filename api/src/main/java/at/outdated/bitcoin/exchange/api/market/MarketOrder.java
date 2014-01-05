package at.outdated.bitcoin.exchange.api.market;

import at.outdated.bitcoin.exchange.api.OrderId;
import at.outdated.bitcoin.exchange.api.currency.CurrencyValue;

/**
 * Created by ebirn on 22.09.13.
 */
public class MarketOrder {

    OrderId id;

    AssetPair asset;

    protected CurrencyValue price;
    protected CurrencyValue volume;

    protected TradeDecision decision;

    public MarketOrder() {

    }

    public MarketOrder(TradeDecision decision, CurrencyValue volume,  CurrencyValue price) {
        this.decision = decision;
        this.volume = volume;
        this.price = price;
    }


    public CurrencyValue getPrice() {
        return price;
    }

    public void setPrice(CurrencyValue price) {
        this.price = price;
    }

    public CurrencyValue getVolume() {
        return volume;
    }

    public void setVolume(CurrencyValue volume) {
        this.volume = volume;
    }

    @Override
    public String toString() {
        return "Order: " + decision + " " + volume  + " @ " + price;
    }

    public OrderId getId() {
        return id;
    }

    public void setId(OrderId id) {
        this.id = id;
    }
}
