package at.outdated.bitcoin.exchange.kraken;

import at.outdated.bitcoin.exchange.api.ExchangeApiClient;
import at.outdated.bitcoin.exchange.api.Market;
import at.outdated.bitcoin.exchange.api.currency.Currency;
import at.outdated.bitcoin.exchange.api.market.transfer.TransferMethod;
import at.outdated.bitcoin.exchange.api.market.transfer.TransferType;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: ebirn
 * Date: 17.09.13
 * Time: 18:28
 * To change this template use File | Settings | File Templates.
 */
public class KrakenMarket extends Market {

    public KrakenMarket() {
        super("kraken", "http://www.kraken.com", "Kraken", Currency.EUR);

        withdrawals.add(new TransferMethod(Currency.BTC, TransferType.VIRTUAL, null));
        withdrawals.add(new TransferMethod(Currency.LTC, TransferType.VIRTUAL, null));
        withdrawals.add(new TransferMethod(Currency.XRP, TransferType.VIRTUAL, null));

        withdrawals.add(new TransferMethod(Currency.EUR, TransferType.BANK, null));
        withdrawals.add(new TransferMethod(Currency.USD, TransferType.BANK, null));


        deposits.add(new TransferMethod(Currency.BTC, TransferType.VIRTUAL, null));
        deposits.add(new TransferMethod(Currency.LTC, TransferType.VIRTUAL, null));
        deposits.add(new TransferMethod(Currency.XRP, TransferType.VIRTUAL, null));

        deposits.add(new TransferMethod(Currency.EUR, TransferType.BANK, null));
        deposits.add(new TransferMethod(Currency.USD, TransferType.BANK, null));
    }

    @Override
    public Currency[] getFiatCurrencies() {
        return new Currency[] { Currency.EUR, Currency.USD };  //
    }

    @Override
    public Currency[] getCryptoCurrencies() {
        return new Currency[] { Currency.BTC };  //
    }

    @Override
    public ExchangeApiClient getApiClient() {
        return new KrakenClient(this);
    }

}
