package at.outdated.bitcoin.exchange.api.track;

import at.outdated.bitcoin.exchange.api.market.TickerValue;
import org.apache.commons.math3.stat.descriptive.MultivariateSummaryStatistics;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: ebirn
 * Date: 09.05.13
 * Time: 15:41
 * To change this template use File | Settings | File Templates.
 */

public class TrackCollection {

    private Map<TrackInterval,TickerValueTrack> tracks = new HashMap<>(TrackInterval.values().length);

    private TickerValue latest;

    public TrackCollection() {

        for(TrackInterval interval : TrackInterval.values()) {
            tracks.put(interval, new TickerValueTrack(interval.numSamples()));
        }
    }


    public void insert(TickerValue value) {
        this.latest = value;
        for(ValueTrack track : tracks.values()) {
            track.insert(value);
        }
    }

    public TickerValue getLatest() {
        return latest;
    }


    // return statistics for a specific window
    public MultivariateSummaryStatistics getStatistics(TrackInterval interval) {
        return tracks.get(interval).getStatistics();
    }


    public TickerValue[] getTrackValues(TrackInterval trackInterval) {

        TickerValueTrack track = getTrack(trackInterval);
        TickerValue[] values = new TickerValue[track.getTrackLength()];

        return track.valueBuffer.toArray(values);
    }

    public TickerValueTrack getTrack(TrackInterval interval) {
        TickerValueTrack track = tracks.get(interval);
        return track;
    }


    /*
    // TODO: return cumulative Statistics for all tracks
    public DescriptiveStatistics getStatistics() {
        MultivariateSummaryStatistics stats = new MultivariateSummaryStatistics(TickerValue.DIMENSIONS, false);
        return null;
    }
*/


    public Date getOldestTimestamp() {
        Date oldest = new Date();

        for(TickerValueTrack track : tracks.values()) {

            TickerValue tickerValue = track.getOldest();
            if(tickerValue == null) continue;

            Date timestamp = tickerValue.getTimestamp();
            if(timestamp != null && timestamp.before(oldest)) {
                // also: make sure we copy information
                oldest.setTime(timestamp.getTime());
            }
        }

        return oldest;
    }
}
