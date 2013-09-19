package at.outdated.bitcoin.exchange.kraken;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created with IntelliJ IDEA.
 * User: ebirn
 * Date: 17.09.13
 * Time: 18:36
 * To change this template use File | Settings | File Templates.
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class TickerResponseResult {

    @XmlElement
    private KrakenTickerValue XXBTZEUR;


    public KrakenTickerValue getXXBTZEUR() {
        return XXBTZEUR;
    }
}
