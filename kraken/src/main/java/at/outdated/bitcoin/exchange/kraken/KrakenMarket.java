package at.outdated.bitcoin.exchange.kraken;

import at.outdated.bitcoin.exchange.api.ExchangeApiClient;
import at.outdated.bitcoin.exchange.api.Market;
import at.outdated.bitcoin.exchange.api.currency.Currency;
import at.outdated.bitcoin.exchange.api.market.AssetPair;
import at.outdated.bitcoin.exchange.api.market.transfer.TransferMethod;
import at.outdated.bitcoin.exchange.api.market.transfer.TransferType;

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

        addWithdrawal(new TransferMethod(Currency.BTC, TransferType.VIRTUAL, null));
        addWithdrawal(new TransferMethod(Currency.LTC, TransferType.VIRTUAL, null));
        addWithdrawal(new TransferMethod(Currency.XRP, TransferType.VIRTUAL, null));

        addWithdrawal(new TransferMethod(Currency.EUR, TransferType.BANK, null));
        addWithdrawal(new TransferMethod(Currency.USD, TransferType.BANK, null));


        addDeposit(new TransferMethod(Currency.BTC, TransferType.VIRTUAL, null));
        addDeposit(new TransferMethod(Currency.LTC, TransferType.VIRTUAL, null));
        addDeposit(new TransferMethod(Currency.XRP, TransferType.VIRTUAL, null));

        addDeposit(new TransferMethod(Currency.EUR, TransferType.BANK, null));
        addDeposit(new TransferMethod(Currency.USD, TransferType.BANK, null));
    }


    @Override
    public AssetPair[] getTradedAssets() {
        return new AssetPair[] {
            new AssetPair(Currency.BTC, Currency.EUR),
            new AssetPair(Currency.BTC, Currency.USD),
            new AssetPair(Currency.BTC, Currency.XRP),
            new AssetPair(Currency.USD, Currency.XRP),
            new AssetPair(Currency.EUR, Currency.XRP),
            new AssetPair(Currency.BTC, Currency.LTC),
            new AssetPair(Currency.LTC, Currency.USD),
            new AssetPair(Currency.LTC, Currency.EUR),
            new AssetPair(Currency.LTC, Currency.XRP),
        };  //
    }

    @Override
    public ExchangeApiClient getApiClient() {
        return new KrakenClient(this);
    }

}
