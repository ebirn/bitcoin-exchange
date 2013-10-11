package at.outdated.bitcoin.exchange.bitstamp;

import at.outdated.bitcoin.exchange.api.account.AccountInfo;
import at.outdated.bitcoin.exchange.api.currency.CurrencyValue;
import at.outdated.bitcoin.exchange.api.market.TradeDecision;

/**
 * Created with IntelliJ IDEA.
 * User: ebirn
 * Date: 26.05.13
 * Time: 17:01
 * To change this template use File | Settings | File Templates.
 */
public class BitstampAccountInfo extends AccountInfo {


    @Override
    public CurrencyValue getTradeFee(CurrencyValue volume, TradeDecision trade) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
