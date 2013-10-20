package at.outdated.bitcoin.exchange.bitcurex.jaxb;

import at.outdated.bitcoin.exchange.api.jaxb.StringNumberAdapter;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.util.List;

/**
 * Created by ebirn on 20.10.13.
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class Orders {

    @XmlElement
    @XmlJavaTypeAdapter(StringNumberAdapter.class)
    protected Number eurs;

    @XmlElement
    @XmlJavaTypeAdapter(StringNumberAdapter.class)
    protected Number btcs;


    @XmlElement
    protected List<BitcurexOrder> orders;


}
