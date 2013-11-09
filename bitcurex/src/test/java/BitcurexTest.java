import at.outdated.bitcoin.exchange.api.BaseTest;
import at.outdated.bitcoin.exchange.api.account.AccountInfo;
import at.outdated.bitcoin.exchange.api.currency.Currency;
import at.outdated.bitcoin.exchange.api.market.AssetPair;
import at.outdated.bitcoin.exchange.api.market.MarketDepth;
import at.outdated.bitcoin.exchange.api.market.Markets;
import at.outdated.bitcoin.exchange.api.market.TickerValue;
import at.outdated.bitcoin.exchange.bitcurex.BitcurexApiClient;
import at.outdated.bitcoin.exchange.bitcurex.BitcurexMarket;
import org.junit.Assert;
import org.junit.Test;

/**
 * Created with IntelliJ IDEA.
 * User: ebirn
 * Date: 30.05.13
 * Time: 13:11
 * To change this template use File | Settings | File Templates.
 */
public class BitcurexTest extends BaseTest{



    @Override
    public void init() {
        market = Markets.getMarket("bitcurex");
        client = new  BitcurexApiClient(market);
    }




}
