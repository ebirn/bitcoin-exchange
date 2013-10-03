package at.outdated.bitcoin.exchange.kraken;

import at.outdated.bitcoin.exchange.api.ExchangeApiClient;
import at.outdated.bitcoin.exchange.api.account.AccountInfo;
import at.outdated.bitcoin.exchange.api.currency.Currency;
import at.outdated.bitcoin.exchange.api.market.MarketDepth;
import at.outdated.bitcoin.exchange.api.market.TickerValue;
import at.outdated.bitcoin.exchange.kraken.jaxb.*;

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

        WebTarget webResource = client.target("https://api.kraken.com/0/public/Ticker?pair=XBT"+currency.name());
        //KrakenTickerResponse tickerResponse =

        TickerValue value = null;

        KrakenTickerResponse response = simpleGetRequest(webResource, KrakenTickerResponse.class);

        KrakenTickerValue tickerResponse = ( response.getResult().getXXBTZEUR());

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

        WebTarget webResource = client.target("https://api.kraken.com/0/public/Depth?pair=XBT"+quote.name());
        KrakenResponse response = simpleGetRequest(webResource, KrakenResponse.class);

        KrakenDepthValue depthResponse = (KrakenDepthValue) response.getResult().getXXBTZEUR();

        MarketDepth depth = null;

        if(depthResponse != null)
            depth = depthResponse.getValue();

        return depth;
    }

    @Override
    protected Invocation.Builder setupProtectedResource(WebTarget tgt) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
