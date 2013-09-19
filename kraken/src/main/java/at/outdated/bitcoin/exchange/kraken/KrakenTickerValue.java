package at.outdated.bitcoin.exchange.kraken;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

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

}
