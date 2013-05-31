package at.outdated.bitcoin.exchange.api.track;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: ebirn
 * Date: 30.05.13
 * Time: 21:59
 * To change this template use File | Settings | File Templates.
 */
public class ValueTrackCollection<V, T extends ValueTrack<V>> {

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

    public V[] getTrackValues(TrackInterval trackInterval) {

        ValueTrack<V> track = getTrack(trackInterval);

        V[] values = (V[]) new ArrayList<V>(track.getTrackLength()).toArray();

        return track.valueBuffer.toArray(values);
    }
}
