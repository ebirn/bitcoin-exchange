package at.outdated.bitcoin.exchange.bitstamp;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by ebirn on 22.11.13.
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class BitstampAddress {

    @XmlElement
    String address;

    public String getAddress() {
        return address;
    }
}
