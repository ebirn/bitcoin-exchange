package at.outdated.bitcoin.exchange.icbit;

import at.outdated.bitcoin.exchange.api.ExchangeApiClient;
import at.outdated.bitcoin.exchange.api.Market;
import at.outdated.bitcoin.exchange.api.currency.Currency;

/**
 * Created by ebirn on 31.10.13.
 */
public class IcbitMarket  extends Market {

    public IcbitMarket() {
        super("icbit", "https://icbit.se", "icbit.se", Currency.BTC);
    }

    @Override
    public ExchangeApiClient getApiClient() {
        return new IcbitClient(this);
    }
}
