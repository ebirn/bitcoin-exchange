package at.outdated.bitcoin.exchange.bter;

import at.outdated.bitcoin.exchange.api.account.AccountInfo;
import at.outdated.bitcoin.exchange.api.currency.CurrencyValue;
import at.outdated.bitcoin.exchange.api.market.TradeDecision;
import at.outdated.bitcoin.exchange.api.market.fee.Fee;
import at.outdated.bitcoin.exchange.api.market.fee.ZeroFee;

/**
 * Created by ebirn on 19.10.13.
 */
public class BterAccountInfo extends AccountInfo {

    Fee tradingFee = new ZeroFee();

    @Override
    public CurrencyValue getTradeFee(CurrencyValue volume, TradeDecision trade) {
        return tradingFee.calculate(trade, volume);
    }
}
