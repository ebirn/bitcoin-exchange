package at.outdated.bitcoin.exchange.kraken.jaxb;

import javax.xml.bind.annotation.*;

/**
 * Created by ebirn on 22.09.13.
 */


@XmlAccessorType(XmlAccessType.FIELD)
public class KrakenResponse<T> {

    @XmlElement
    protected Object[] error;


    @XmlElementRefs({
        @XmlElementRef(type=KrakenDepthResponse.class),
        @XmlElementRef(type=KrakenTickerResponse.class)
    })
    protected KrakenResponseResult<T> result;


    public Object[] getError() {
        return error;
    }


    public void setResult(KrakenResponseResult<T> result) {
        this.result = result;
    }

    public void setError(Object[] error) {
        this.error = error;
    }
}
