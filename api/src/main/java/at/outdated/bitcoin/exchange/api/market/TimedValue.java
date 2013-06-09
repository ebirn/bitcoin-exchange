package at.outdated.bitcoin.exchange.api.market;

import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: ebirn
 * Date: 04.05.13
 * Time: 18:18
 * To change this template use File | Settings | File Templates.
 */
public class TimedValue<V> implements Comparable<TimedValue<V>> {

    protected Date timestamp = new Date();
    protected V value;

    public TimedValue() {

    }

    public TimedValue(V value) {
        this.timestamp = new Date();
        this.value = value;
    }

    public TimedValue(V value, Date timestamp) {
        this.timestamp = timestamp;
        this.value = value;
    }


    public V getValue() {
        return value;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    @Override
    public int compareTo(TimedValue<V> o) {
        return this.getTimestamp().compareTo(o.getTimestamp());
    }
}
