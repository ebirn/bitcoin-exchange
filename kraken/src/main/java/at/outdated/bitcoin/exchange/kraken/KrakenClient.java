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
        TickerResponse tickerResponse = simpleGetRequest(webResource, TickerResponse.class);

        return tickerResponse.getValue();
    }

    @Override
    public Number getLag() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    protected WebResource.Builder setupProtectedResource(WebResource res) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
