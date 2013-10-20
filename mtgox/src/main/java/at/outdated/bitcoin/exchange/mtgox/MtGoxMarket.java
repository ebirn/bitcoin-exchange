package at.outdated.bitcoin.exchange.mtgox;

import at.outdated.bitcoin.exchange.api.ExchangeApiClient;
import at.outdated.bitcoin.exchange.api.Market;
import at.outdated.bitcoin.exchange.api.currency.Currency;
import at.outdated.bitcoin.exchange.api.market.AssetPair;
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
        super("mtgox", "http://www.mtgox.com", "Mt.Gox", Currency.EUR);


         withdrawals.put(Currency.BTC, new TransferMethod(Currency.BTC, TransferType.VIRTUAL, null));
         withdrawals.put(Currency.EUR, new TransferMethod(Currency.EUR, TransferType.BANK, null));

         deposits.put(Currency.BTC, new TransferMethod(Currency.BTC, TransferType.VIRTUAL, null));
         deposits.put(Currency.EUR, new TransferMethod(Currency.EUR, TransferType.BANK, null));
    }

    public AssetPair[] getTradedAssets() {
        return new AssetPair[] {
            new AssetPair(Currency.BTC, Currency.USD),
            new AssetPair(Currency.BTC, Currency.EUR),
            new AssetPair(Currency.BTC, Currency.JPY),
        };
    }

    @Override
    public ExchangeApiClient getApiClient() {
        return new MtGoxClient(this);
    }

}
