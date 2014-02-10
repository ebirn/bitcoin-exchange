package at.outdated.bitcoin.exchange.bitstamp;

import at.outdated.bitcoin.exchange.api.jaxb.DateIso8601SpacedAdapter;
import at.outdated.bitcoin.exchange.api.jaxb.StringBigDecimalAdapter;
import at.outdated.bitcoin.exchange.api.jaxb.UnixTimeDateAdapter;
import at.outdated.bitcoin.exchange.api.market.OrderType;

import javax.xml.bind.annotation.*;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapters;
import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by ebirn on 13.01.14.
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class BitstampOrder {

    @XmlElements({
        @XmlElement(name="id"),
        @XmlElement(name="tid")
    })
    int id;

    //FIXME: is there a way to limit formatter to a total number of digits?
    // {"error": {"price": ["Ensure that there are no more than 7 digits in total."]}}
    @XmlJavaTypeAdapter(StringBigDecimalAdapter.class)
    BigDecimal price;

    @XmlJavaTypeAdapter(StringBigDecimalAdapter.class)
    BigDecimal amount;

    //time comes in UTC
    @XmlElement
    @XmlJavaTypeAdapter(DateIso8601SpacedAdapter.class)
    Date datetime;

    @XmlElement
    @XmlJavaTypeAdapter(UnixTimeDateAdapter.class)
    Date date;


    @XmlElement
    @XmlJavaTypeAdapter(BitstampOrderTypeAdapter.class)
    OrderType type = OrderType.UNDEF;


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

    public Date getDate() {
        return date;
    }
}
