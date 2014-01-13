package at.outdated.bitcoin.exchange.bitstamp;

import at.outdated.bitcoin.exchange.api.jaxb.DateIso8601SpacedAdapter;
import at.outdated.bitcoin.exchange.api.jaxb.StringBigDecimalAdapter;

import javax.xml.bind.annotation.*;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by ebirn on 13.01.14.
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class BitstampOrder {

    // 0 == buy; 1 == sell;
    @XmlEnum(Integer.class)
    public enum OrderType {
        @XmlEnumValue("0") BUY,
        @XmlEnumValue("1") SELL
    }

    @XmlElement
    int id;

    //FIXME: is there a way to limit formatter to a total number of digits?
    // {"error": {"price": ["Ensure that there are no more than 7 digits in total."]}}
    @XmlJavaTypeAdapter(StringBigDecimalAdapter.class)
    BigDecimal price;

    @XmlJavaTypeAdapter(StringBigDecimalAdapter.class)
    BigDecimal amount;

    //FIXME this might be dangerous: bitstamp does not provide timezone offset=
    @XmlElement
    @XmlJavaTypeAdapter(DateIso8601SpacedAdapter.class)
    Date datetime;

    @XmlElement
    OrderType type;


    public int getId() {
        return id;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public OrderType getType() {
        return type;
    }

    public Date getDatetime() {
        return datetime;
    }
}
