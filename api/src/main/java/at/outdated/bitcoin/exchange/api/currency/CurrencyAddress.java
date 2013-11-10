package at.outdated.bitcoin.exchange.api.currency;

/**
 * Created by ebirn on 10.11.13.
 */
public abstract class CurrencyAddress {

    Currency reference;

    String address;

    public void setAddress(String rawAddress) throws IllegalArgumentException {

        if(validateAddress(rawAddress))
            this.address = rawAddress;
        else
            throw new IllegalArgumentException("Invalid address '" + rawAddress + "' for " + reference);
    }

    public Currency getReference() {
        return reference;
    }

    public String getAddress() {
        return address;
    }

    abstract boolean validateAddress(String rawAddress);

    @Override
    public String toString() {
        return reference + ":" + address;
    }
}
