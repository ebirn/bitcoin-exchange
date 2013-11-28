package at.outdated.bitcoin.exchange.api.market;

import at.outdated.bitcoin.exchange.api.currency.Currency;

import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: ebirn
 * Date: 23.05.13
 * Time: 21:21
 * To change this template use File | Settings | File Templates.
 */
public class Markets {

    private static ServiceLoader<Market> loader = ServiceLoader.load(Market.class);

    private static Market defaultMarket;
    private static Map<String,Market> marketMap = new HashMap<>();

    private static final String defaultMarketKey = "mtgox";

    static {
        for(Market m : loader) {
            if(m.getKey().equalsIgnoreCase(defaultMarketKey)) defaultMarket = m;
            marketMap.put(m.getKey(), m);
        }
    }


    public static Market getDefaultMarket() {
        return defaultMarket;
    }

    public static Market getMarket(String key) {
        return marketMap.get(key);
    }


    public static Iterable<Market> allMarkets() {
        return loader;
    }

    public static Set<Market> getMarkets(Currency currency) {

        Set<Market> markets = new HashSet<>();

        for(Market m : marketMap.values()) {
            if(m.getCurrencies().contains(currency)) {
                markets.add(m);
            }
        }

        return markets;
    }

    public static Set<Market> getMarkets(AssetPair asset) {

        Set<Market> markets = new HashSet<>();

        for(Market m : marketMap.values()) {
            if(m.getTradedAssets().contains(asset)) {
                markets.add(m);
            }
        }

        return markets;
    }


    public static Set<Market> getMarkets(Currency a, Currency b) {
        Set<Market> markets = new HashSet<>();

        for(Market m : marketMap.values()) {
            if(m.getAsset(a, b) != null) {
                markets.add(m);
            }
        }

        return markets;
    }
}
