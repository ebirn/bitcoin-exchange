package at.outdated.bitcoin.exchange.api.market.fee;

import at.outdated.bitcoin.exchange.api.currency.CurrencyValue;
import at.outdated.bitcoin.exchange.api.market.TradeDecision;

/**
 * Created with IntelliJ IDEA.
 * User: ebirn
 * Date: 09.06.13
 * Time: 17:30
 * To change this template use File | Settings | File Templates.
 */
public abstract class Fee {


    public abstract CurrencyValue calculate(TradeDecision decision, CurrencyValue volume);
}
