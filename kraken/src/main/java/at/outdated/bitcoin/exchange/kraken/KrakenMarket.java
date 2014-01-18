package at.outdated.bitcoin.exchange.kraken;

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
 * Date: 17.09.13
 * Time: 18:28
 * To change this template use File | Settings | File Templates.
 */
public class KrakenMarket extends Market {

    public KrakenMarket() {
        super("kraken", "http://www.kraken.com", "Kraken");

        addWithdrawal(new TransferMethod(Currency.BTC, TransferType.VIRTUAL, new ConstantFee(new CurrencyValue(0.0005, Currency.BTC))));
        addWithdrawal(new TransferMethod(Currency.LTC, TransferType.VIRTUAL, new ConstantFee(new CurrencyValue(0.02, Currency.LTC))));
        addWithdrawal(new TransferMethod(Currency.XRP, TransferType.VIRTUAL, new ConstantFee(new CurrencyValue(0.00002, Currency.XRP))));

        addWithdrawal(new TransferMethod(Currency.NMC, TransferType.VIRTUAL, new ConstantFee(new CurrencyValue(0.005, Currency.NMC))));

        addWithdrawal(new TransferMethod(Currency.EUR, TransferType.BANK, new ConstantFee(new CurrencyValue(0.09, Currency.EUR))));
        addWithdrawal(new TransferMethod(Currency.USD, TransferType.BANK, new ConstantFee(new CurrencyValue(30.0, Currency.USD))));


        addDeposit(new TransferMethod(Currency.BTC, TransferType.VIRTUAL));
        addDeposit(new TransferMethod(Currency.LTC, TransferType.VIRTUAL));
        addDeposit(new TransferMethod(Currency.XRP, TransferType.VIRTUAL));
        addDeposit(new TransferMethod(Currency.NMC, TransferType.VIRTUAL));

        addDeposit(new TransferMethod(Currency.EUR, TransferType.BANK));
        addDeposit(new TransferMethod(Currency.USD, TransferType.BANK));

        addAsset(Currency.BTC, Currency.EUR);
        addAsset(Currency.BTC, Currency.USD);
        addAsset(Currency.BTC, Currency.XRP);
        addAsset(Currency.USD, Currency.XRP);
        addAsset(Currency.EUR, Currency.XRP);
        addAsset(Currency.BTC, Currency.LTC);
        addAsset(Currency.LTC, Currency.USD);
        addAsset(Currency.LTC, Currency.EUR);
        addAsset(Currency.LTC, Currency.XRP);

        addAsset(Currency.BTC, Currency.NMC);
        addAsset(Currency.NMC, Currency.USD);
        addAsset(Currency.NMC, Currency.EUR);
        addAsset(Currency.NMC, Currency.XRP);
    }

    @Override
    public ExchangeClient createClient() {
        return new KrakenClient(this);
    }

}
