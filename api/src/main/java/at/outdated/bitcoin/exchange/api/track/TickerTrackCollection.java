package at.outdated.bitcoin.exchange.api.track;

import at.outdated.bitcoin.exchange.api.market.TickerValue;
import org.apache.commons.math3.stat.descriptive.MultivariateSummaryStatistics;

import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: ebirn
 * Date: 09.05.13
 * Time: 15:41
 * To change this template use File | Settings | File Templates.
 */

public class TickerTrackCollection extends ValueTrackCollection<TickerValue, TickerValueTrack> {



    public TickerTrackCollection() {

        for(TrackInterval interval : TrackInterval.values()) {
            tracks.put(interval, new TickerValueTrack(interval.numSamples()));
        }

        latest = new TickerValue();
        latest.setTimestamp(new Date(0L));
    }



    // return statistics for a specific window
    public MultivariateSummaryStatistics getStatistics(TrackInterval interval) {
        return tracks.get(interval).getStatistics();
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
