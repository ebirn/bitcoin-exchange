package at.outdated.bitcoin.exchange.api.market.fee;

import at.outdated.bitcoin.exchange.api.currency.Currency;
import at.outdated.bitcoin.exchange.api.currency.CurrencyValue;
import at.outdated.bitcoin.exchange.api.market.OrderType;

import java.math.BigDecimal;

/**
 * Created with IntelliJ IDEA.
 * User: ebirn
 * Date: 10.06.13
 * Time: 16:27
 * To change this template use File | Settings | File Templates.
 */
public class ConstantFee extends Fee {

    BigDecimal constantFee;
    Currency currency;

    public ConstantFee(CurrencyValue currencyFee) {
        this.constantFee = currencyFee.getValue();
        this.currency = currencyFee.getCurrency();
    }


    public ConstantFee(Number feeValue) {
        super();
        constantFee = new BigDecimal(feeValue.doubleValue());
    }


    public ConstantFee(String feeValue) {
        super();
        constantFee = new BigDecimal(feeValue);
    }

    @Override
    public CurrencyValue calculate(OrderType type, CurrencyValue volume) {

        Currency feeCurrency = currency == null ? volume.getCurrency() : currency;

        return new CurrencyValue(constantFee, feeCurrency);  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public String toString() {

        String curr = this.currency == null ? constantFee.toString() : new CurrencyValue(constantFee, currency).toString();

        return super.toString()  + curr;
    }
}
