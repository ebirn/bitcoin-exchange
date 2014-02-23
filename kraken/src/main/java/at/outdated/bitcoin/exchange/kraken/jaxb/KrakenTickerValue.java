package at.outdated.bitcoin.exchange.kraken.jaxb;

import at.outdated.bitcoin.exchange.api.market.TickerValue;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.math.BigDecimal;

/**
 * Created with IntelliJ IDEA.
 * User: ebirn
 * Date: 17.09.13
 * Time: 18:37
 * To change this template use File | Settings | File Templates.
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class KrakenTickerValue {

    @XmlElement
    String a[];

    @XmlElement
    String b[];

    @XmlElement
    String c[];


    @XmlElement
    String v[];

    @XmlElement
    String p[];

    @XmlElement
    int t[];

    @XmlElement
    String l[];

    @XmlElement
    String h[];

    @XmlElement
    String o;


    public void setA(String[] a) {
        this.a = a;
    }

    public void setB(String[] b) {
        this.b = b;
    }

    public void setC(String[] c) {
        this.c = c;
    }

    public void setV(String[] v) {
        this.v = v;
    }

    public void setP(String[] p) {
        this.p = p;
    }

    public void setT(int[] t) {
        this.t = t;
    }

    public void setL(String[] l) {
        this.l = l;
    }

    public void setH(String[] h) {
        this.h = h;
    }

    public void setO(String o) {
        this.o = o;
    }

    public TickerValue getValue() {
        TickerValue value = new TickerValue();

        value.setLast(new BigDecimal(l[0]));

        value.setAsk(new BigDecimal(a[0]));
        value.setBid(new BigDecimal(b[0]));

        value.setVolume(new BigDecimal(v[0]));

        value.setHigh(new BigDecimal(h[0]));
        value.setLow(new BigDecimal(l[0]));

        return value;
    }
}
