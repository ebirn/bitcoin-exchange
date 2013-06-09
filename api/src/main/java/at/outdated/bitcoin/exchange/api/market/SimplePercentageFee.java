package at.outdated.bitcoin.exchange.api.market;

import at.outdated.bitcoin.exchange.api.currency.CurrencyValue;

import java.math.BigDecimal;

/**
 * Created with IntelliJ IDEA.
 * User: ebirn
 * Date: 09.06.13
 * Time: 17:37
 * To change this template use File | Settings | File Templates.
 */
public class SimplePercentageFee extends Fee {

    BigDecimal percentage = new BigDecimal(0.0);

    public SimplePercentageFee() {

    }

    @Override
    public CurrencyValue calculate(TradeDecision decision, CurrencyValue volume) {

        CurrencyValue fee = new CurrencyValue(volume);
        fee.multiply(percentage);

        return fee;
    }

}
