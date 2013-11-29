package at.outdated.bitcoin.exchange.api;

import at.outdated.bitcoin.exchange.api.currency.Currency;
import at.outdated.bitcoin.exchange.api.currency.CurrencyValue;
import at.outdated.bitcoin.exchange.api.market.AssetPair;
import at.outdated.bitcoin.exchange.api.market.MarketDepth;
import at.outdated.bitcoin.exchange.api.market.TradeDecision;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

/**
 * Created by ebirn on 29.11.13.
 */
public class DepthTest {

    MarketDepth depth;

    AssetPair asset = new AssetPair(Currency.BTC, Currency.USD);

    Logger log = LoggerFactory.getLogger("MarketDepthTest");

    @Before
    public void setup() {
        depth = new MarketDepth();

        depth.setAsset(asset);
        depth.setTimestamp(new Date());


        bids();
        asks();
    }

    @Test
    public void testDepthPriceInAssetOrder() {

        log.info("depth: {}", depth);

        CurrencyValue volume = new CurrencyValue(3.0, Currency.BTC);

        CurrencyValue buyPrice = depth.getPrice(TradeDecision.BUY, volume);
        log.info("buy {} for {}", volume, buyPrice);

        Assert.assertNotEquals("buy currency mismatch", volume.getCurrency(), buyPrice.getCurrency());
        Assert.assertEquals("buy pice mismatch", 3170.43, buyPrice.getValue(), 0.001);

        CurrencyValue sellPrice = depth.getPrice(TradeDecision.SELL, volume);
        log.info("sell {} for {}", volume, sellPrice);
        Assert.assertEquals("sell pice mismatch", 3204.3353, sellPrice.getValue(), 0.001);

        Assert.assertNotEquals("sell currency mismatch", volume.getCurrency(), buyPrice.getCurrency());
    }

    @Test
    public void testDepthPriceReverseAssetOrder() {

        CurrencyValue volume = new CurrencyValue(5600.0, Currency.USD);

        CurrencyValue buyPrice = depth.getPrice(TradeDecision.BUY, volume);
        log.info("buy {} for {}", volume, buyPrice);

        Assert.assertNotEquals("buy currency mismatch", volume.getCurrency(), buyPrice.getCurrency());

        CurrencyValue sellPrice = depth.getPrice(TradeDecision.SELL, volume);
        log.info("sell {} for {}", volume, sellPrice);

        Assert.assertNotEquals("sell currency mismatch", volume.getCurrency(), buyPrice.getCurrency());

    }

        private void bids() {
        //["1056.81", "2.36020121"],
        depth.addBid(2.36020121, 1056.81);


        //["1054.80", "11.65030900"],
        depth.addBid(11.65030900, 1056.81);

        //["1054.64", "0.04900000"],
        depth.addBid(0.04900000, 1056.81);

        //["1053.59", "0.06204956"],
        depth.addBid(0.06204956, 1056.81);

        //["1052.20", "8.29972543"],
        depth.addBid(8.29972543, 1056.81);

        //["1051.10", "1.00000000"],
        depth.addBid(1.0, 1056.81);

        //["1051.00", "17.28495511"],
        depth.addBid(17.28495511, 1056.81);

        //["1050.80", "0.04400000"],
        depth.addBid(0.04400000, 1056.81);

        //["1050.52", "10.04163161"],
        depth.addBid(10.04163161, 1056.81);

        //["1050.00", "0.98854804"],
        depth.addBid(0.98854804, 1056.81);

        //["1050.96", "0.04500000"],
        depth.addBid(0.04500000, 1050.96);

        //["1049.12", "0.03600000"],
        depth.addBid(0.03600000, 049.12);

        //["1045.10", "10.05172184"],
        depth.addBid(10.05172184, 1045.10);

    }

    private void asks() {
        //["1068.10", "1.82157835"],
        depth.addAsk(1.82157835, 1068.10);

        //["1068.13", "16.98500000"],
        depth.addAsk(16.98500000, 1068.13);

        //["1068.50", "10.00000000"],
        depth.addAsk(10.00000000, 1068.50);

        //["1068.80", "0.49900000"],
        depth.addAsk(0.49900000, 1068.10);

        //["1068.99", "8.90236000"],
        depth.addAsk(8.90236000, 1068.99);

        //["1069.00", "26.99489207"],
        depth.addAsk(26.99489207, 1069.00);

        //["1069.03", "0.45000000"],
        depth.addAsk(0.45000000, 1069.03);

        //["1069.18", "0.00381027"],
        depth.addAsk(0.00381027, 1069.18);

        //["1069.20", "0.00382109"],
        depth.addAsk(0.00382109, 1069.20);

        //["1069.27", "0.04400000"],
        depth.addAsk(0.04400000, 1069.27);

        //["1069.38", "0.00986424"],
        depth.addAsk(0.00986424, 1069.38);

        //["1069.49", "0.10000000"],
        depth.addAsk(0.10000000, 1069.49);

        //["1069.50", "0.00200000"],
        depth.addAsk(0.00200000, 1069.50);

        //["1070.75", "0.01153800"],
        depth.addAsk(0.01153800, 1070.75);

        //["1070.80", "0.80950801"],
        depth.addAsk(0.80950801, 1070.80);

        //["1070.89", "13.74790000"],
        depth.addAsk(13.74790000, 1070.89);

        //["1071.90", "140.00000000"],
        depth.addAsk(140.00000000, 1071.90);

        //["1071.98", "20.00000000"],
        depth.addAsk(20.00000000, 1071.98);

        //["1072.00", "39.90833454"],
        depth.addAsk(39.90833454, 1072.00);

        //["1073.12", "0.05303906"],
        depth.addAsk(0.05303906, 1073.12);

        //["1073.13", "0.04400000"],
        depth.addAsk(0.04400000, 1073.13);

        //["1073.42", "0.05000000"],
        depth.addAsk(0.05000000, 1073.42);

        //["1075.50", "0.00200000"],
        depth.addAsk(0.00200000, 1075.50);

    }
}
