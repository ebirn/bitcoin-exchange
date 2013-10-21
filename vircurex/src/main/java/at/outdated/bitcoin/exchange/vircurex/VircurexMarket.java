package at.outdated.bitcoin.exchange.vircurex;

import at.outdated.bitcoin.exchange.api.ExchangeApiClient;
import at.outdated.bitcoin.exchange.api.Market;
import at.outdated.bitcoin.exchange.api.currency.Currency;
import at.outdated.bitcoin.exchange.api.market.AssetPair;
import at.outdated.bitcoin.exchange.api.market.transfer.TransferMethod;
import at.outdated.bitcoin.exchange.api.market.transfer.TransferType;

/**
 * Created by ebirn on 11.10.13.
 */
public class VircurexMarket extends Market {

    public VircurexMarket() {
        super("vircurex", "http://vircurex.com", "Vircurex", Currency.USD);

        withdrawals.put(Currency.BTC, new TransferMethod(Currency.BTC, TransferType.VIRTUAL, null));
        withdrawals.put(Currency.LTC, new TransferMethod(Currency.LTC, TransferType.VIRTUAL, null));
        withdrawals.put(Currency.NMC, new TransferMethod(Currency.NMC, TransferType.VIRTUAL, null));
        withdrawals.put(Currency.NVC, new TransferMethod(Currency.NVC, TransferType.VIRTUAL, null));

        deposits.put(Currency.BTC, new TransferMethod(Currency.BTC, TransferType.VIRTUAL, null));

        deposits.put(Currency.EUR, new TransferMethod(Currency.EUR, TransferType.BANK, null));
        deposits.put(Currency.USD, new TransferMethod(Currency.USD, TransferType.BANK, null));

    }


    @Override
    public AssetPair[] getTradedAssets() {
        return new AssetPair[]{
            new AssetPair(Currency.BTC, Currency.LTC),
            new AssetPair(Currency.BTC, Currency.LTC),
            new AssetPair(Currency.BTC, Currency.NMC),
            new AssetPair(Currency.BTC, Currency.NVC),
            new AssetPair(Currency.LTC, Currency.NMC),
            new AssetPair(Currency.LTC, Currency.NVC),
            new AssetPair(Currency.NVC, Currency.NMC)
        };
    }

    @Override
    public ExchangeApiClient getApiClient() {
        return new VircurexApiClient(this);
    }


}
