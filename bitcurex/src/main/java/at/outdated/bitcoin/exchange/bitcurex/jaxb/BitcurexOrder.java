package at.outdated.bitcoin.exchange.bitcurex.jaxb;

import at.outdated.bitcoin.exchange.api.jaxb.StringBigDecimalAdapter;
import at.outdated.bitcoin.exchange.api.jaxb.StringNumberAdapter;
import at.outdated.bitcoin.exchange.api.jaxb.UnixTimeDateAdapter;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by ebirn on 20.10.13.
 */

public class BitcurexOrder {

    @XmlElements({
        @XmlElement(name="oid"),
        @XmlElement(name="tid")
    })
    String oid;

    @XmlElement
    BitcurexOrderType type;

    @XmlElement
    @XmlJavaTypeAdapter(StringBigDecimalAdapter.class)
    BigDecimal amount;

    @XmlElement
    @XmlJavaTypeAdapter(StringBigDecimalAdapter.class)
    BigDecimal price;

    @XmlElements({
        @XmlElement(name="timestamp"),
        @XmlElement(name="date")
    })
    @XmlJavaTypeAdapter(UnixTimeDateAdapter.class)
    Date timestamp;

    public String getOid() {
        return oid;
    }

    public BitcurexOrderType getType() {
        return type;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public Date getTimestamp() {
        return timestamp;
    }
}
