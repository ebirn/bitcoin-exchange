package at.outdated.bitcoin.exchange.api.currency;

import org.apache.commons.lang3.builder.CompareToBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.util.Locale;

/**
 * Created with IntelliJ IDEA.
 * User: ebirn
 * Date: 02.05.13
 * Time: 20:15
 * To change this template use File | Settings | File Templates.
 */
public class CurrencyValue implements Cloneable, Comparable<CurrencyValue> {

    public static final MathContext CURRENCY_MATH_CONTEXT = new MathContext(7, RoundingMode.HALF_UP);

    private BigDecimal value;
    private Currency currency;

    @Deprecated
    public CurrencyValue() {
    }

    @Deprecated
    public CurrencyValue(double value, Currency curr) {
        this.value = new BigDecimal(value, CURRENCY_MATH_CONTEXT);
        this.currency = curr;
    }

    public CurrencyValue(long value, Currency curr) {
        this.value = new BigDecimal(value, CURRENCY_MATH_CONTEXT);
        this.currency = curr;
    }


    public CurrencyValue(BigDecimal value, Currency curr) {
        value.setScale(CURRENCY_MATH_CONTEXT.getPrecision(), CURRENCY_MATH_CONTEXT.getRoundingMode());
        this.value = value;
        this.currency = curr;
    }

    public CurrencyValue(String value, Currency curr) {
        this.value = new BigDecimal(value, CURRENCY_MATH_CONTEXT);
        this.currency = curr;
    }

    public CurrencyValue(Currency curr) {
        this.value = new BigDecimal(0L, CURRENCY_MATH_CONTEXT);
        this.currency = curr;
    }

    public CurrencyValue(CurrencyValue value) {
        this.currency = value.currency;
        this.value = value.value;
    }

    public double doubleValue() {
        return value.doubleValue();
    }

    public CurrencyValue abs() {
        return new CurrencyValue(value.abs(), currency);
    }

    public Currency getCurrency() {
        return currency;
    }

    public CurrencyValue add(CurrencyValue other) {
        checkArgument(other);
        this.value = this.value.add(other.value, CURRENCY_MATH_CONTEXT);

        return this;
    }

    public CurrencyValue add(BigDecimal other) {
        this.value = this.value.add(other, CURRENCY_MATH_CONTEXT);

        return this;
    }

    public CurrencyValue subtract(CurrencyValue other) {

        checkArgument(other);
        this.value = this.value.subtract(other.value, CURRENCY_MATH_CONTEXT);
        return this;
    }

    public CurrencyValue subtract(BigDecimal other) {
        this.value = this.value.subtract(other, CURRENCY_MATH_CONTEXT);
        return this;
    }

    public CurrencyValue multiply(BigDecimal mul) {
        value = this.value.multiply(mul, CURRENCY_MATH_CONTEXT);

        return this;
    }

    public CurrencyValue multiply(CurrencyValue other) {
        checkArgument(other);
        this.value = this.value.multiply(other.value, CURRENCY_MATH_CONTEXT);

        return this;
    }

    public CurrencyValue divide(CurrencyValue other) {
        checkArgument(other);
        this.value = this.value.divide(other.value, CURRENCY_MATH_CONTEXT);
        return this;
    }
    public CurrencyValue divide(BigDecimal div) {
        this.value = this.value.divide(div, CURRENCY_MATH_CONTEXT);
        return this;
    }

    private void checkArgument(CurrencyValue other) {
        if(this.currency != other.currency) {
            throw new IllegalArgumentException("currency mismatch");
        }
    }

    public boolean isMoreThan(CurrencyValue other) {
        return value.compareTo(other.value) > 0;
    }

    public boolean isLessThan(CurrencyValue other) {
        return value.compareTo(other.value) < 0;
    }

    public String toString() {
        NumberFormat fmt = NumberFormat.getNumberInstance(Locale.US);
        fmt.setMinimumIntegerDigits(1);
        fmt.setMinimumFractionDigits(2);

        return fmt.format(value) + " " + currency;
    }

    public boolean isPositive() {
        return value.signum() > 0;
    }

    public boolean isNonNegative() {
        return value.signum() >= 0;
    }

    public boolean isNegative() {
        return value.signum() < 0;
    }

    public void setValue(BigDecimal value) {
        this.value = value;
    }

    // TODO: review whether this is a good idea
    public void setCurrency(Currency currency) {
        this.currency = currency;
    }

    public BigDecimal getValue() {
        return value;
    }

    public String valueToString() {
        return value.toPlainString();
    }

    @Override
    public int hashCode() {

        HashCodeBuilder builder = new HashCodeBuilder();

        builder.append(currency);
        builder.append(value);

        return builder.toHashCode();
    }

    @Override
    public boolean equals(Object obj) {

        boolean isEquals = false;

        if(this == obj) {
            isEquals = true;
        }
        else if(obj.getClass() != getClass()) {
            isEquals = false;
        }
        else {
            CurrencyValue other = (CurrencyValue) obj;
            isEquals = (this.currency == other.currency) && (value.compareTo(other.value) == 0);
        }
        return isEquals;

    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return copy();
    }

    public CurrencyValue copy() {
        return new CurrencyValue(value, currency);
    }


    @Override
    public int compareTo(CurrencyValue o) {

        CompareToBuilder builder = new CompareToBuilder();

        builder.append(currency, o.currency);
        builder.append(value, o.value);

        return builder.toComparison();
    }
}
