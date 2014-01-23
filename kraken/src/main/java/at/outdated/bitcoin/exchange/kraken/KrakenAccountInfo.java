package at.outdated.bitcoin.exchange.kraken;

import at.outdated.bitcoin.exchange.api.account.AccountInfo;
import at.outdated.bitcoin.exchange.api.currency.CurrencyValue;
import at.outdated.bitcoin.exchange.api.market.OrderType;
import at.outdated.bitcoin.exchange.api.market.fee.Fee;
import at.outdated.bitcoin.exchange.api.market.fee.SimplePercentageFee;

import java.math.BigDecimal;

/**
 * Created by ebirn on 07.10.13.
 */
public class KrakenAccountInfo extends AccountInfo {



    @Override
    public Fee getTradeFee(OrderType trade) {

        return new SimplePercentageFee(new BigDecimal("0.002", CurrencyValue.CURRENCY_MATH_CONTEXT));
    }
}
