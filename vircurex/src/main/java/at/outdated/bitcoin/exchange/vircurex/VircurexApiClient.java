package at.outdated.bitcoin.exchange.vircurex;

import at.outdated.bitcoin.exchange.api.ExchangeApiClient;
import at.outdated.bitcoin.exchange.api.Market;
import at.outdated.bitcoin.exchange.api.account.AccountInfo;
import at.outdated.bitcoin.exchange.api.currency.Currency;
import at.outdated.bitcoin.exchange.api.market.MarketDepth;
import at.outdated.bitcoin.exchange.api.market.TickerValue;

import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;

/**
 * Created by ebirn on 11.10.13.
 */
public class VircurexApiClient extends ExchangeApiClient {

    public VircurexApiClient(Market market) {
        super(market);
    }

    @Override
    public AccountInfo getAccountInfo() {
        return null;
    }

    @Override
    public TickerValue getTicker(Currency currency) {

        // get_info_for_1_currency
        WebTarget tickerTgt = client.target("https://vircurex.com/api/get_info_for_1_currency.json?base=BTC&alt=" + currency.name());
        // {"base":"BTC","alt":"LTC","lowest_ask":"62.30141425","highest_bid":"61.12503063","last_trade":"62.30529595","volume":"82.92624028"}%

        VircurexTicker ticker = simpleGetRequest(tickerTgt, VircurexTicker.class);

        return ticker.getValue();
    }

    @Override
    public Number getLag() {
        return null;
    }

    @Override
    public MarketDepth getMarketDepth(Currency base, Currency quote) {
        return null;
    }

    @Override
    protected <T> Invocation.Builder setupProtectedResource(WebTarget res, Entity<T> entity) {
        return null;
    }
}
