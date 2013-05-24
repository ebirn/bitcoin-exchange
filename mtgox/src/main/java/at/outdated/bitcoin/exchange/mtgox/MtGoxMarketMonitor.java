package at.outdated.bitcoin.exchange.mtgox;


import at.outdated.bitcoin.exchange.api.Market;
import at.outdated.bitcoin.exchange.api.MarketUpdate;
import at.outdated.bitcoin.exchange.api.currency.Currency;
import at.outdated.bitcoin.exchange.api.market.Markets;
import at.outdated.bitcoin.exchange.api.market.TickerValue;
import at.outdated.btrader.market.ExchangeMarketMonitor;
import at.outdated.btrader.mechanics.TickerValueFactory;

import javax.ejb.AsyncResult;
import java.util.concurrent.Future;

/**
 * Created with IntelliJ IDEA.
 * User: ebirn
 * Date: 01.05.13
 * Time: 21:08
 * To change this template use File | Settings | File Templates.
 */

public class MtGoxMarketMonitor extends ExchangeMarketMonitor {

    MtGoxClient client = new MtGoxClient();


    @Override
    public Market getMarket() {
        return Markets.getMarket("mtgox");
    }

    @Override
    public Currency[] getTradedCurrencies() {
        return new Currency[]{ Currency.EUR, Currency.USD, Currency.JPY};
    }

    @Override
    public Future<TickerResponse> checkTicker(Currency currency) {

        TickerResponse ticker = null;

        try {
            ticker = client.getTicker(currency);
            ticker.setItemCurrency(Currency.BTC);

            TickerValue value = ticker.getTickerValue()

            ExchangeMarketMonitor.lastTicker.set(currency, value);

            // todo: log error when ticker timestamp is older than XXX
            MarketUpdate update = new MarketUpdate(getMarket(), value, getApiLagAvg(), getTradeLagAvg());
            marketUpdateEvent.fire(update);

        }
        catch (Exception e) {
            e.printStackTrace();
            ExchangeMarketMonitor.log.error("failed to get ticker value");
        }

        return new AsyncResult<TickerResponse>(ticker);
    }


    // this is order execution lag in millisecs (NOT ticker info delay!)
    @Override
    public Future<Number> checkTradeLag() {

        LagResponse lagResponse = client.getLag();

        if(lagResponse == null) {
            ExchangeMarketMonitor.log.error("failed to get trade lag.");
            return new AsyncResult<Number>(Double.NaN);
        }

        ExchangeMarketMonitor.tradeLagTrack.insert(lagResponse.getSeconds());
        return new AsyncResult<Number>(lagResponse.getSeconds());
    }


    @Override
    public double getTradeLagAvg() {
        return ExchangeMarketMonitor.tradeLagTrack.getStatistics().getMean();
    }

    @Override
    public Future<Number> getApiLag() {
        double apiLag = client.getApiLag();
        ExchangeMarketMonitor.apiLagTrack.insert(apiLag);
        return new AsyncResult<Number>(apiLag);
    }

    @Override
    public double getApiLagAvg() {
        return ExchangeMarketMonitor.apiLagTrack.getStatistics().getMean();
    }



}
