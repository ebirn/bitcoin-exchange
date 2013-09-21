package at.outdated.bitcoin.exchange.bitstamp;

import at.outdated.bitcoin.exchange.api.ExchangeApiClient;
import at.outdated.bitcoin.exchange.api.account.AccountInfo;
import at.outdated.bitcoin.exchange.api.account.Wallet;
import at.outdated.bitcoin.exchange.api.currency.Currency;
import at.outdated.bitcoin.exchange.api.currency.CurrencyValue;
import at.outdated.bitcoin.exchange.api.market.TickerValue;
import org.slf4j.LoggerFactory;

import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;

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
    protected Invocation.Builder setupProtectedResource(WebTarget res) {
        return res.request();
    }

    @Override
    public AccountInfo getAccountInfo() {


        StringBuilder payloadBuilder = new StringBuilder();
        payloadBuilder.append("user=");
        payloadBuilder.append(getUserId("bitstamp"));
        payloadBuilder.append("&");
        payloadBuilder.append("password=");
        payloadBuilder.append(getSecret("bitstamp"));

        WebTarget balanceResource = client.target("https://www.bitstamp.net/api/balance/");
        BitstampAccountBalance balance = simplePostRequest(balanceResource, BitstampAccountBalance.class, payloadBuilder.toString());


        log.info("bitstamp balance: {}", balance);


        BitstampAccountInfo info = new BitstampAccountInfo();

        Wallet wUSD = new BitstampWallet(Currency.USD);
        wUSD.setBalance(new CurrencyValue(balance.getUsdBalance().doubleValue(), Currency.USD));
        info.setWallet(wUSD);

        Wallet wBTC = new BitstampWallet(Currency.BTC);
        wBTC.setBalance(new CurrencyValue(balance.getBtcBalance().doubleValue(), Currency.BTC));
        info.setWallet(wBTC);

        return info;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public TickerValue getTicker(Currency currency) {

        WebTarget tickerResource = client.target("https://www.bitstamp.net/api/ticker/");
        BitstampTickerValue bticker = simpleGetRequest(tickerResource, BitstampTickerValue.class);

        TickerValue ticker = null;
        if(bticker != null) ticker = bticker.getTickerValue();
        return ticker;
    }

    @Override
    public Number getLag() {
        return 1.0;
    }


}
