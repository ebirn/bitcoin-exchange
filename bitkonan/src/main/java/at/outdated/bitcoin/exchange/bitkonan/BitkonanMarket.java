package at.outdated.bitcoin.exchange.bitkonan;

import at.outdated.bitcoin.exchange.api.ExchangeApiClient;
import at.outdated.bitcoin.exchange.api.Market;
import at.outdated.bitcoin.exchange.api.currency.Currency;
import at.outdated.bitcoin.exchange.api.market.transfer.TransferMethod;
import at.outdated.bitcoin.exchange.api.market.transfer.TransferType;

/**
 * Created with IntelliJ IDEA.
 * User: ebirn
 * Date: 26.05.13
 * Time: 23:41
 * To change this template use File | Settings | File Templates.
 */
public class BitkonanMarket extends Market {


    public BitkonanMarket() {
        super("bitkonan", "http://bitkonan.com", "BitKonan", Currency.USD);

        addDeposit(new TransferMethod(Currency.BTC, TransferType.VIRTUAL, null));
        addDeposit(new TransferMethod(Currency.USD, TransferType.BANK, null));

        addWithdrawal(new TransferMethod(Currency.BTC, TransferType.VIRTUAL, null));
        addWithdrawal(new TransferMethod(Currency.USD, TransferType.BANK, null));

        addAsset(Currency.BTC, Currency.USD);
    }

    @Override
    public ExchangeApiClient getApiClient() {
        return new BitkonanApiClient(this);  //To change body of implemented methods use File | Settings | File Templates.
    }

}
