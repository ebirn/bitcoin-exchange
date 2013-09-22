package at.outdated.bitcoin.exchange.kraken;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by ebirn on 22.09.13.
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class KrakenDepthValue {

    @XmlElement
    float[][] asks;

    @XmlElement
    float[][] bids;

    public float[][] getAsks() {
        return asks;
    }

    public float[][] getBids() {
        return bids;
    }
}
