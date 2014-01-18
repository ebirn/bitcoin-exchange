package at.outdated.bitcoin.exchange.coinse;

import at.outdated.bitcoin.exchange.api.client.ExchangeClient;
import at.outdated.bitcoin.exchange.api.currency.Currency;
import at.outdated.bitcoin.exchange.api.currency.CurrencyValue;
import at.outdated.bitcoin.exchange.api.market.Market;
import at.outdated.bitcoin.exchange.api.market.fee.ConstantFee;
import at.outdated.bitcoin.exchange.api.market.transfer.TransferMethod;
import at.outdated.bitcoin.exchange.api.market.transfer.TransferType;

/**
 * Created by ebirn on 06.01.14.
 */
public class CoinseMarket extends Market {

    public CoinseMarket() {
        super("coinse", "http://coins-e.com", "Coins-e.com");


        addDeposit(new TransferMethod(Currency.BTC, TransferType.VIRTUAL));
        addDeposit(new TransferMethod(Currency.LTC, TransferType.VIRTUAL));
        addDeposit(new TransferMethod(Currency.NVC, TransferType.VIRTUAL));
        addDeposit(new TransferMethod(Currency.PPC, TransferType.VIRTUAL));
        addDeposit(new TransferMethod(Currency.QRK, TransferType.VIRTUAL));

        addWithdrawal(new TransferMethod(Currency.BTC, TransferType.VIRTUAL, new ConstantFee(new CurrencyValue(0.00200000, Currency.BTC))));
        addWithdrawal(new TransferMethod(Currency.LTC, TransferType.VIRTUAL, new ConstantFee(new CurrencyValue(0.05000000, Currency.LTC))));
        addWithdrawal(new TransferMethod(Currency.NVC, TransferType.VIRTUAL, new ConstantFee(new CurrencyValue(0.01000000, Currency.NVC))));
        addWithdrawal(new TransferMethod(Currency.PPC, TransferType.VIRTUAL, new ConstantFee(new CurrencyValue(0.01000000, Currency.PPC))));
        addWithdrawal(new TransferMethod(Currency.QRK, TransferType.VIRTUAL, new ConstantFee(new CurrencyValue(0.50000000, Currency.QRK))));

        addAsset(Currency.LTC, Currency.BTC);
        addAsset(Currency.NVC, Currency.BTC);
        addAsset(Currency.PPC, Currency.BTC);
        addAsset(Currency.QRK, Currency.BTC);

        addAsset(Currency.QRK, Currency.LTC);
    }

    @Override
    public ExchangeClient createClient() {
        return new CoinseApiClient(this);
    }
}
