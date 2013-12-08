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

//    @XmlElement
//    private long value_int = 0;

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
        //this.value_int = (long)(value * curr.getDivide());
        currency = curr;
    }

    /*
    //public CurrencyValue(long value, Currency curr) {
        value_int = value;
        //this.value = ((double)value / curr.getDivide());
        currency = curr;
    }
*/

    public CurrencyValue(CurrencyValue value) {
        this.currency = value.currency;
    //    this.value_int = value.value_int;
        this.value = value.value;
        this.display_short = value.display_short;
        this.display = value.display;
    }

    public double getValue() {
        return value;
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

    public CurrencyValue add(CurrencyValue other) {
        this.value += other.value;

        return this;
    }

    public CurrencyValue add(double other) {
        this.value += other;

        return this;
    }

    public CurrencyValue subtract(CurrencyValue other) {
        this.value -= other.value;

        return this;
    }

    public CurrencyValue multiply(long mul) {
        value *= (double) mul;

        return this;
    }

    public CurrencyValue multiply(BigDecimal mul) {
        value *= mul.doubleValue();

        return this;
    }

    public CurrencyValue multiply(double mul) {
        value *= mul;

        return this;
    }

    public CurrencyValue divide(double div) {
        value /= div;
        return this;
    }

    public BigDecimal asDecimal() {
        return new BigDecimal(value);
    }

    public boolean isMoreThan(CurrencyValue other) {
        return this.value > other.value;
    }

    public boolean isLessThan(CurrencyValue other) {
        return this.value < other.value;
    }

    public String toString() {
        NumberFormat nf = NumberFormat.getInstance();
        nf.setMaximumFractionDigits(2);
        nf.setMinimumFractionDigits(2);
        nf.setMinimumIntegerDigits(1);
        return nf.format(value) + " " + currency.name();
    }

    public boolean isPositive() {
        return value > 0.0;
    }

    public boolean isNonNegative() {
        return value >= 0.0;
    }

    public boolean isNegative() {
        return value < 0.0;
    }
}
