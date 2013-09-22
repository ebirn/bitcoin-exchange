package at.outdated.bitcoin.exchange.kraken;

import javax.xml.bind.annotation.*;

/**
 * Created by ebirn on 22.09.13.
 */

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class KrakenResponse<T> {

    @XmlElement
    protected Object[] error;


    @XmlElementRefs({
        @XmlElementRef(type=KrakenResponseResult.class,name="result"),
    })
    protected KrakenResponseResult<T> result;


    public Object[] getError() {
        return error;
    }
}
