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
public class Orders extends BaseApiResponse  {

    @XmlElement
    @XmlJavaTypeAdapter(StringNumberAdapter.class)
    Number eurs;


    @XmlElement
    @XmlJavaTypeAdapter(StringNumberAdapter.class)
    Number plns;

    @XmlElement
    @XmlJavaTypeAdapter(StringNumberAdapter.class)
    Number btcs;


    @XmlElement
    List<BitcurexOrder> orders;


    public Number getEurs() {
        return eurs;
    }

    public Number getBtcs() {
        return btcs;
    }

    public List<BitcurexOrder> getOrders() {
        return orders;
    }

    public Number getPlns() {
        return plns;
    }
}
