package at.outdated.bitcoin.exchange.bitstamp;

import at.outdated.bitcoin.exchange.api.ExchangeApiClient;
import at.outdated.bitcoin.exchange.api.account.AccountInfo;
import at.outdated.bitcoin.exchange.api.account.Wallet;
import at.outdated.bitcoin.exchange.api.currency.Currency;
import at.outdated.bitcoin.exchange.api.currency.CurrencyValue;
import at.outdated.bitcoin.exchange.api.market.MarketDepth;
import at.outdated.bitcoin.exchange.api.market.MarketOrder;
import at.outdated.bitcoin.exchange.api.market.TickerValue;
import at.outdated.bitcoin.exchange.api.market.TradeDecision;
import org.slf4j.LoggerFactory;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import java.io.StringReader;

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


        log.debug("bitstamp balance: {}", balance);


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
    public MarketDepth getMarketDepth(Currency base, Currency quote) {

        // https://www.bitstamp.net/api/order_book/

        WebTarget depthTarget = client.target("https://www.bitstamp.net/api/order_book/");

        String depthString = simpleGetRequest(depthTarget, String.class);

        JsonObject depthData = Json.createReader(new StringReader(depthString)).readObject();

        MarketDepth depth = new MarketDepth();
        depth.setBaseCurrency(base);

        double[][] asks = parseNestedArray(depthData.getJsonArray("asks"));
        double[][] bids = parseNestedArray(depthData.getJsonArray("bids"));


        for(double[] bid : bids) {
            depth.getBids().add(new MarketOrder(TradeDecision.BUY, new CurrencyValue(bid[1], base), new CurrencyValue(bid[0], quote)));
        }

        for(double[] ask : asks) {
            depth.getAsks().add(new MarketOrder(TradeDecision.SELL, new CurrencyValue(ask[1], base), new CurrencyValue(ask[0], quote)));
        }

        return depth;
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
