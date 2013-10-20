package at.outdated.bitcoin.exchange.bitcurex.jaxb;

import at.outdated.bitcoin.exchange.api.jaxb.StringNumberAdapter;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

/**
 * Created by ebirn on 20.10.13.
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class Funds {

    @XmlElement
    @XmlJavaTypeAdapter(StringNumberAdapter.class)
    private Number eurs;

    @XmlElement
    @XmlJavaTypeAdapter(StringNumberAdapter.class)
    private Number btcs;

    @XmlElement
    private String address;

    public double getEurs() {
        return eurs.doubleValue();
    }

    public double getBtcs() {
        return btcs.doubleValue();
    }

    public String getAddress() {
        return address;
    }
}
