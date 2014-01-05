package at.outdated.bitcoin.exchange.kraken.jaxb;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by ebirn on 05.01.14.
 */
@XmlRootElement
public class KrakenResponse<R> {

    @XmlElement
    Object[] error;


    @XmlElement
    R result;


    public Object[] getError() {
        return error;
    }

    public R getResult() {
        return result;
    }
}
