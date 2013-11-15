package at.outdated.bitcoin.exchange.api.container;

import at.outdated.bitcoin.exchange.api.market.AssetPair;
import at.outdated.bitcoin.exchange.api.market.Market;
import at.outdated.bitcoin.exchange.api.currency.Currency;
import at.outdated.bitcoin.exchange.api.market.MarketContainer;

import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: ebirn
 * Date: 27.05.13
 * Time: 12:44
 * To change this template use File | Settings | File Templates.
 */
public class MarketAssetContainer<T> extends MarketContainer<Map<AssetPair,T>> {


    public T get(Market market, AssetPair asset) {
        return this.get(market).get(asset);
    }


    public void set(Market market, AssetPair asset, T value) {
        this.get(market).put(asset, value);
    }
}
