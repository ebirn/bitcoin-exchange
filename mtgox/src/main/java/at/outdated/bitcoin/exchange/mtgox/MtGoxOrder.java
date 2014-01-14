package at.outdated.bitcoin.exchange.mtgox;

import at.outdated.bitcoin.exchange.api.OrderId;
import at.outdated.bitcoin.exchange.api.currency.Currency;
import at.outdated.bitcoin.exchange.api.currency.CurrencyValue;
import at.outdated.bitcoin.exchange.api.jaxb.UnixTimeDateAdapter;
import at.outdated.bitcoin.exchange.api.jaxb.UnixTimeMicroDateAdapter;
import at.outdated.bitcoin.exchange.api.market.MarketOrder;
import at.outdated.bitcoin.exchange.api.market.TradeDecision;

import javax.xml.bind.annotation.*;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.util.Date;

/**
 * Created by ebirn on 14.01.14.
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class MtGoxOrder {

    public enum Type {
        @XmlEnumValue("bid") BUY,
        @XmlEnumValue("ask") SELL
    }

    public enum Status {
        //  pending, executing, post-pending, open, stop, invalid,
        @XmlEnumValue("pending") PENDING,
        @XmlEnumValue("executing") EXECUTING,
        @XmlEnumValue("post-pending") POSTPENDING,
        @XmlEnumValue("open") OPEN,
        @XmlEnumValue("stop") STOP,
        @XmlEnumValue("invalid") INVALID
    }

    @XmlElement
    String oid;


    @XmlElement(name = "item")
    Currency base;


    @XmlElement(name = "currency")
    Currency quote;

    @XmlElement(name="date")
    @XmlJavaTypeAdapter(UnixTimeDateAdapter.class)
    Date timestamp;

    @XmlElement
    @XmlJavaTypeAdapter(UnixTimeMicroDateAdapter.class)
    Date priority;

    @XmlElement
    Type type;

    @XmlElement
    CurrencyValue amount;

    @XmlElement
    CurrencyValue price;

    @XmlElement(name="effective_amount")
    CurrencyValue effectiveAmount;

    @XmlElement
    Status status;


    public String getOid() {
        return oid;
    }

    public Currency getBase() {
        return base;
    }

    public Currency getQuote() {
        return quote;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public Date getPriority() {
        return priority;
    }

    public Type getType() {
        return type;
    }

    public CurrencyValue getAmount() {
        return amount;
    }

    public CurrencyValue getPrice() {
        return price;
    }

    public CurrencyValue getEffectiveAmount() {
        return effectiveAmount;
    }

    public Status getStatus() {
        return status;
    }

}
