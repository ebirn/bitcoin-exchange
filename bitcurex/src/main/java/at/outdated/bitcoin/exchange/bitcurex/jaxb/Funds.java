package at.outdated.bitcoin.exchange.bitcurex.jaxb;

import at.outdated.bitcoin.exchange.api.jaxb.StringBigDecimalAdapter;
import at.outdated.bitcoin.exchange.api.jaxb.StringNumberAdapter;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.math.BigDecimal;

/**
 * Created by ebirn on 20.10.13.
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class Funds extends BaseApiResponse {

    @XmlElement
    @XmlJavaTypeAdapter(StringBigDecimalAdapter.class)
    private BigDecimal eurs;

    @XmlElement
    @XmlJavaTypeAdapter(StringBigDecimalAdapter.class)
    private BigDecimal btcs;

    @XmlElement
    private String address;

    public BigDecimal getEurs() {
        return eurs;
    }

    public BigDecimal getBtcs() {
        return btcs;
    }

    public String getAddress() {
        return address;
    }


}
