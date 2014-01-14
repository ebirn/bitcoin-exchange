package at.outdated.bitcoin.exchange.mtgox;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by ebirn on 14.01.14.
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class OrderDeletionResult {

    @XmlElement
    String result;

    @XmlElement
    OrderDeletion data;

    public String getResult() {
        return result;
    }

    public OrderDeletion getData() {
        return data;
    }
}
