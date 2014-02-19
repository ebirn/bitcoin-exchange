package at.outdated.bitcoin.exchange.api.market.fee;

import at.outdated.bitcoin.exchange.api.currency.CurrencyValue;
import at.outdated.bitcoin.exchange.api.market.OrderType;

/**
 * Created with IntelliJ IDEA.
 * User: ebirn
 * Date: 09.06.13
 * Time: 17:30
 * To change this template use File | Settings | File Templates.
 */
public abstract class Fee {


    public abstract CurrencyValue calculate(OrderType type, CurrencyValue volume);


    // what remains after paying the fee
    public CurrencyValue remaining(OrderType type, CurrencyValue volume) {

        CurrencyValue remaining = new CurrencyValue(volume);

        CurrencyValue fee = calculate(type, volume);

        remaining.subtract(fee);

        return remaining;
    }

    @Override
    public String toString() {
        return "Fee: ";
    }
}
