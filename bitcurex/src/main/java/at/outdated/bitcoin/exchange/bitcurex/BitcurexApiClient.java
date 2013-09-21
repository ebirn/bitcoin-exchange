package at.outdated.bitcoin.exchange.bitcurex;

import at.outdated.bitcoin.exchange.api.ExchangeApiClient;
import at.outdated.bitcoin.exchange.api.account.AccountInfo;
import at.outdated.bitcoin.exchange.api.currency.Currency;
import at.outdated.bitcoin.exchange.api.market.TickerValue;

import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;

/**
 * Created with IntelliJ IDEA.
 * User: ebirn
 * Date: 30.05.13
 * Time: 12:50
 * To change this template use File | Settings | File Templates.
 */
public class BitcurexApiClient extends ExchangeApiClient {
    @Override
    public AccountInfo getAccountInfo() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    protected <R> R simpleGetRequest(WebTarget resource, Class<R> resultClass) {
        String str =  super.simpleGetRequest(resource, String.class);
        return BitcurexJsonResolver.convertFromJson(str, resultClass);
    }


    @Override
    public TickerValue getTicker(Currency currency) {


        WebTarget tickerResource = client.target("https://" + currency.name().toLowerCase() + ".bitcurex.com/data/ticker.json");

        BitcurexTickerValue bTicker = simpleGetRequest(tickerResource, BitcurexTickerValue.class);

        if(bTicker == null) return null;

        TickerValue ticker = bTicker.getTickerValue();

        ticker.setCurrency(currency);

        return ticker;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Number getLag() {
        return 1.0;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    protected Invocation.Builder setupProtectedResource(WebTarget res) {
        return res.request();  //To change body of implemented methods use File | Settings | File Templates.
    }
}
