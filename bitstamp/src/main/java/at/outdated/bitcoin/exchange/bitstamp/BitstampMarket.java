package at.outdated.bitcoin.exchange.bitstamp;

import at.outdated.bitcoin.exchange.api.ExchangeApiClient;
import at.outdated.bitcoin.exchange.api.Market;
import at.outdated.bitcoin.exchange.api.currency.Currency;

/**
 * Created with IntelliJ IDEA.
 * User: ebirn
 * Date: 24.05.13
 * Time: 15:41
 * To change this template use File | Settings | File Templates.
 */
public class BitstampMarket extends Market {


    public BitstampMarket() {
        super("bitstamp", "https://www.bitstamp.net", "Bitstamp.net", Currency.USD);
    }

    @Override
    public Currency[] getFiatCurrencies() {
        return new Currency[]{ Currency.USD };
    }

    @Override
    public Currency[] getCryptoCurrencies() {
        return new Currency[]{ Currency.BTC };
    }

    @Override
    public ExchangeApiClient getApiClient() {
        return new BitstampClient(this);  //To change body of implemented methods use File | Settings | File Templates.
    }
}
