package at.outdated.bitcoin.exchange.kraken;

import at.outdated.bitcoin.exchange.api.ExchangeApiClient;
import at.outdated.bitcoin.exchange.api.account.AccountInfo;
import at.outdated.bitcoin.exchange.api.currency.Currency;
import at.outdated.bitcoin.exchange.api.market.MarketDepth;
import at.outdated.bitcoin.exchange.api.market.TickerValue;

import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;


/**
 * Created with IntelliJ IDEA.
 * User: ebirn
 * Date: 17.09.13
 * Time: 18:23
 * To change this template use File | Settings | File Templates.
 */
public class KrakenClient extends ExchangeApiClient {

    @Override
    public AccountInfo getAccountInfo() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public TickerValue getTicker(Currency currency) {

        WebTarget webResource = client.target("https://api.kraken.com/0/public/Ticker?pair=XBTEUR");
        KrakenTickerResponse tickerResponse = simpleGetRequest(webResource, KrakenTickerResponse.class);

        TickerValue value = null;

        if(tickerResponse != null)
            value = tickerResponse.getValue();

        return value;
    }

    @Override
    public Number getLag() {
        return 1;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public MarketDepth getMarketDepth(Currency base, Currency quote) {

        WebTarget webResource = client.target("https://api.kraken.com/0/public/Depth?pair=XBTEUR");
        KrakenDepthResponse depthResponse = simpleGetRequest(webResource, KrakenDepthResponse.class);

        MarketDepth depth = null;

        if(depthResponse != null)
            depth = depthResponse.getDepthValue();

        return depth;
    }

    @Override
    protected Invocation.Builder setupProtectedResource(WebTarget tgt) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
