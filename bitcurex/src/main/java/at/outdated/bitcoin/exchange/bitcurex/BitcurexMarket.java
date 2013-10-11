package at.outdated.bitcoin.exchange.bitcurex;

import at.outdated.bitcoin.exchange.api.ExchangeApiClient;
import at.outdated.bitcoin.exchange.api.Market;
import at.outdated.bitcoin.exchange.api.currency.Currency;

/**
 * Created with IntelliJ IDEA.
 * User: ebirn
 * Date: 30.05.13
 * Time: 12:50
 * To change this template use File | Settings | File Templates.
 */
public class BitcurexMarket extends Market {


    public BitcurexMarket() {
        super("bitcurex", "https://eur.bitcurex.com/", "Bitcurex", Currency.EUR);
    }

    @Override
    public Currency[] getFiatCurrencies() {
        return new Currency[] { Currency.EUR, Currency.PLN };  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Currency[] getCryptoCurrencies() {
        return new Currency[]{ Currency.BTC};  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public ExchangeApiClient getApiClient() {
        return new BitcurexApiClient(this);  //To change body of implemented methods use File | Settings | File Templates.
    }
}
