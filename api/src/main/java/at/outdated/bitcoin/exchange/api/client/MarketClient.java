package at.outdated.bitcoin.exchange.api.client;

import at.outdated.bitcoin.exchange.api.currency.Currency;
import at.outdated.bitcoin.exchange.api.market.AssetPair;
import at.outdated.bitcoin.exchange.api.market.MarketDepth;
import at.outdated.bitcoin.exchange.api.market.MarketOrder;
import at.outdated.bitcoin.exchange.api.market.TickerValue;

import java.util.Date;
import java.util.List;

/**
 * Created by ebirn on 09.11.13.
 */
public interface MarketClient {
    TickerValue getTicker(AssetPair asset);

    double getQuote(Currency base, Currency quote);

    MarketDepth getMarketDepth(AssetPair asset);

    // get historic trade data
    //List<MarketOrder> getTrades(Date since);

    double getApiLag();
}
