package at.outdated.bitcoin.exchange.api.market;

import at.outdated.bitcoin.exchange.api.Market;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: ebirn
 * Date: 16.05.13
 * Time: 21:48
 * To change this template use File | Settings | File Templates.
 */
public class MarketContainer<T> {


    protected Map<Market, T> container = new HashMap<>();


    public Iterator<T> iterator() {
        return container.values().iterator();
    }

    public void clear() {
        container.clear();
    }


    public T get(Market e) {
        T t = container.get(e);

        return t;
    }

    public void set(Market e, T t) {
        if(t != null) {
            container.put(e, t);
        }
    }

    public Set<Market> getKeys() {
        return container.keySet();
    }
}
