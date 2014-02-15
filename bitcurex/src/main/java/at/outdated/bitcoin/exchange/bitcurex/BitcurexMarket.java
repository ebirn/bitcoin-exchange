package at.outdated.bitcoin.exchange.bitcurex;

import at.outdated.bitcoin.exchange.api.client.ExchangeClient;
import at.outdated.bitcoin.exchange.api.market.Market;
import at.outdated.bitcoin.exchange.api.currency.Currency;
import at.outdated.bitcoin.exchange.api.currency.CurrencyValue;
import at.outdated.bitcoin.exchange.api.market.fee.ConstantFee;
import at.outdated.bitcoin.exchange.api.market.transfer.TransferMethod;
import at.outdated.bitcoin.exchange.api.market.transfer.TransferType;

/**
 * Created with IntelliJ IDEA.
 * User: ebirn
 * Date: 30.05.13
 * Time: 12:50
 * To change this template use File | Settings | File Templates.
 */
public class BitcurexMarket extends Market {


    public BitcurexMarket() {
        super("bitcurex", "https://eur.bitcurex.com/", "Bitcurex");

        addDeposit(new TransferMethod(Currency.BTC, TransferType.VIRTUAL));
        addDeposit(new TransferMethod(Currency.EUR, TransferType.BANK, new ConstantFee(new CurrencyValue("0.30", Currency.EUR))));

        addWithdrawal(new TransferMethod(Currency.BTC, TransferType.VIRTUAL));
        addWithdrawal(new TransferMethod(Currency.EUR, TransferType.BANK, new ConstantFee(new CurrencyValue("1.15", Currency.EUR))));

        addAsset(Currency.BTC, Currency.EUR);
        addAsset(Currency.BTC, Currency.PLN);
    }

    @Override
    public ExchangeClient createClient() {
        return new BitcurexApiClient(this);  //To change body of implemented methods use File | Settings | File Templates.
    }


}
