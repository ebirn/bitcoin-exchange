package at.outdated.bitcoin.exchange.bter;

import at.outdated.bitcoin.exchange.api.account.AccountInfo;
import at.outdated.bitcoin.exchange.api.market.TradeDecision;
import at.outdated.bitcoin.exchange.api.market.fee.Fee;
import at.outdated.bitcoin.exchange.api.market.fee.SimplePercentageFee;
import at.outdated.bitcoin.exchange.api.market.fee.ZeroFee;

/**
 * Created by ebirn on 19.10.13.
 */
public class BterAccountInfo extends AccountInfo {

    Fee tradingFee = new SimplePercentageFee(0.002);

    @Override
    public Fee getTradeFee(TradeDecision trade) {
        return tradingFee;
    }
}
