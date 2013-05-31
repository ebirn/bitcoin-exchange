package at.outdated.bitcoin.exchange.api.track;

/**
 * Created with IntelliJ IDEA.
 * User: ebirn
 * Date: 09.05.13
 * Time: 15:42
 * To change this template use File | Settings | File Templates.
 */
public enum TrackInterval {

    MIN1(1000L * 60L),
    MIN5(1000L * 60L * 5L),
    MIN15(1000L * 60L * 15L),
    MIN20(1000L * 60L* 20L),
    MIN30(1000L * 60L* 30L),
    H1(1000L * 60L * 60L),
    H2(1000L * 60L * 60L * 2L),
    H3(1000L * 60L * 60L * 3L),
    H4(1000L * 60L * 60L * 4L),
    H6(1000L * 60L * 60L * 6L),
    H12(1000L * 60L * 60L * 12L),
    H24(1000L * 60L * 60L * 24L);


    private long duration;

    private static final long SAMPLES_PER_MINUTE = 1;

    private TrackInterval(long durationInMillis) {
        this.duration = durationInMillis;
    }

    public long duration() {
        return duration;
    }

    public int numSamples() {
        return (int) (duration/1000/60/SAMPLES_PER_MINUTE);
    }


}
