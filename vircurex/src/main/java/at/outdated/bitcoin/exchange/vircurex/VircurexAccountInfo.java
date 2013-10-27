package at.outdated.bitcoin.exchange.vircurex;

import at.outdated.bitcoin.exchange.api.account.AccountInfo;
import at.outdated.bitcoin.exchange.api.currency.CurrencyValue;
import at.outdated.bitcoin.exchange.api.market.TradeDecision;
import at.outdated.bitcoin.exchange.api.market.fee.Fee;
import at.outdated.bitcoin.exchange.api.market.fee.SimplePercentageFee;

/**
 * Created by ebirn on 27.10.13.
 */
public class VircurexAccountInfo extends AccountInfo {

    Fee tradeFee = new SimplePercentageFee(0.0002);

    @Override
    public CurrencyValue getTradeFee(CurrencyValue volume, TradeDecision trade) {
        return tradeFee.calculate(trade, volume);
    }
}
