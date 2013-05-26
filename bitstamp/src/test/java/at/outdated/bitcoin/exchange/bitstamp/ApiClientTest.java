package at.outdated.bitcoin.exchange.bitstamp;

import at.outdated.bitcoin.exchange.api.account.AccountInfo;
import at.outdated.bitcoin.exchange.api.currency.Currency;
import at.outdated.bitcoin.exchange.api.market.TickerValue;
import org.junit.Assert;
import org.junit.Test;

/**
 * Created with IntelliJ IDEA.
 * User: ebirn
 * Date: 25.05.13
 * Time: 14:03
 * To change this template use File | Settings | File Templates.
 */
public class ApiClientTest {

    BitstampClient bitstampClient = new BitstampClient();

    @Test
    public void testTicker() {

        TickerValue ticker = bitstampClient.getTicker(Currency.USD);
        Assert.assertNotNull("ticker value null", ticker);



        /*
        Client client = new Client();

        ClientResponse response = client.resource("https://www.bitstamp.net/api/ticker/").header("User-Agent", "blubbTest").accept(MediaType.APPLICATION_JSON_TYPE).get(ClientResponse.class);

        ClientResponse.Status status = response.getClientResponseStatus();
        System.out.println("status: " + status);
        System.out.println("content: " + response.getEntity(String.class));
*/

    }

    @Test
    public void testAccountInfo() {

        AccountInfo info = bitstampClient.getAccountInfo();

        Assert.assertNotNull(info);

    }
}
