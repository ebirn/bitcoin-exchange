
package at.outdated.bitcoin.exchange.cryptsy;

import at.outdated.bitcoin.exchange.api.client.ExchangeApiClient;
import at.outdated.bitcoin.exchange.api.currency.Currency;
import at.outdated.bitcoin.exchange.api.currency.CurrencyValue;
import at.outdated.bitcoin.exchange.api.market.AssetPair;
import at.outdated.bitcoin.exchange.api.market.Market;
import at.outdated.bitcoin.exchange.api.market.fee.ConstantFee;
import at.outdated.bitcoin.exchange.api.market.fee.Fee;
import at.outdated.bitcoin.exchange.api.market.transfer.TransferMethod;
import at.outdated.bitcoin.exchange.api.market.transfer.TransferType;

import java.util.HashMap;
import java.util.Map;

public class CryptsyMarket extends Market {

    Map<AssetPair,Integer> marketId = new HashMap<>();

    public CryptsyMarket() {
        super("cryptsy", "http://cryptsy.com", "Cryptsy.com");


        addWithdrawal(new TransferMethod(Currency.BTC, TransferType.VIRTUAL, new ConstantFee(new CurrencyValue(0.00050000, Currency.BTC))));
        addWithdrawal(new TransferMethod(Currency.LTC, TransferType.VIRTUAL, new ConstantFee(new CurrencyValue(0.04000000, Currency.LTC))));
        addWithdrawal(new TransferMethod(Currency.NMC, TransferType.VIRTUAL, new ConstantFee(new CurrencyValue(0.01500000, Currency.NMC))));
        addWithdrawal(new TransferMethod(Currency.PPC, TransferType.VIRTUAL, new ConstantFee(new CurrencyValue(0.02000000, Currency.PPC))));
        addWithdrawal(new TransferMethod(Currency.NVC, TransferType.VIRTUAL, new ConstantFee(new CurrencyValue(0.02000000, Currency.NVC))));
        addWithdrawal(new TransferMethod(Currency.QRK, TransferType.VIRTUAL, new ConstantFee(new CurrencyValue(0.10000000 , Currency.QRK))));


        addDeposit(new TransferMethod(Currency.BTC, TransferType.VIRTUAL));
        addDeposit(new TransferMethod(Currency.LTC, TransferType.VIRTUAL));
        addDeposit(new TransferMethod(Currency.NMC, TransferType.VIRTUAL));
        addDeposit(new TransferMethod(Currency.PPC, TransferType.VIRTUAL));
        addDeposit(new TransferMethod(Currency.NVC, TransferType.VIRTUAL));
        addDeposit(new TransferMethod(Currency.QRK, TransferType.VIRTUAL));



        addAsset(Currency.LTC, Currency.BTC, 3); //3
        addAsset(Currency.NVC, Currency.BTC, 13); // 13
        addAsset(Currency.NMC, Currency.BTC, 29); // 29
        addAsset(Currency.PPC, Currency.BTC, 28); // 28
        addAsset(Currency.QRK, Currency.BTC, 71); // 71

        /*******************/

        addAsset(Currency.PPC, Currency.LTC, 125); // 125
        addAsset(Currency.QRK, Currency.LTC, 126); // 126
    }

    protected void addAsset(Currency base, Currency quote, int num) {
        AssetPair asset = new AssetPair(base, quote);
        assets.add(asset);
        marketId.put(asset, num);
    }

    @Override
    public ExchangeApiClient getApiClient() {
        return new CryptsyApiClient(this);
    }
}
