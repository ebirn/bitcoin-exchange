package at.outdated.bitcoin.exchange.vircurex;

import at.outdated.bitcoin.exchange.api.client.ExchangeClient;
import at.outdated.bitcoin.exchange.api.market.Market;
import at.outdated.bitcoin.exchange.api.currency.Currency;
import at.outdated.bitcoin.exchange.api.currency.CurrencyValue;
import at.outdated.bitcoin.exchange.api.market.fee.ConstantFee;
import at.outdated.bitcoin.exchange.api.market.transfer.TransferMethod;
import at.outdated.bitcoin.exchange.api.market.transfer.TransferType;

/**
 * Created by ebirn on 11.10.13.
 */
public class VircurexMarket extends Market {

    public VircurexMarket() {
        super("vircurex", "http://vircurex.com", "Vircurex");

        addWithdrawal(new TransferMethod(Currency.BTC, TransferType.VIRTUAL, new ConstantFee(new CurrencyValue("0.002", Currency.BTC))));
        addWithdrawal(new TransferMethod(Currency.LTC, TransferType.VIRTUAL, new ConstantFee(new CurrencyValue("0.1", Currency.LTC))));
        addWithdrawal(new TransferMethod(Currency.NMC, TransferType.VIRTUAL, new ConstantFee(new CurrencyValue("0.1", Currency.NMC))));
        addWithdrawal(new TransferMethod(Currency.NVC, TransferType.VIRTUAL, new ConstantFee(new CurrencyValue("0.1", Currency.NVC))));
        addWithdrawal(new TransferMethod(Currency.QRK, TransferType.VIRTUAL, new ConstantFee(new CurrencyValue("0.5", Currency.QRK))));
        addWithdrawal(new TransferMethod(Currency.FTC, TransferType.VIRTUAL, new ConstantFee(new CurrencyValue("0.01", Currency.FTC))));
        addWithdrawal(new TransferMethod(Currency.PPC, TransferType.VIRTUAL, new ConstantFee(new CurrencyValue("0.02", Currency.PPC))));

        addDeposit(new TransferMethod(Currency.BTC, TransferType.VIRTUAL));
        addDeposit(new TransferMethod(Currency.LTC, TransferType.VIRTUAL));
        addDeposit(new TransferMethod(Currency.NMC, TransferType.VIRTUAL));
        addDeposit(new TransferMethod(Currency.NVC, TransferType.VIRTUAL));
        addDeposit(new TransferMethod(Currency.QRK, TransferType.VIRTUAL));
        addDeposit(new TransferMethod(Currency.FTC, TransferType.VIRTUAL));
        addDeposit(new TransferMethod(Currency.PPC, TransferType.VIRTUAL));


        addDeposit(new TransferMethod(Currency.EUR, TransferType.BANK));
        addDeposit(new TransferMethod(Currency.USD, TransferType.BANK));


        addAsset(Currency.BTC, Currency.USD);
        addAsset(Currency.BTC, Currency.EUR);

        addAsset(Currency.BTC, Currency.LTC);
        addAsset(Currency.BTC, Currency.NMC);
        addAsset(Currency.BTC, Currency.NVC);
        addAsset(Currency.BTC, Currency.PPC);
        addAsset(Currency.BTC, Currency.QRK);
        addAsset(Currency.BTC, Currency.FTC);


        addAsset(Currency.LTC, Currency.BTC);
        addAsset(Currency.LTC, Currency.NMC);
        addAsset(Currency.LTC, Currency.NVC);
        addAsset(Currency.LTC, Currency.PPC);
        addAsset(Currency.LTC, Currency.QRK);
        addAsset(Currency.LTC, Currency.FTC);

        addAsset(Currency.PPC, Currency.BTC);
        addAsset(Currency.PPC, Currency.NMC);
        addAsset(Currency.PPC, Currency.NVC);
        addAsset(Currency.PPC, Currency.LTC);
        addAsset(Currency.PPC, Currency.QRK);
        addAsset(Currency.PPC, Currency.FTC);

        addAsset(Currency.NMC, Currency.BTC);
        addAsset(Currency.NMC, Currency.LTC);
        addAsset(Currency.NMC, Currency.NVC);
        addAsset(Currency.NMC, Currency.PPC);
        addAsset(Currency.NMC, Currency.FTC);
        addAsset(Currency.NMC, Currency.QRK);

        addAsset(Currency.NVC, Currency.BTC);
        addAsset(Currency.NVC, Currency.LTC);
        addAsset(Currency.NVC, Currency.NMC);
        addAsset(Currency.NVC, Currency.PPC);
        addAsset(Currency.NVC, Currency.FTC);
        addAsset(Currency.NVC, Currency.QRK);

        addAsset(Currency.QRK, Currency.BTC);
        addAsset(Currency.QRK, Currency.LTC);
        addAsset(Currency.QRK, Currency.NMC);
        addAsset(Currency.QRK, Currency.NVC);
        addAsset(Currency.QRK, Currency.PPC);
        addAsset(Currency.QRK, Currency.FTC);

        addAsset(Currency.FTC, Currency.BTC);
        addAsset(Currency.FTC, Currency.LTC);
        addAsset(Currency.FTC, Currency.NMC);
        addAsset(Currency.FTC, Currency.NVC);
        addAsset(Currency.FTC, Currency.PPC);
        // addAsset(Currency.FTC, Currency.QRK);
    }

    @Override
    public ExchangeClient createClient() {
        return new VircurexApiClient(this);
    }


}
