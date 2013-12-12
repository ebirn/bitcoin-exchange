package at.outdated.bitcoin.exchange.api.market.fee;

import at.outdated.bitcoin.exchange.api.currency.CurrencyValue;
import at.outdated.bitcoin.exchange.api.market.TradeDecision;

/**
 * Created with IntelliJ IDEA.
 * User: ebirn
 * Date: 10.06.13
 * Time: 16:31
 * To change this template use File | Settings | File Templates.
 */
public class InfiniteFee extends Fee {

    @Override
    public CurrencyValue calculate(TradeDecision decision, CurrencyValue volume) {
        return new CurrencyValue(Double.POSITIVE_INFINITY, volume.getCurrency());
    }
}
