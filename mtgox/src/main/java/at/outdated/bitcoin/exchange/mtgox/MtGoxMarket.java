package at.outdated.bitcoin.exchange.mtgox;

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
        this.url = "http://www.mtgox.com";
        this.description = "Mt.Gox";
        this.primaryCurrency = Currency.EUR;
        this.key = "mtgox";

    }

    @Override
    public Currency[] getTradedCurrencies() {
        return new Currency[] { Currency.BTC, Currency.EUR, Currency.JPY, Currency.USD };
    }
}
