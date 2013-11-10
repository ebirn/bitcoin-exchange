package at.outdated.bitcoin.exchange.api.currency;

/**
 * Created by ebirn on 10.11.13.
 */
public class SimpleCurrencyAddress extends CurrencyAddress {


    @Override
    boolean validateAddress(String rawAddress) {
        return true;
    }
}
