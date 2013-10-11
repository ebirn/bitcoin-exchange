package at.outdated.bitcoin.exchange.vircurex;

import at.outdated.bitcoin.exchange.api.ExchangeApiClient;
import at.outdated.bitcoin.exchange.api.Market;
import at.outdated.bitcoin.exchange.api.currency.Currency;

/**
 * Created by ebirn on 11.10.13.
 */
public class VircurexMarket extends Market {

    public VircurexMarket() {
        super("vircurex", "http://vircurex.com", "Vircurex", Currency.USD);
    }

    @Override
    public Currency[] getFiatCurrencies() {
        return new Currency[]{ Currency.USD, Currency.EUR };
    }

    @Override
    public Currency[] getCryptoCurrencies() {
        return new Currency[]{ Currency.BTC };
    }

    @Override
    public ExchangeApiClient getApiClient() {
        return new VircurexApiClient(this);
    }
}
