package at.outdated.bitcoin.exchange.mtgox;

import at.outdated.bitcoin.exchange.api.currency.Currency;
import at.outdated.bitcoin.exchange.api.currency.CurrencyValue;
import org.apache.commons.lang3.StringEscapeUtils;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

/**
 * Created with IntelliJ IDEA.
 * User: ebirn
 * Date: 02.05.13
 * Time: 20:15
 * To change this template use File | Settings | File Templates.
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class MtGoxCurrencyValue {

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

    public MtGoxCurrencyValue() {

    }

    public MtGoxCurrencyValue(double value, Currency curr) {
        this.value = value;
        //this.value_int = (long)(value * curr.getDivide());
        currency = curr;
    }

    public MtGoxCurrencyValue(MtGoxCurrencyValue value) {
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

    public MtGoxCurrencyValue add(MtGoxCurrencyValue other) {
        this.value += other.value;

        return this;
    }

    public MtGoxCurrencyValue add(double other) {
        this.value += other;

        return this;
    }

    public MtGoxCurrencyValue subtract(MtGoxCurrencyValue other) {
        this.value -= other.value;
        return this;
    }

    public MtGoxCurrencyValue subtract(double other) {
        this.value -= other;
        return this;
    }

    public MtGoxCurrencyValue multiply(long mul) {
        value *= (double) mul;

        return this;
    }

    public MtGoxCurrencyValue multiply(BigDecimal mul) {
        value *= mul.doubleValue();

        return this;
    }

    public MtGoxCurrencyValue multiply(double mul) {
        value *= mul;

        return this;
    }

    public MtGoxCurrencyValue divide(double div) {
        value /= div;
        return this;
    }

    public BigDecimal asDecimal() {
        return new BigDecimal(value);
    }

    public boolean isMoreThan(MtGoxCurrencyValue other) {
        return this.value > other.value;
    }

    public boolean isLessThan(MtGoxCurrencyValue other) {
        return this.value < other.value;
    }

    public String toString() {
        return valueToString() + " " + currency.name();
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

    public void setValue(double value) {
        this.value = value;
    }

    public String valueToString() {
/*
        NumberFormat fmt = NumberFormat.getNumberInstance(Locale.US);
        fmt.setGroupingUsed(false);
        fmt.setMinimumIntegerDigits(1);

        fmt.setMinimumFractionDigits(4);
        fmt.setMaximumFractionDigits(7);
*/
        BigDecimal number = new BigDecimal(value, new MathContext(7, RoundingMode.HALF_UP));

        return number.toPlainString();
    }


    CurrencyValue convert() {
        return new CurrencyValue(value, currency);
    }
}
