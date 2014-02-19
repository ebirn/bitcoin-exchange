package at.outdated.bitcoin.exchange.api.track;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

/**
 * Created with IntelliJ IDEA.
 * User: ebirn
 * Date: 10.05.13
 * Time: 14:03
 * To change this template use File | Settings | File Templates.
 */
public class NumberTrack extends ValueTrack<Number> {

    DescriptiveStatistics stats;

    public NumberTrack(int count) {
        super(count);
        stats = new DescriptiveStatistics(count);
    }

    @Override
    public void insert(Number value) {
        super.insert(value);
        stats.addValue(value.doubleValue());
    }

    public DescriptiveStatistics getStatistics() {
        return stats;
    }
}
