package at.outdated.bitcoin.exchange.coinse;

import at.outdated.bitcoin.exchange.api.account.AccountInfo;
import at.outdated.bitcoin.exchange.api.market.TradeDecision;
import at.outdated.bitcoin.exchange.api.market.fee.Fee;
import at.outdated.bitcoin.exchange.api.market.fee.SimplePercentageFee;

/**
 * Created by ebirn on 06.01.14.
 */
public class CoinseAccountInfo extends AccountInfo {

    @Override
    public Fee getTradeFee(TradeDecision trade) {
        return new SimplePercentageFee(0.002);
    }
}
