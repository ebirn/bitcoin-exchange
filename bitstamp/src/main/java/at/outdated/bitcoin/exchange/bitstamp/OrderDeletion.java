package at.outdated.bitcoin.exchange.bitstamp;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by ebirn on 13.01.14.
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class OrderDeletion {

    @XmlElement
    String error;


    public String getError() {
        return error;
    }
}
