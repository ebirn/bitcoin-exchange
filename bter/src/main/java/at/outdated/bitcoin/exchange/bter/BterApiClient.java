package at.outdated.bitcoin.exchange.bter;

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
public class BterApiClient extends ExchangeApiClient {

    public BterApiClient(Market market) {
        super(market);
    }

    @Override
    public AccountInfo getAccountInfo() {
        return null;
    }

    @Override
    public TickerValue getTicker(Currency currency) {

        WebTarget tickerTgt = client.target("https://bter.com/api/1/ticker/" + currency.name().toLowerCase() + "_btc");

        BterTicker ticker = simpleGetRequest(tickerTgt, BterTicker.class);

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
