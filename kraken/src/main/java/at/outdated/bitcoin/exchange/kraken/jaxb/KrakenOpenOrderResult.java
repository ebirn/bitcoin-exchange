package at.outdated.bitcoin.exchange.kraken.jaxb;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by ebirn on 05.01.14.
 */
@XmlRootElement
public class KrakenOpenOrderResult {


    @XmlElement
    String open;

    public String getOpen() {
        return open;
    }
}
