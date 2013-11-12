package at.outdated.bitcoin.exchange.api.currency;

/**
 * Created by ebirn on 10.11.13.
 */
public  class CurrencyAddress {

    Currency reference;

    String address;

    public CurrencyAddress(Currency c, String address) {
        this.reference = c;
        this.address = address;
    }

    public void setAddress(String rawAddress) {

//        if(validateAddress(rawAddress))
            this.address = rawAddress;
 //       else
 //           throw new IllegalArgumentException("Invalid address '" + rawAddress + "' for " + reference);
    }

    public Currency getReference() {
        return reference;
    }

    public String getAddress() {
        return address;
    }


    @Override
    public String toString() {
        return reference + ":" + address;
    }
}
