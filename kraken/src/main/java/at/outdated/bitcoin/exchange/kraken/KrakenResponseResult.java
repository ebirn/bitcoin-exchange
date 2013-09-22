package at.outdated.bitcoin.exchange.kraken;

import javax.xml.bind.annotation.*;

/**
 * Created with IntelliJ IDEA.
 * User: ebirn
 * Date: 17.09.13
 * Time: 18:36
 * To change this template use File | Settings | File Templates.
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "result")
@XmlSeeAlso({KrakenTickerValue.class, KrakenDepthValue.class})

public class KrakenResponseResult<T> {

    @XmlElementRefs({
            @XmlElementRef(type=KrakenTickerValue.class),
            @XmlElementRef(type=KrakenDepthValue.class)
    })
    private T XXBTZEUR;


    public T getXXBTZEUR() {
        return XXBTZEUR;
    }
}
