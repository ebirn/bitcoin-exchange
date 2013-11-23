package at.outdated.bitcoin.exchange.kraken;

import at.outdated.bitcoin.exchange.api.account.AccountInfo;
import at.outdated.bitcoin.exchange.api.market.TradeDecision;
import at.outdated.bitcoin.exchange.api.market.fee.Fee;
import at.outdated.bitcoin.exchange.api.market.fee.SimplePercentageFee;

/**
 * Created by ebirn on 07.10.13.
 */
public class KrakenAccountInfo extends AccountInfo {



    @Override
    public Fee getTradeFee(TradeDecision trade) {

        return new SimplePercentageFee(0.003);
    }
}
