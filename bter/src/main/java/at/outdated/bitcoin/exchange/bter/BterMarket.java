package at.outdated.bitcoin.exchange.bter;

import at.outdated.bitcoin.exchange.api.client.ExchangeClient;
import at.outdated.bitcoin.exchange.api.currency.CurrencyValue;
import at.outdated.bitcoin.exchange.api.market.Market;
import at.outdated.bitcoin.exchange.api.currency.Currency;
import at.outdated.bitcoin.exchange.api.market.fee.ConstantFee;
import at.outdated.bitcoin.exchange.api.market.transfer.TransferMethod;
import at.outdated.bitcoin.exchange.api.market.transfer.TransferType;

/**
 * Created by ebirn on 11.10.13.
 */
public class BterMarket extends Market {

    public BterMarket() {
        super("bter", "http://bter.com", "Bter.com");

        addWithdrawal(new TransferMethod(Currency.BTC, TransferType.VIRTUAL, new ConstantFee(new CurrencyValue(0.0005, Currency.BTC))));
        addWithdrawal(new TransferMethod(Currency.LTC, TransferType.VIRTUAL, new ConstantFee(new CurrencyValue(0.02, Currency.LTC))));
        addWithdrawal(new TransferMethod(Currency.PPC, TransferType.VIRTUAL,new ConstantFee(new CurrencyValue(0.001, Currency.PPC))));
        addWithdrawal(new TransferMethod(Currency.NMC, TransferType.VIRTUAL, new ConstantFee(new CurrencyValue(0.001, Currency.NMC))));

        addDeposit(new TransferMethod(Currency.BTC, TransferType.VIRTUAL));
        addDeposit(new TransferMethod(Currency.LTC, TransferType.VIRTUAL));
        addDeposit(new TransferMethod(Currency.PPC, TransferType.VIRTUAL));
        addDeposit(new TransferMethod(Currency.NMC, TransferType.VIRTUAL));

        addAsset(Currency.LTC, Currency.BTC);
        //addAsset(Currency.FTC, Currency.BTC);
        addAsset(Currency.PPC, Currency.BTC);
        //addAsset(Currency.NVC, Currency.BTC);
        addAsset(Currency.QRK, Currency.BTC);
        addAsset(Currency.NMC, Currency.BTC);
    }

    @Override
    public ExchangeClient createClient() {
        return new BterApiClient(this);
    }


}
