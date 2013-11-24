package at.outdated.bitcoin.exchange.mtgox;

import at.outdated.bitcoin.exchange.api.client.ExchangeApiClient;
import at.outdated.bitcoin.exchange.api.market.Market;
import at.outdated.bitcoin.exchange.api.currency.Currency;
import at.outdated.bitcoin.exchange.api.market.transfer.TransferMethod;
import at.outdated.bitcoin.exchange.api.market.transfer.TransferType;

/**
 * Created with IntelliJ IDEA.
 * User: ebirn
 * Date: 23.05.13
 * Time: 21:32
 * To change this template use File | Settings | File Templates.
 */
public class MtGoxMarket extends Market {

     public MtGoxMarket() {
        super("mtgox", "http://www.mtgox.com", "Mt.Gox");

        withdrawals.put(Currency.BTC, new TransferMethod(Currency.BTC, TransferType.VIRTUAL));
        withdrawals.put(Currency.EUR, new TransferMethod(Currency.EUR, TransferType.BANK));

        deposits.put(Currency.BTC, new TransferMethod(Currency.BTC, TransferType.VIRTUAL));
        deposits.put(Currency.EUR, new TransferMethod(Currency.EUR, TransferType.BANK));

        addAsset(Currency.BTC, Currency.USD);
        addAsset(Currency.BTC, Currency.EUR);
        addAsset(Currency.BTC, Currency.JPY);

    }

    @Override
    public ExchangeApiClient getApiClient() {
        return new MtGoxClient(this);
    }

}
