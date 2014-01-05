package at.outdated.bitcoin.exchange.kraken.jaxb;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.util.List;
import java.util.Map;

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
