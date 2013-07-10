package at.outdated.bitcoin.exchange.api.track;

import at.outdated.bitcoin.exchange.api.market.TickerValue;
import org.apache.commons.math3.stat.descriptive.MultivariateSummaryStatistics;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: ebirn
 * Date: 10.05.13
 * Time: 13:58
 * To change this template use File | Settings | File Templates.
 */
public class TickerValueTrack extends ValueTrack<TickerValue> {

    public TickerValueTrack(TickerValueTrack track) {
        super(track.valueBuffer);
    }

    public TickerValueTrack(Collection c) {
        super(c);
    }

    public TickerValueTrack(int length) {
        super(length);
    }

    @Override
    public void insert(TickerValue value) {
        super.insert(value);    //To change body of overridden methods use File | Settings | File Templates.
    }

    public MultivariateSummaryStatistics getStatistics() {

        MultivariateSummaryStatistics stats = new MultivariateSummaryStatistics(TickerValue.DIMENSIONS+1, false);

        for (TickerValue observation : this.valueBuffer) {
            stats.addValue(observation.getValue());
        }

        return stats;
    }

    public TickerValueTrack until(final Date date) {
        List<TickerValue> selected = new ArrayList<>();

        for(TickerValue ticker : valueBuffer) {
            if(ticker.getTimestamp().before(date))
                selected.add(ticker);
        }

        return new TickerValueTrack(selected);
    }

}
