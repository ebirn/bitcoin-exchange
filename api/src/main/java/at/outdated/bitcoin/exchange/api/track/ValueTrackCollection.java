package at.outdated.bitcoin.exchange.api.track;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: ebirn
 * Date: 30.05.13
 * Time: 21:59
 * To change this template use File | Settings | File Templates.
 */
public class ValueTrackCollection<V, T extends ValueTrack<V>> implements Iterable<T> {

    protected Map<TrackInterval,T> tracks = new HashMap<>(TrackInterval.values().length);

    protected V latest;


    public void insert(V value) {
        this.latest = value;
        for(ValueTrack track : tracks.values()) {
            track.insert(value);
        }
    }

    public V getLatest() {
        return latest;
    }


    public T getTrack(TrackInterval interval) {
        T track = tracks.get(interval);
        return track;
    }


    @Override
    public Iterator<T> iterator() {
        return tracks.values().iterator();
    }

    public void setTrack(TrackInterval ti, T track) {
        tracks.put(ti, track);
    }
}
