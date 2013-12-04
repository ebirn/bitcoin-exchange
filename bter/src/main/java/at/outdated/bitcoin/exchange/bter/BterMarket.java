package at.outdated.bitcoin.exchange.bter;

import at.outdated.bitcoin.exchange.api.client.ExchangeApiClient;
import at.outdated.bitcoin.exchange.api.market.Market;
import at.outdated.bitcoin.exchange.api.currency.Currency;
import at.outdated.bitcoin.exchange.api.market.transfer.TransferMethod;
import at.outdated.bitcoin.exchange.api.market.transfer.TransferType;

/**
 * Created by ebirn on 11.10.13.
 */
public class BterMarket extends Market {

    public BterMarket() {
        super("bter", "http://bter.com", "Bter.com");

        addWithdrawal(new TransferMethod(Currency.BTC, TransferType.VIRTUAL));
        addWithdrawal(new TransferMethod(Currency.LTC, TransferType.VIRTUAL));
        addWithdrawal(new TransferMethod(Currency.PPC, TransferType.VIRTUAL));
        //addWithdrawal(new TransferMethod(Currency.NVC, TransferType.VIRTUAL, null));

        addDeposit(new TransferMethod(Currency.BTC, TransferType.VIRTUAL));
        addDeposit(new TransferMethod(Currency.LTC, TransferType.VIRTUAL));
        addDeposit(new TransferMethod(Currency.PPC, TransferType.VIRTUAL));
        //addDeposit(new TransferMethod(Currency.NVC, TransferType.VIRTUAL, null));

        addAsset(Currency.LTC, Currency.BTC);
        //addAsset(Currency.FTC, Currency.BTC);
        addAsset(Currency.PPC, Currency.BTC);
        //addAsset(Currency.NVC, Currency.BTC);
        addAsset(Currency.QRK, Currency.BTC);
    }

    @Override
    public ExchangeApiClient getApiClient() {
        return new BterApiClient(this);
    }


}
