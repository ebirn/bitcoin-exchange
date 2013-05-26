package at.outdated.bitcoin.exchange.mtgox;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created with IntelliJ IDEA.
 * User: ebirn
 * Date: 03.05.13
 * Time: 11:21
 * To change this template use File | Settings | File Templates.
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class ApiAccountInfo extends ApiResponse {

    @XmlElement(name="data")
    protected MtGoxAccountInfo data;

    public MtGoxAccountInfo getData() {
        return data;
    }
}

