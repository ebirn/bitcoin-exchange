package at.outdated.bitcoin.exchange.bitstamp;

import at.outdated.bitcoin.exchange.api.ExchangeApiClient;
import at.outdated.bitcoin.exchange.api.Market;
import at.outdated.bitcoin.exchange.api.currency.Currency;
import at.outdated.bitcoin.exchange.api.market.transfer.TransferMethod;
import at.outdated.bitcoin.exchange.api.market.transfer.TransferType;

/**
 * Created with IntelliJ IDEA.
 * User: ebirn
 * Date: 24.05.13
 * Time: 15:41
 * To change this template use File | Settings | File Templates.
 */
public class BitstampMarket extends Market {


    public BitstampMarket() {
        super("bitstamp", "https://www.bitstamp.net", "Bitstamp.net", Currency.USD);

        addWithdrawal(new TransferMethod(Currency.BTC, TransferType.VIRTUAL, null));
        addWithdrawal(new TransferMethod(Currency.XRP, TransferType.VIRTUAL, null));

        addDeposit(new TransferMethod(Currency.BTC, TransferType.VIRTUAL, null));
        addDeposit(new TransferMethod(Currency.XRP, TransferType.VIRTUAL, null));

        addAsset(Currency.BTC, Currency.USD);
    }

    @Override
    public ExchangeApiClient getApiClient() {
        return new BitstampClient(this);  //To change body of implemented methods use File | Settings | File Templates.
    }

}
