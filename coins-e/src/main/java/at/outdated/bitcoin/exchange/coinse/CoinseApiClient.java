package at.outdated.bitcoin.exchange.coinse;

import at.outdated.bitcoin.exchange.api.account.AccountInfo;
import at.outdated.bitcoin.exchange.api.client.ExchangeApiClient;
import at.outdated.bitcoin.exchange.api.client.MarketClient;
import at.outdated.bitcoin.exchange.api.client.TradeClient;
import at.outdated.bitcoin.exchange.api.currency.CurrencyValue;
import at.outdated.bitcoin.exchange.api.market.*;

import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;

/**
 * Created by ebirn on 06.01.14.
 */
public class CoinseApiClient extends ExchangeApiClient implements TradeClient, MarketClient {

    public CoinseApiClient(Market market) {
        super(market);


    }

    @Override
    public Number getLag() {
        return 0.01234;
    }

    @Override
    protected <T> Invocation.Builder setupProtectedResource(WebTarget res, Entity<T> entity) {
        return null;
    }

    @Override
    public TickerValue getTicker(AssetPair asset) {

        // https://www.coins-e.com/api/v2/markets/data/

        String raw = simpleGetRequest(client.target("https://www.coins-e.com/api/v2/markets/data/"), String.class);

        JsonObject root = jsonFromString(raw);

        JsonObject jsonMarketList = root.getJsonObject("markets");

        String marketKey = asset.getBase() + "_" + asset.getQuote();
        JsonObject jsonMarket = jsonMarketList.getJsonObject(marketKey);

        JsonObject marketStat = jsonMarket.getJsonObject("marketstat");
        JsonObject stat24 = marketStat.getJsonObject("24h");

        TickerValue ticker = new TickerValue();

        ticker.setAsset(asset);
        ticker.setLast(Double.parseDouble(marketStat.getString("ltp")));
        ticker.setAsk(Double.parseDouble(marketStat.getString("ask")));
        ticker.setBid(Double.parseDouble(marketStat.getString("bid")));

        ticker.setHigh(Double.parseDouble(stat24.getString("h")));
        ticker.setLow(Double.parseDouble(stat24.getString("l")));
        ticker.setVolume(Double.parseDouble(stat24.getString("volume")));

        return ticker;
    }

    @Override
    public MarketDepth getMarketDepth(AssetPair asset) {

        // https://www.coins-e.com/api/v2/market/WDC_BTC/depth/

        String marketKey = asset.getBase() + "_" + asset.getQuote();

        String raw = simpleGetRequest(client.target("https://www.coins-e.com/api/v2/market/"+marketKey+"/depth/"), String.class);
        JsonObject root = jsonFromString(raw);

        // {"status": true, "ltq": "31.07186789", "ltp": "0.00052500", "marketdepth": {"bids": [{"q": "67.61970

        JsonObject jsonDepth = root.getJsonObject("marketdepth");




        MarketDepth depth = new MarketDepth(asset);

        // are these mixed up in the api / exchange site?

        JsonArray jsonAsks = jsonDepth.getJsonArray("asks");
        // beginning lowest ask
        for(int i=0; i<jsonAsks.size(); i++) {
            JsonObject obj = jsonAsks.getJsonObject(i);
            double price = Double.parseDouble(obj.getString("r"));
            double volume = Double.parseDouble(obj.getString("q"));

            if(obj.getInt("n") > 0) {
                depth.addAsk(volume, price);
            }
        }

        JsonArray jsonBids = jsonDepth.getJsonArray("bids");
        // beginning with highest bid
        for(int i=0; i<jsonBids.size(); i++) {
            JsonObject obj = jsonBids.getJsonObject(i);
            double price = Double.parseDouble(obj.getString("r"));
            double volume = Double.parseDouble(obj.getString("q"));

            if(obj.getInt("n") > 0) {
                depth.addBid(volume, price);
            }
        }

        return depth;
    }



    //FIXME actually implement this
    @Override
    public AccountInfo getAccountInfo() {

        return new CoinseAccountInfo();
    }
}
