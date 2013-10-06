package at.outdated.bitcoin.exchange.btce;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by ebirn on 06.10.13.
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class InfoResponse {

    @XmlElement
    int success;

    @XmlElement(name="return")
    BtcEAccountInfo result;
}
