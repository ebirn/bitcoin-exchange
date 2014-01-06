import at.outdated.bitcoin.exchange.api.BaseTest;
import at.outdated.bitcoin.exchange.api.currency.Currency;
import at.outdated.bitcoin.exchange.api.currency.CurrencyValue;
import at.outdated.bitcoin.exchange.api.market.*;
import at.outdated.bitcoin.exchange.kraken.KrakenClient;
import org.junit.Test;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: ebirn
 * Date: 17.09.13
 * Time: 18:46
 * To change this template use File | Settings | File Templates.
 */
public class KrakenTest extends BaseTest {



    public void init() {
        market = Markets.getMarket("kraken");
        client = new KrakenClient(market);
        log = LoggerFactory.getLogger(getClass());
    }


    //TODO: reenable that
    /*
    @Test
    public void testTickerclient() {

        TickerValue ticker = client.getTicker(new AssetPair(Currency.BTC, Currency.EUR));
        System.out.println("ticker: " + ticker);

        assertTicker(ticker);
    }

    @Test
    public void testDepthClient() {
        MarketDepth depth = client.getMarketDepth(new AssetPair(Currency.BTC, Currency.EUR));
        assertDepth(depth);
    }


    @Test
    public void testMarketDepthPrice() {

        MarketDepth depth = client.getMarketDepth(new AssetPair(Currency.BTC, Currency.LTC));
        CurrencyValue tradeVolume = new CurrencyValue(90.0, Currency.LTC);

        CurrencyValue resultPrice = null;

        resultPrice = depth.getPrice(TradeDecision.SELL, tradeVolume);
        log.info("SELL {} get {}", tradeVolume, resultPrice);

        resultPrice = depth.getPrice(TradeDecision.BUY, tradeVolume);
        log.info("BUY {} get {}", tradeVolume, resultPrice);
    }

    */

    @Test
    public void placeOrder() {

        AssetPair asset = new AssetPair(Currency.BTC, Currency.EUR);

        CurrencyValue volume = new CurrencyValue(0.01, Currency.BTC);
        CurrencyValue price = new CurrencyValue(0.01, Currency.EUR);

        client.placeOrder(asset, TradeDecision.BUY, volume, price);

    }

    @Test
    public void testOpenOrders() {

        List<MarketOrder> openOrders = client.getOpenOrders();

        for(MarketOrder order : openOrders) {


            boolean success = client.cancelOrder(order.getId());

            log.info("cancel order: {} - success: {}", order.getId(), success);
        }

    }
}
