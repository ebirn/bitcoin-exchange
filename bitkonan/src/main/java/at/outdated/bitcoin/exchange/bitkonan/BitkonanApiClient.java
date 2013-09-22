package at.outdated.bitcoin.exchange.bitkonan;

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
 * Date: 26.05.13
 * Time: 23:43
 * To change this template use File | Settings | File Templates.
 */
public class BitkonanApiClient extends ExchangeApiClient {

    @Override
    public AccountInfo getAccountInfo() {
        return new BitkonanAccountInfo();  //To change body of implemented methods use File | Settings | File Templates.
    }


    @Override
    public MarketDepth getMarketDepth(Currency base, Currency quote) {
        return null;
    }

    @Override
    protected <R> R simpleGetRequest(WebTarget target, Class<R> resultClass) {

        R result = null;

        String resultStr = super.simpleGetRequest(target, String.class);

        result = BitkonanJsonResolver.convertFromJson(resultStr, resultClass);

        return result;
    }

    @Override
    public TickerValue getTicker(Currency currency) {

        // https://btc-e.com/api/2/btc_usd/ticker

        WebTarget tickerResource = client.target("https://bitkonan.com/api/ticker/");

        BitkonanTickerValue response = simpleGetRequest(tickerResource, BitkonanTickerValue.class);


        TickerValue value = response.getTickerValue();
        value.setCurrency(currency);

        return value;
    }

    @Override
    public Number getLag() {
        return 0.12345678910;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    protected Invocation.Builder setupProtectedResource(WebTarget res) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
