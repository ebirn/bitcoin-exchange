package at.outdated.bitcoin.exchange.btce;

import at.outdated.bitcoin.exchange.api.client.ExchangeClient;
import at.outdated.bitcoin.exchange.api.currency.CurrencyValue;
import at.outdated.bitcoin.exchange.api.market.Market;
import at.outdated.bitcoin.exchange.api.currency.Currency;
import at.outdated.bitcoin.exchange.api.market.fee.ConstantFee;
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
        super("btce", "http://btc-e.com", "BTC-E");

        addWithdrawal(new TransferMethod(Currency.BTC, TransferType.VIRTUAL, new ConstantFee(new CurrencyValue(0.001, Currency.BTC))));
        addWithdrawal(new TransferMethod(Currency.LTC, TransferType.VIRTUAL, new ConstantFee(new CurrencyValue(0.1, Currency.LTC))));
        addWithdrawal(new TransferMethod(Currency.NMC, TransferType.VIRTUAL, new ConstantFee(new CurrencyValue(0.1, Currency.NMC))));
        addWithdrawal(new TransferMethod(Currency.PPC, TransferType.VIRTUAL, new ConstantFee(new CurrencyValue(0.1, Currency.PPC))));

        addDeposit(new TransferMethod(Currency.BTC, TransferType.VIRTUAL));
        addDeposit(new TransferMethod(Currency.LTC, TransferType.VIRTUAL));
        addDeposit(new TransferMethod(Currency.NMC, TransferType.VIRTUAL));
        addDeposit(new TransferMethod(Currency.PPC, TransferType.VIRTUAL));

        addAsset(Currency.BTC, Currency.USD);
        addAsset(Currency.BTC, Currency.EUR);

        addAsset(Currency.LTC, Currency.BTC);
        addAsset(Currency.PPC, Currency.BTC);
        addAsset(Currency.PPC, Currency.USD);

        addAsset(Currency.LTC, Currency.USD);
        addAsset(Currency.LTC, Currency.EUR);

        addAsset(Currency.NMC, Currency.BTC);
        addAsset(Currency.NMC, Currency.USD);

        addAsset(Currency.EUR, Currency.USD);
    }

    @Override
    public ExchangeClient createClient() {
        return new BtcEApiClient(this);  //To change body of implemented methods use File | Settings | File Templates.
    }


}
