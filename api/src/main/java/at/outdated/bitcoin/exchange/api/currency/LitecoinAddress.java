package at.outdated.bitcoin.exchange.api.currency;

/**
 * Created by ebirn on 10.11.13.
 */
public class LitecoinAddress extends CurrencyAddress {
    @Override
    boolean validateAddress(String rawAddress) {
        return rawAddress.startsWith("L") && rawAddress.length() == 35;
    }
}
