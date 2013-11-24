package at.outdated.bitcoin.exchange.kraken;

import at.outdated.bitcoin.exchange.api.client.ExchangeApiClient;
import at.outdated.bitcoin.exchange.api.market.Market;
import at.outdated.bitcoin.exchange.api.currency.Currency;
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
        super("kraken", "http://www.kraken.com", "Kraken");

        addWithdrawal(new TransferMethod(Currency.BTC, TransferType.VIRTUAL));
        addWithdrawal(new TransferMethod(Currency.LTC, TransferType.VIRTUAL));
        addWithdrawal(new TransferMethod(Currency.XRP, TransferType.VIRTUAL));

        addWithdrawal(new TransferMethod(Currency.EUR, TransferType.BANK));
        addWithdrawal(new TransferMethod(Currency.USD, TransferType.BANK));


        addDeposit(new TransferMethod(Currency.BTC, TransferType.VIRTUAL));
        addDeposit(new TransferMethod(Currency.LTC, TransferType.VIRTUAL));
        addDeposit(new TransferMethod(Currency.XRP, TransferType.VIRTUAL));

        addDeposit(new TransferMethod(Currency.EUR, TransferType.BANK));
        addDeposit(new TransferMethod(Currency.USD, TransferType.BANK));

        addAsset(Currency.BTC, Currency.EUR);
        addAsset(Currency.BTC, Currency.USD);
        addAsset(Currency.BTC, Currency.XRP);
        addAsset(Currency.USD, Currency.XRP);
        addAsset(Currency.EUR, Currency.XRP);
        addAsset(Currency.BTC, Currency.LTC);
        addAsset(Currency.LTC, Currency.USD);
        addAsset(Currency.LTC, Currency.EUR);
        addAsset(Currency.LTC, Currency.XRP);
    }

    @Override
    public ExchangeApiClient getApiClient() {
        return new KrakenClient(this);
    }

}
