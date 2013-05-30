package at.outdated.bitcoin.exchange.mtgox;

import at.outdated.bitcoin.exchange.api.ExchangeApiClient;
import at.outdated.bitcoin.exchange.api.Market;
import at.outdated.bitcoin.exchange.api.currency.Currency;

/**
 * Created with IntelliJ IDEA.
 * User: ebirn
 * Date: 23.05.13
 * Time: 21:32
 * To change this template use File | Settings | File Templates.
 */
public class MtGoxMarket extends Market {

     public MtGoxMarket() {
        super("mtgox", "http://www.mtgox.com", "Mt.Gox", Currency.EUR);
    }

    @Override
    public Currency[] getFiatCurrencies() {
        return new Currency[] { Currency.EUR, Currency.JPY, Currency.USD };
    }

    public Currency[] getCryptoCurrencies() {
        return new Currency[] {Currency.BTC };
    }

    @Override
    public ExchangeApiClient getApiClient() {
        return new MtGoxClient();
    }
}
