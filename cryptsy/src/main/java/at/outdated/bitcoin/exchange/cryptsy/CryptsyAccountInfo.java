package at.outdated.bitcoin.exchange.cryptsy;

import at.outdated.bitcoin.exchange.api.account.AccountInfo;
import at.outdated.bitcoin.exchange.api.market.TradeDecision;
import at.outdated.bitcoin.exchange.api.market.fee.Fee;
import at.outdated.bitcoin.exchange.api.market.fee.SimplePercentageFee;

/**
 * Created by ebirn on 06.01.14.
 */
public class CryptsyAccountInfo extends AccountInfo {

    @Override
    public Fee getTradeFee(TradeDecision trade) {

        switch(trade) {
            case BUY:
                return new SimplePercentageFee(0.002);

            case SELL:
                return new SimplePercentageFee(0.003);
        }


        // you sould never come here
        return null;
    }
}
