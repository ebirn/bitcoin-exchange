package at.outdated.bitcoin.exchange.vircurex;

import at.outdated.bitcoin.exchange.api.client.ExchangeApiClient;
import at.outdated.bitcoin.exchange.api.market.Market;
import at.outdated.bitcoin.exchange.api.currency.Currency;
import at.outdated.bitcoin.exchange.api.currency.CurrencyValue;
import at.outdated.bitcoin.exchange.api.market.fee.ConstantFee;
import at.outdated.bitcoin.exchange.api.market.fee.ZeroFee;
import at.outdated.bitcoin.exchange.api.market.transfer.TransferMethod;
import at.outdated.bitcoin.exchange.api.market.transfer.TransferType;

/**
 * Created by ebirn on 11.10.13.
 */
public class VircurexMarket extends Market {

    public VircurexMarket() {
        super("vircurex", "http://vircurex.com", "Vircurex");

        addWithdrawal(new TransferMethod(Currency.BTC, TransferType.VIRTUAL, new ConstantFee(new CurrencyValue(0.002, Currency.BTC))));
        addWithdrawal(new TransferMethod(Currency.LTC, TransferType.VIRTUAL, new ConstantFee(new CurrencyValue(0.1, Currency.LTC))));
        addWithdrawal(new TransferMethod(Currency.NMC, TransferType.VIRTUAL, new ConstantFee(new CurrencyValue(0.1, Currency.NMC))));
        addWithdrawal(new TransferMethod(Currency.NVC, TransferType.VIRTUAL, new ConstantFee(new CurrencyValue(0.1, Currency.NVC))));
        addWithdrawal(new TransferMethod(Currency.QRK, TransferType.VIRTUAL, new ConstantFee(new CurrencyValue(0.1, Currency.QRK))));

        // FIXME check currency
        addWithdrawal(new TransferMethod(Currency.PPC, TransferType.VIRTUAL, new ConstantFee(new CurrencyValue(0.02, Currency.PPC))));

        addDeposit(new TransferMethod(Currency.BTC, TransferType.VIRTUAL));
        addDeposit(new TransferMethod(Currency.LTC, TransferType.VIRTUAL));
        addDeposit(new TransferMethod(Currency.PPC, TransferType.VIRTUAL));
        addDeposit(new TransferMethod(Currency.QRK, TransferType.VIRTUAL));
        addDeposit(new TransferMethod(Currency.FTC, TransferType.VIRTUAL));

        addDeposit(new TransferMethod(Currency.EUR, TransferType.BANK));
        addDeposit(new TransferMethod(Currency.USD, TransferType.BANK));

        addAsset(Currency.BTC, Currency.LTC);
        addAsset(Currency.BTC, Currency.NMC);
        addAsset(Currency.BTC, Currency.NVC);
        addAsset(Currency.LTC, Currency.NMC);
        addAsset(Currency.LTC, Currency.NVC);
        addAsset(Currency.PPC, Currency.BTC);
        addAsset(Currency.NVC, Currency.NMC);
        addAsset(Currency.QRK, Currency.BTC);
    }

    @Override
    public ExchangeApiClient getApiClient() {
        return new VircurexApiClient(this);
    }


}
