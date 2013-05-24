package at.outdated.bitcoin.exchange.api.track;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

/**
 * Created with IntelliJ IDEA.
 * User: ebirn
 * Date: 10.05.13
 * Time: 14:03
 * To change this template use File | Settings | File Templates.
 */
public class NumberValueTrack extends ValueTrack<Number> {

    public NumberValueTrack(int count) {
        super(count);
    }

    public DescriptiveStatistics getStatistics() {

        DescriptiveStatistics stats = new DescriptiveStatistics();

        for(Number num : this.valueBuffer) {
            stats.addValue(num.doubleValue());
        }

        return stats;
    }
}
