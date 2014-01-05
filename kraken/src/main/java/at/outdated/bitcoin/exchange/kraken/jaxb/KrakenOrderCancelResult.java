package at.outdated.bitcoin.exchange.kraken.jaxb;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

/**
 * Created by ebirn on 05.01.14.
 */
@XmlRootElement
public class KrakenOrderCancelResult {

    @XmlElement
    int count;

    @XmlElement
    List<String> pending;

    public int getCount() {
        return count;
    }

    public List<String> getPending() {
        return pending;
    }

    public boolean isSuccess() {

        if(count > 0) return true;

        if(pending != null && pending.isEmpty() == false) return true;

        return false;
    }
}
