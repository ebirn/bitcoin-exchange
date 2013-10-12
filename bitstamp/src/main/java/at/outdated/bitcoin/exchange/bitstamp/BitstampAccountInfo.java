package at.outdated.bitcoin.exchange.bitstamp;

import at.outdated.bitcoin.exchange.api.account.AccountInfo;
import at.outdated.bitcoin.exchange.api.currency.CurrencyValue;
import at.outdated.bitcoin.exchange.api.market.TradeDecision;
import at.outdated.bitcoin.exchange.api.market.fee.Fee;

/**
 * Created with IntelliJ IDEA.
 * User: ebirn
 * Date: 26.05.13
 * Time: 17:01
 * To change this template use File | Settings | File Templates.
 */
public class BitstampAccountInfo extends AccountInfo {

    protected Fee fee;

    public void setFee(Fee fee) {
        this.fee = fee;
    }

    @Override
    public CurrencyValue getTradeFee(CurrencyValue volume, TradeDecision trade) {
        return fee.calculate(trade, volume);  //To change body of implemented methods use File | Settings | File Templates.
    }
}
