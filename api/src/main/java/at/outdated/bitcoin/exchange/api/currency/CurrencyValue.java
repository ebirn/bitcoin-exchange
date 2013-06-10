package at.outdated.bitcoin.exchange.api.currency;

import org.apache.commons.lang3.StringEscapeUtils;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.math.BigDecimal;
import java.text.NumberFormat;

/**
 * Created with IntelliJ IDEA.
 * User: ebirn
 * Date: 02.05.13
 * Time: 20:15
 * To change this template use File | Settings | File Templates.
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class CurrencyValue {

    @XmlElement
    private double value = 0.0;

    @XmlElement
    private long value_int = 0;

    @XmlElement
    private String display = "";

    @XmlElement
    private String display_short = "";

    @XmlElement
    private Currency currency = Currency.BTC;

    public CurrencyValue() {

    }

    public CurrencyValue(double value, Currency curr) {
        this.value = value;
        this.value_int = (long)(value / curr.getDivide());
        currency = curr;
    }

    public CurrencyValue(long value, Currency curr) {
        value_int = value;
        this.value = ((double)value / curr.getDivide());
        currency = curr;
    }

    public CurrencyValue(CurrencyValue value) {
        this.value_int = value.value_int;
        this.value = value.value;
        this.display_short = value.display_short;
        this.display = value.display;
    }

    public double getValue() {
        return value;
    }

    public long getIntValue() {
        return value_int;
    }

    public String getDisplay() {
        return StringEscapeUtils.unescapeJava(display);
    }

    public String getDisplayShort() {
        return StringEscapeUtils.unescapeJava(display_short);
    }

    public Currency getCurrency() {
        return currency;
    }

    public void add(CurrencyValue other) {
        this.value += other.value;
        this.value_int += other.value_int;
    }

    public void subtract(CurrencyValue other) {
        this.value -= other.value;
        this.value_int -= other.value_int;
    }

    public void multiply(long mul) {
        value_int *= mul;
        value *= (double) mul;
    }

    public void multiply(BigDecimal mul) {
        value_int *= mul.longValue();
        value *= mul.doubleValue();
    }




    public void divide(long div) {
        value_int /= div;
        value /= (double) div;
    }

    public BigDecimal asDecimal() {
        BigDecimal number = new BigDecimal(value_int);

        number = number.divide(new BigDecimal(currency.getDivide()));

        return number;
    }

    public String toString() {
        NumberFormat nf = NumberFormat.getInstance();
        nf.setMaximumFractionDigits(2);
        nf.setMinimumFractionDigits(2);
        nf.setMinimumIntegerDigits(1);
        return nf.format(value) + " " + currency.name();
    }
}
