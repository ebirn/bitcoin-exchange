package at.outdated.bitcoin.exchange.kraken;

import at.outdated.bitcoin.exchange.api.account.AccountInfo;
import at.outdated.bitcoin.exchange.api.currency.CurrencyValue;
import at.outdated.bitcoin.exchange.api.market.TradeDecision;

/**
 * Created by ebirn on 07.10.13.
 */
public class KrakenAccountInfo extends AccountInfo {

    @Override
    public String getLogin() {
        return null;
    }

    @Override
    public CurrencyValue getTradeFee(CurrencyValue volume, TradeDecision trade) {
        return null;
    }
}
