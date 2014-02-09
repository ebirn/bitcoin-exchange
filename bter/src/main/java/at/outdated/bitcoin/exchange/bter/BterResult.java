package at.outdated.bitcoin.exchange.bter;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by ebirn on 09.02.14.
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class BterResult {
    // {"result":"true","data":[ ...

    @XmlElement
    String result;

    public boolean isSuccess() {
        return "true".equalsIgnoreCase(result);
    }

}
