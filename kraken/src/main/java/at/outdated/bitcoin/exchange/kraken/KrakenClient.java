package at.outdated.bitcoin.exchange.kraken;

import at.outdated.bitcoin.exchange.api.ExchangeApiClient;
import at.outdated.bitcoin.exchange.api.account.AccountInfo;
import at.outdated.bitcoin.exchange.api.currency.Currency;
import at.outdated.bitcoin.exchange.api.market.TickerValue;
import com.sun.jersey.api.client.WebResource;

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


        WebResource webResource = client.resource("https://api.kraken.com/0/public/Ticker?pair=XBTEUR");
        String rawTicker = simpleGetRequest(webResource, String.class);
        log.info("raw KRAKEN " + rawTicker);

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
    protected WebResource.Builder setupProtectedResource(WebResource res) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
