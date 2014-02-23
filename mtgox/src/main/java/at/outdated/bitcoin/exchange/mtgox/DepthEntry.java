package at.outdated.bitcoin.exchange.mtgox;

import at.outdated.bitcoin.exchange.api.jaxb.UnixTimeMicroDateAdapter;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by ebirn on 03.10.13.
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class DepthEntry {

    @XmlElement
    BigDecimal price;

    @XmlElement
    BigDecimal amount;

    @XmlElement
    int price_int;

    @XmlElement
    int amount_int;


    @XmlElement
    @XmlJavaTypeAdapter(UnixTimeMicroDateAdapter.class)
    Date stamp;


    public BigDecimal getPrice() {
        return price;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public int getPrice_int() {
        return price_int;
    }

    public int getAmount_int() {
        return amount_int;
    }

    public Date getStamp() {
        return stamp;
    }
}
