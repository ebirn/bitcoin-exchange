package at.outdated.bitcoin.exchange.api.market;

import at.outdated.bitcoin.exchange.api.currency.Currency;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

/**
 * Created by ebirn on 20.10.13.
 */
public class AssetPair {

    Currency base;

    Currency quote;

    public AssetPair(Currency base, Currency quote) {
        this.base = base;
        this.quote = quote;
    }

    public Currency getBase() {
        return base;
    }

    public Currency getQuote() {
        return quote;
    }

    public Currency getOther(Currency one) {
        if(one == base) return quote;
        if(one == quote) return base;

        throw new IllegalArgumentException(one + " is not part of pair");
    }

    public boolean isCrypto() {
        return (base.isCrypto() && quote.isCrypto());
    }

    public boolean isMember(Currency one) {
        if(base == one || quote == one) return true;

        return false;
    }

    @Override
    public String toString() {
        return base + ":" + quote;
    }

    @Override
    public boolean equals(Object obj) {

        if(obj.getClass() != getClass()) return false;

        EqualsBuilder builder = new EqualsBuilder();

        AssetPair other = (AssetPair) obj;

        builder.append(this.base, other.base);
        builder.append(this.quote, other.quote);

        return builder.isEquals();
    }

    @Override
    public int hashCode() {

        HashCodeBuilder builder = new HashCodeBuilder();

        builder.append(base);
        builder.append(quote);

        return builder.toHashCode();
    }
}
