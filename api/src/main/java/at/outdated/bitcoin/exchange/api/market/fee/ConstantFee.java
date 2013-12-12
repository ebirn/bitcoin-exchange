package at.outdated.bitcoin.exchange.api.market.fee;

import at.outdated.bitcoin.exchange.api.currency.Currency;
import at.outdated.bitcoin.exchange.api.currency.CurrencyValue;
import at.outdated.bitcoin.exchange.api.market.TradeDecision;

/**
 * Created with IntelliJ IDEA.
 * User: ebirn
 * Date: 10.06.13
 * Time: 16:27
 * To change this template use File | Settings | File Templates.
 */
public class ConstantFee extends Fee {

    double constantFee;
    Currency currency;

    public ConstantFee(CurrencyValue currencyFee) {
        this.constantFee = currencyFee.getValue();
        this.currency = currencyFee.getCurrency();
    }

    public ConstantFee(Number fee) {
        super();
        constantFee = fee.doubleValue();
    }

    @Override
    public CurrencyValue calculate(TradeDecision decision, CurrencyValue volume) {

        Currency feeCurrency = currency == null ? volume.getCurrency() : currency;

        return new CurrencyValue(constantFee, feeCurrency);  //To change body of implemented methods use File | Settings | File Templates.
    }
}
