package at.outdated.bitcoin.exchange.api.market;

import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: ebirn
 * Date: 23.05.13
 * Time: 21:21
 * To change this template use File | Settings | File Templates.
 */
public class Markets {

    private static Market defaultMarket;
    private static Map<String,Market> marketMap = new HashMap<>();

    private static String defaultMarketKey = "kraken";


    public static void loadMarkets() {
        ServiceLoader<Market> loader = ServiceLoader.load(Market.class);
        for(Market m : loader) {

            if(m.getKey().equalsIgnoreCase(defaultMarketKey)) defaultMarket = m;
            registerMarket(m);
        }
    }

    public static void setDefaultMarket(Market market) {
        defaultMarket = market;
    }

    public static boolean unregisterMarket(Market m) {
        return m == marketMap.remove(m.getKey());
    }

    public static boolean registerMarket(Market m) {

        boolean wasAdded = false;
        if(!marketMap.containsKey(m.getKey())) {
            wasAdded = (m == marketMap.put(m.getKey(), m));
        }
        else {
            wasAdded = false;
        }

        return wasAdded;
    }

    public static Market getDefaultMarket() {
        return defaultMarket;
    }

    public static Market getMarket(String key) {
        return marketMap.get(key);
    }

    public static Set<Market> allMarkets() {
        return new HashSet(marketMap.values());
    }
}
