package at.outdated.bitcoin.exchange.api.market;

import java.util.HashMap;
import java.util.Map;
import java.util.ServiceLoader;

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
}
