package at.outdated.bitcoin.exchange.kraken.jaxb;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlTransient;

/**
 * Created by ebirn on 27.09.13.
 */

//@XmlRootElement
@XmlSeeAlso({KrakenTickerValue.class, KrakenDepthValue.class})
public abstract class KrakenResultValue {

}
