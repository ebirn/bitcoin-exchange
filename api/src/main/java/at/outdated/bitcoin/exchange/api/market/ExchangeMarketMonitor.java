package at.outdated.bitcoin.exchange.api.market;

import at.outdated.bitcoin.exchange.api.Market;
import at.outdated.bitcoin.exchange.api.container.CurrencyContainer;
import at.outdated.bitcoin.exchange.api.currency.Currency;
import at.outdated.bitcoin.exchange.api.track.NumberTrack;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Future;

/**
 * Created with IntelliJ IDEA.
 * User: ebirn
 * Date: 16.05.13
 * Time: 18:40
 * To change this template use File | Settings | File Templates.
 */

public abstract class ExchangeMarketMonitor {


    protected static NumberTrack tradeLagTrack = new NumberTrack(5);
    protected static NumberTrack apiLagTrack = new NumberTrack(5);

    protected static CurrencyContainer<TickerValue> lastTicker = new CurrencyContainer<>();

    protected static Logger log = LoggerFactory.getLogger("MarketMonitor");


    public abstract Currency[] getTradedCurrencies();
    public abstract Market getMarket();

    public abstract double getTradeLagAvg();

    public abstract Future<Number> getApiLag();

    public abstract double getApiLagAvg();

    public TickerValue getLastTicker(Currency c) {
        return lastTicker.get(c);
    }

}
