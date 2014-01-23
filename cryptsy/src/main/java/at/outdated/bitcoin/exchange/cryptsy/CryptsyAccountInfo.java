package at.outdated.bitcoin.exchange.cryptsy;

import at.outdated.bitcoin.exchange.api.account.AccountInfo;
import at.outdated.bitcoin.exchange.api.market.OrderType;
import at.outdated.bitcoin.exchange.api.market.fee.Fee;
import at.outdated.bitcoin.exchange.api.market.fee.SimplePercentageFee;

/**
 * Created by ebirn on 06.01.14.
 */
public class CryptsyAccountInfo extends AccountInfo {

    @Override
    public Fee getTradeFee(OrderType type) {

        switch(type) {
            case BID:
                return new SimplePercentageFee(0.002);

            case ASK:
                return new SimplePercentageFee(0.003);
        }


        // you sould never come here
        return null;
    }
}
