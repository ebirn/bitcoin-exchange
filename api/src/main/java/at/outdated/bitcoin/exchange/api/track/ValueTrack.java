package at.outdated.bitcoin.exchange.api.track;


import at.outdated.bitcoin.exchange.api.container.CircularStore;
import at.outdated.bitcoin.exchange.api.market.TimedValue;
import org.apache.commons.math3.stat.regression.MillerUpdatingRegression;
import org.apache.commons.math3.stat.regression.RegressionResults;

import java.util.Iterator;

/**
 * Created with IntelliJ IDEA.
 * User: ebirn
 * Date: 02.05.13
 * Time: 21:16
 * To change this template use File | Settings | File Templates.
 */
public abstract class ValueTrack<D> implements Iterable<D> {

    // assuming values every minute: track 1 hours
    private static final int LENGTH = 60;

    CircularStore<D> valueBuffer; //, diffBuffer, diff2Buffer;

    private long inserts = 0L;

    public ValueTrack() {
        init(LENGTH);
    }

    public ValueTrack(int length) {
        init(length);
    }

    protected void init(int length) {
        valueBuffer = new CircularStore<>(length);
    }


    public void insert(D value) {
        valueBuffer.add(value);
        inserts++;
    }

    private D getLatestBufferValue(CircularStore<D> buffer) {


        if(buffer.get() == null) return null;

        return ((D) buffer.get());
    }

    public D getLatest() {
        return getLatestBufferValue(valueBuffer);
    }



    public D getOldest() {
        return this.valueBuffer.getTail();
    }

    public RegressionResults getRegression() {
        int numberOfVariables = 8; // TODO: this sould not be hardcoded
        boolean includeConstant = true; // enables intercept
        double errorTolerance = 1.0;

        MillerUpdatingRegression regression = new MillerUpdatingRegression(numberOfVariables, includeConstant, errorTolerance);
        // maybe also check this: http://openforecast.sourceforge.net

        Iterator<TimedValue<double[]>> valueIt = valueBuffer.iterator();
        while(valueIt.hasNext()) {
            TimedValue<double[]> data = valueIt.next();
            regression.addObservation(data.getValue(), data.getTimestamp().getTime());
        }

        return regression.regress();
    }



    public int getTrackLength() {
        return valueBuffer.size();
    }


    public Iterator<D> iterator() {
        return valueBuffer.iterator();
    }

}
