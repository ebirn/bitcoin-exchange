package at.outdated.bitcoin.exchange.kraken.jaxb;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by ebirn on 29.09.13.
 */

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class KrakenTickerResponse {


    @XmlElement
    protected Object[] error;


    @XmlElement
    protected TickerResponseResult result;

    public TickerResponseResult getResult() {
        return result;
    }
}
