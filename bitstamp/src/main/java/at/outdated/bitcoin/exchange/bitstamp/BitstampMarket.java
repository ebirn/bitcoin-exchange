package at.outdated.bitcoin.exchange.bitstamp;

import at.outdated.bitcoin.exchange.api.client.ExchangeApiClient;
import at.outdated.bitcoin.exchange.api.currency.CurrencyValue;
import at.outdated.bitcoin.exchange.api.market.Market;
import at.outdated.bitcoin.exchange.api.currency.Currency;
import at.outdated.bitcoin.exchange.api.market.fee.ConstantFee;
import at.outdated.bitcoin.exchange.api.market.fee.SimplePercentageFee;
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
        super("bitstamp", "https://www.bitstamp.net", "Bitstamp.net");

        addWithdrawal(new TransferMethod(Currency.BTC, TransferType.VIRTUAL));
        addWithdrawal(new TransferMethod(Currency.XRP, TransferType.VIRTUAL, new SimplePercentageFee( 0.002)));

        addWithdrawal(new TransferMethod(Currency.EUR, TransferType.BANK, new ConstantFee(new CurrencyValue(0.90, Currency.EUR))));
        addWithdrawal(new TransferMethod(Currency.USD, TransferType.BANK, new SimplePercentageFee(0.0009)));

        addDeposit(new TransferMethod(Currency.BTC, TransferType.VIRTUAL));
        addDeposit(new TransferMethod(Currency.XRP, TransferType.VIRTUAL));

        addDeposit(new TransferMethod(Currency.EUR, TransferType.BANK));
        addDeposit(new TransferMethod(Currency.USD, TransferType.BANK));

        addAsset(Currency.BTC, Currency.USD);
    }

    @Override
    public ExchangeApiClient getApiClient() {
        return new BitstampClient(this);  //To change body of implemented methods use File | Settings | File Templates.
    }

}
