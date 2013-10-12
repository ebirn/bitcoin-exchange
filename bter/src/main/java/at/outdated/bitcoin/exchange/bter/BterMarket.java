package at.outdated.bitcoin.exchange.bter;

import at.outdated.bitcoin.exchange.api.ExchangeApiClient;
import at.outdated.bitcoin.exchange.api.Market;
import at.outdated.bitcoin.exchange.api.currency.Currency;
import at.outdated.bitcoin.exchange.api.market.transfer.TransferMethod;
import at.outdated.bitcoin.exchange.api.market.transfer.TransferType;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ebirn on 11.10.13.
 */
public class BterMarket extends Market {

    public BterMarket() {
        super("bter", "http://bter.com", "Bter.com", Currency.CNY);

        withdrawals.add(new TransferMethod(Currency.BTC, TransferType.VIRTUAL, null));
        withdrawals.add(new TransferMethod(Currency.LTC, TransferType.VIRTUAL, null));
        withdrawals.add(new TransferMethod(Currency.NMC, TransferType.VIRTUAL, null));
        withdrawals.add(new TransferMethod(Currency.NVC, TransferType.VIRTUAL, null));

        deposits.add(new TransferMethod(Currency.BTC, TransferType.VIRTUAL, null));
        deposits.add(new TransferMethod(Currency.LTC, TransferType.VIRTUAL, null));
        deposits.add(new TransferMethod(Currency.NMC, TransferType.VIRTUAL, null));
        deposits.add(new TransferMethod(Currency.NVC, TransferType.VIRTUAL, null));
    }

    @Override
    public Currency[] getFiatCurrencies() {
        return new Currency[]{ };
    }

    @Override
    public Currency[] getCryptoCurrencies() {
        return new Currency[]{ Currency.BTC, Currency.LTC, Currency.NMC, Currency.NVC };
    }

    @Override
    public ExchangeApiClient getApiClient() {
        return new BterApiClient(this);
    }


}
