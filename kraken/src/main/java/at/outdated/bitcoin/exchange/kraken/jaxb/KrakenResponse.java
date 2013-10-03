package at.outdated.bitcoin.exchange.kraken.jaxb;

import javax.xml.bind.annotation.*;

/**
 * Created by ebirn on 22.09.13.
 */

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class KrakenResponse {

    @XmlElement
    protected Object[] error;


    @XmlElement
    protected KrakenResponseResult result;


    public Object[] getError() {
        return error;
    }


    public void setError(Object[] error) {
        this.error = error;
    }

    public KrakenResponseResult getResult() {
        return result;
    }
}
