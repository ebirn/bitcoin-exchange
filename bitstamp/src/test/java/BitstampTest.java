import at.outdated.bitcoin.exchange.api.BaseTest;
import at.outdated.bitcoin.exchange.api.account.AccountInfo;
import at.outdated.bitcoin.exchange.api.currency.Currency;
import at.outdated.bitcoin.exchange.api.market.AssetPair;
import at.outdated.bitcoin.exchange.api.market.MarketDepth;
import at.outdated.bitcoin.exchange.api.market.Markets;
import at.outdated.bitcoin.exchange.api.market.TickerValue;
import at.outdated.bitcoin.exchange.bitstamp.BitstampClient;
import at.outdated.bitcoin.exchange.bitstamp.BitstampMarket;
import org.junit.Assert;
import org.junit.Test;

/**
 * Created with IntelliJ IDEA.
 * User: ebirn
 * Date: 25.05.13
 * Time: 14:03
 * To change this template use File | Settings | File Templates.
 */
public class BitstampTest extends BaseTest {


    @Override
    public void init() {
        market = Markets.getMarket("bitstamp");
        client = new BitstampClient(new BitstampMarket());
    }


}
