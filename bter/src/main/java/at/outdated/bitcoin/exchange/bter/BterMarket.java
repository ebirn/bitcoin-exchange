package at.outdated.bitcoin.exchange.bter;

import at.outdated.bitcoin.exchange.api.ExchangeApiClient;
import at.outdated.bitcoin.exchange.api.Market;
import at.outdated.bitcoin.exchange.api.currency.Currency;
import at.outdated.bitcoin.exchange.api.market.AssetPair;
import at.outdated.bitcoin.exchange.api.market.transfer.TransferMethod;
import at.outdated.bitcoin.exchange.api.market.transfer.TransferType;

/**
 * Created by ebirn on 11.10.13.
 */
public class BterMarket extends Market {

    public BterMarket() {
        super("bter", "http://bter.com", "Bter.com", Currency.CNY);

        addWithdrawal(new TransferMethod(Currency.BTC, TransferType.VIRTUAL, null));
        addWithdrawal(new TransferMethod(Currency.LTC, TransferType.VIRTUAL, null));
        addWithdrawal(new TransferMethod(Currency.NMC, TransferType.VIRTUAL, null));
        addWithdrawal(new TransferMethod(Currency.NVC, TransferType.VIRTUAL, null));

        addDeposit(new TransferMethod(Currency.BTC, TransferType.VIRTUAL, null));
        addDeposit(new TransferMethod(Currency.LTC, TransferType.VIRTUAL, null));
        addDeposit(new TransferMethod(Currency.NMC, TransferType.VIRTUAL, null));
        addDeposit(new TransferMethod(Currency.NVC, TransferType.VIRTUAL, null));
    }


    @Override
    public AssetPair[] getTradedAssets() {
        return new AssetPair[]{
            new AssetPair(Currency.LTC, Currency.BTC),
            new AssetPair(Currency.FTC, Currency.BTC),
            new AssetPair(Currency.NMC, Currency.BTC),
            new AssetPair(Currency.NVC, Currency.BTC),
        };
    }

    @Override
    public ExchangeApiClient getApiClient() {
        return new BterApiClient(this);
    }


}
