package at.outdated.bitcoin.exchange.bitstamp;

import at.outdated.bitcoin.exchange.api.jaxb.UnixTimeDateAdapter;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.util.Date;
import java.util.List;

/**
 * Created by ebirn on 30.09.13.
 */

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class BitstampMarketDepth {

    @XmlElement
    @XmlJavaTypeAdapter(UnixTimeDateAdapter.class)
    Date timestamp;

    @XmlElement
    //@XmlJavaTypeAdapter(NestedArrayAdapter.class)
    List<List<String>> bids;

    @XmlElement
    //@XmlJavaTypeAdapter(NestedArrayAdapter.class)
    List<List<String>> asks;

}
