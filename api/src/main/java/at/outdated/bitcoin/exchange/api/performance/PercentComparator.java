package at.outdated.bitcoin.exchange.api.performance;

import java.io.Serializable;
import java.util.Comparator;

/**
 * Created with IntelliJ IDEA.
 * User: ebirn
 * Date: 25.06.13
 * Time: 14:02
 * To change this template use File | Settings | File Templates.
 */
public class PercentComparator implements Comparator<Performance>, Serializable {

    @Override
    public int compare(Performance o1, Performance o2) {
        return o1.getPercent().compareTo(o2.getPercent());
    }
}
