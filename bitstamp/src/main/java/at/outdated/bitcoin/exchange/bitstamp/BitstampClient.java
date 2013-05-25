package at.outdated.bitcoin.exchange.bitstamp;

import at.outdated.bitcoin.exchange.api.ExchangeApiClient;
import at.outdated.bitcoin.exchange.api.account.WalletHistory;
import at.outdated.bitcoin.exchange.api.client.AccountInfo;
import at.outdated.bitcoin.exchange.api.currency.Currency;
import at.outdated.bitcoin.exchange.api.market.TickerValue;
import com.sun.jersey.api.client.WebResource;
import org.slf4j.LoggerFactory;

/**
 * Created with IntelliJ IDEA.
 * User: ebirn
 * Date: 25.05.13
 * Time: 13:04
 * To change this template use File | Settings | File Templates.
 */
public class BitstampClient extends ExchangeApiClient {

    static {
        log = LoggerFactory.getLogger("client.bitstamp");
    }

    @Override
    protected WebResource.Builder setupProtectedResource(WebResource res) {
        return res.getRequestBuilder();
    }

    @Override
    public AccountInfo getAccountInfo() {
        WebResource balanceResource = client.resource("https://www.bitstamp.net/api/balance/");

        StringBuilder payloadBuilder = new StringBuilder();
        payloadBuilder.append("user=");
        payloadBuilder.append(getUserId("bitstamp"));
        payloadBuilder.append("&");
        payloadBuilder.append("password=");
        payloadBuilder.append(getSecret("bitstamp"));

        BitstampAccountBalance balance = simplePostRequest(balanceResource, BitstampAccountBalance.class, payloadBuilder.toString());


        log.info("bitstamp balance: {}", balance);


        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public TickerValue getTicker(Currency currency) {

        WebResource tickerResource = client.resource("https://www.bitstamp.net/api/ticker/");
        BitstampTickerValue bticker = simpleGetRequest(tickerResource, BitstampTickerValue.class);

        TickerValue ticker = null;
        if(bticker != null) ticker = bticker.getTickerValue();
        return ticker;
    }

    @Override
    public Number getLag() {
        return 1.0;
    }

    @Override
    public WalletHistory getWalletHistory(Currency currency) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }


}
