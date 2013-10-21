package at.outdated.bitcoin.exchange.btce;

import at.outdated.bitcoin.exchange.api.ExchangeApiClient;
import at.outdated.bitcoin.exchange.api.Market;
import at.outdated.bitcoin.exchange.api.currency.Currency;
import at.outdated.bitcoin.exchange.api.market.AssetPair;
import at.outdated.bitcoin.exchange.api.market.transfer.TransferMethod;
import at.outdated.bitcoin.exchange.api.market.transfer.TransferType;

/**
 * Created with IntelliJ IDEA.
 * User: ebirn
 * Date: 26.05.13
 * Time: 23:41
 * To change this template use File | Settings | File Templates.
 */
public class BtcEMarket extends Market {


    public BtcEMarket() {
        super("btce", "http://btc-e.com", "BTC-E", Currency.EUR);

        addWithdrawal(new TransferMethod(Currency.BTC, TransferType.VIRTUAL, null));
        addWithdrawal(new TransferMethod(Currency.LTC, TransferType.VIRTUAL, null));
        addWithdrawal(new TransferMethod(Currency.NMC, TransferType.VIRTUAL, null));

        addDeposit(new TransferMethod(Currency.BTC, TransferType.VIRTUAL, null));
        addDeposit(new TransferMethod(Currency.LTC, TransferType.VIRTUAL, null));
        addDeposit(new TransferMethod(Currency.NMC, TransferType.VIRTUAL, null));

    }

    @Override
    public AssetPair[] getTradedAssets() {

        return new AssetPair[] {
            new AssetPair(Currency.BTC, Currency.USD),
            new AssetPair(Currency.BTC, Currency.EUR),
            new AssetPair(Currency.LTC, Currency.BTC),
            new AssetPair(Currency.LTC, Currency.USD),
            new AssetPair(Currency.LTC, Currency.EUR),
            new AssetPair(Currency.NMC, Currency.BTC),
        };
    }

    @Override
    public ExchangeApiClient getApiClient() {
        return new BtcEApiClient(this);  //To change body of implemented methods use File | Settings | File Templates.
    }


}
