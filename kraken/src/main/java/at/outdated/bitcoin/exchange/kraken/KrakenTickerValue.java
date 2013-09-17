package at.outdated.bitcoin.exchange.kraken;

import javax.xml.bind.annotation.XmlElement;

/**
 * Created with IntelliJ IDEA.
 * User: ebirn
 * Date: 17.09.13
 * Time: 18:37
 * To change this template use File | Settings | File Templates.
 */
public class KrakenTickerValue {

    @XmlElement
    String a[];

    @XmlElement
    String b[];

    @XmlElement
    String c[];


    @XmlElement
    double v[];

    @XmlElement
    double p[];


    @XmlElement
    double t[];

    @XmlElement
    String l[];

    @XmlElement
    String h[];

    double o;

}
