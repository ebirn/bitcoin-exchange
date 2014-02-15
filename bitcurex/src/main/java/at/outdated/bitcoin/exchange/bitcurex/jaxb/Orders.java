package at.outdated.bitcoin.exchange.bitcurex.jaxb;

import at.outdated.bitcoin.exchange.api.jaxb.StringBigDecimalAdapter;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.math.BigDecimal;
import java.util.List;

/**
 * Created by ebirn on 20.10.13.
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class Orders extends BaseApiResponse  {

    @XmlElement
    @XmlJavaTypeAdapter(StringBigDecimalAdapter.class)
    BigDecimal eurs;


    @XmlElement
    @XmlJavaTypeAdapter(StringBigDecimalAdapter.class)
    BigDecimal plns;

    @XmlElement
    @XmlJavaTypeAdapter(StringBigDecimalAdapter.class)
    BigDecimal btcs;


    @XmlElement
    List<BitcurexOrder> orders;


    public BigDecimal getEurs() {
        return eurs;
    }

    public BigDecimal getBtcs() {
        return btcs;
    }

    public List<BitcurexOrder> getOrders() {
        return orders;
    }

    public BigDecimal getPlns() {
        return plns;
    }
}
