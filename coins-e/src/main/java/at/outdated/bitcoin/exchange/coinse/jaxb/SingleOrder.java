package at.outdated.bitcoin.exchange.coinse.jaxb;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by ebirn on 30.01.14.
 */

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class SingleOrder extends BaseResponse {

    @XmlElement
    CoinseOrder order;

    public CoinseOrder getOrder() {
        return order;
    }
}
