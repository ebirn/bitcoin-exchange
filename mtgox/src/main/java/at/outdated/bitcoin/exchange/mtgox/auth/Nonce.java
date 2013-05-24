package at.outdated.bitcoin.exchange.mtgox.auth;

import java.util.Date;
import java.util.Random;

/**
 * Created with IntelliJ IDEA.
 * User: ebirn
 * Date: 03.05.13
 * Time: 10:47
 * To change this template use File | Settings | File Templates.
 */
public class Nonce {

    static long nonce = 0;
    static Random rnd = new Random();

    static {
        Date now = new Date();
        rnd.setSeed(now.getTime());

        nonce = rnd.nextInt(10000);
    }

    public static long next() {
        //nonce += increment();


        Date now = new Date();

        return now.getTime();
    }

    private static long increment() {
        return rnd.nextInt(1000);
    }

}
