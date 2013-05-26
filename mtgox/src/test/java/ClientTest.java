import at.outdated.bitcoin.exchange.api.ExchangeApiClient;
import at.outdated.bitcoin.exchange.api.account.AccountInfo;
import at.outdated.bitcoin.exchange.mtgox.MtGoxClient;
import org.junit.Assert;
import org.junit.Test;

/**
 * Created with IntelliJ IDEA.
 * User: ebirn
 * Date: 26.05.13
 * Time: 17:24
 * To change this template use File | Settings | File Templates.
 */
public class ClientTest {


    ExchangeApiClient client = new MtGoxClient();

    @Test
    public void testAccountInfo() {

        AccountInfo info = client.getAccountInfo();
        Assert.assertNotNull(info);
    }
}
