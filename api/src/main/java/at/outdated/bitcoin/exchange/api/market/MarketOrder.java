package at.outdated.bitcoin.exchange.api.market;

import at.outdated.bitcoin.exchange.api.OrderId;
import at.outdated.bitcoin.exchange.api.currency.CurrencyValue;

import java.util.Date;

/**
 * Created by ebirn on 22.09.13.
 */
public class MarketOrder {

    OrderId id;

    AssetPair asset;

    protected CurrencyValue price;
    protected CurrencyValue volume;

    protected OrderType type;

    protected Date timestamp;

    public MarketOrder() {

    }

    public MarketOrder(OrderId id) {
        this.id = id;
    }

    public MarketOrder(OrderType type, CurrencyValue volume,  CurrencyValue price) {
        this.type = type;
        this.volume = volume;
        this.price = price;
    }


    public CurrencyValue getPrice() {
        return price;
    }

    public void setPrice(CurrencyValue price) {
        this.price = price;
    }

    public CurrencyValue getPriceVolume() {
        return new CurrencyValue(price).multiply(volume.getValue());
    }

    public CurrencyValue getVolume() {
        return volume;
    }

    public void setVolume(CurrencyValue volume) {
        this.volume = volume;
    }

    @Override
    public String toString() {
        return "Order: " + type + " " + volume  + " @ " + price;
    }

    public OrderId getId() {
        return id;
    }

    public void setId(OrderId id) {
        this.id = id;
    }

    public AssetPair getAsset() {
        return asset;
    }

    public void setAsset(AssetPair asset) {
        this.asset = asset;
    }

    public OrderType getType() {
        return type;
    }

    public void setType(OrderType type) {
        this.type = type;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }
}
