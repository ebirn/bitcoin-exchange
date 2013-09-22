package at.outdated.bitcoin.exchange.bitkonan;

import at.outdated.bitcoin.exchange.api.ExchangeApiClient;
import at.outdated.bitcoin.exchange.api.Market;
import at.outdated.bitcoin.exchange.api.currency.Currency;

/**
 * Created with IntelliJ IDEA.
 * User: ebirn
 * Date: 26.05.13
 * Time: 23:41
 * To change this template use File | Settings | File Templates.
 */
public class BitkonanMarket extends Market {


    public BitkonanMarket() {
        super("bitkonan", "http://bitkonan.com", "BitKonan", Currency.USD);
    }

    @Override
    public Currency[] getFiatCurrencies() {
        return new Currency[] {Currency.USD};  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Currency[] getCryptoCurrencies() {
        return new Currency[] { Currency.BTC };  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public ExchangeApiClient getApiClient() {
        return new BitkonanApiClient();  //To change body of implemented methods use File | Settings | File Templates.
    }
}
