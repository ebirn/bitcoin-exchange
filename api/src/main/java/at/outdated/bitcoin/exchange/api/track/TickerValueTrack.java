package at.outdated.bitcoin.exchange.api.track;

import at.outdated.bitcoin.exchange.api.market.TickerValue;
import org.apache.commons.math3.stat.descriptive.MultivariateSummaryStatistics;

/**
 * Created with IntelliJ IDEA.
 * User: ebirn
 * Date: 10.05.13
 * Time: 13:58
 * To change this template use File | Settings | File Templates.
 */
public class TickerValueTrack extends ValueTrack<TickerValue> {


    public TickerValueTrack(int length) {
        super(length);
    }


    public MultivariateSummaryStatistics getStatistics() {

        MultivariateSummaryStatistics stats = new MultivariateSummaryStatistics(TickerValue.DIMENSIONS+1, false);

        for (TickerValue observation : this.valueBuffer) {
            stats.addValue(observation.getValue());
        }

        return stats;
    }

}
