package at.outdated.bitcoin.exchange.api.container;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: ebirn
 * Date: 16.05.13
 * Time: 21:14
 * To change this template use File | Settings | File Templates.
 */
public class EnumContainer<E extends Enum, T> implements Iterable<T> {


    protected Map<E, T> container = new HashMap<>();


    public Iterator<T> iterator() {
        return container.values().iterator();
    }

    public void clear() {
        container.clear();
    }


    public T get(E e) {
        T t = container.get(e);

        return t;
    }

    public void set(E e, T t) {
        if(t != null) {
            container.put(e, t);
        }
    }

    public Set<E> getKeys() {
        return container.keySet();
    }

}
