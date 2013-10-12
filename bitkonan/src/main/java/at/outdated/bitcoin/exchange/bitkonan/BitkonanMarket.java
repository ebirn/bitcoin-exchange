package at.outdated.bitcoin.exchange.bitkonan;

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
 * Date: 26.05.13
 * Time: 23:41
 * To change this template use File | Settings | File Templates.
 */
public class BitkonanMarket extends Market {


    public BitkonanMarket() {
        super("bitkonan", "http://bitkonan.com", "BitKonan", Currency.USD);

        deposits.add(new TransferMethod(Currency.BTC, TransferType.VIRTUAL, null));
        deposits.add(new TransferMethod(Currency.USD, TransferType.BANK, null));

        withdrawals.add(new TransferMethod(Currency.BTC, TransferType.VIRTUAL, null));
        withdrawals.add(new TransferMethod(Currency.USD, TransferType.BANK, null));

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
        return new BitkonanApiClient(this);  //To change body of implemented methods use File | Settings | File Templates.
    }

}
