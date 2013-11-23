package at.outdated.bitcoin.exchange.bitcurex.jaxb;

import at.outdated.bitcoin.exchange.api.account.AccountInfo;
import at.outdated.bitcoin.exchange.api.market.TradeDecision;
import at.outdated.bitcoin.exchange.api.market.fee.Fee;
import at.outdated.bitcoin.exchange.api.market.fee.SimplePercentageFee;

/**
 * Created by ebirn on 20.10.13.
 */
public class BitcurexAccountInfo extends AccountInfo {

    private Fee tradingFee = new SimplePercentageFee(0.004);

    @Override
    public Fee getTradeFee(TradeDecision trade) {
        return tradingFee;
    }
}
