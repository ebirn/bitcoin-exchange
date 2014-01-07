package at.outdated.bitcoin.exchange.cryptsy;

import at.outdated.bitcoin.exchange.api.account.AccountInfo;
import at.outdated.bitcoin.exchange.api.client.ExchangeApiClient;
import at.outdated.bitcoin.exchange.api.client.MarketClient;
import at.outdated.bitcoin.exchange.api.client.TradeClient;
import at.outdated.bitcoin.exchange.api.currency.Currency;
import at.outdated.bitcoin.exchange.api.market.AssetPair;
import at.outdated.bitcoin.exchange.api.market.Market;
import at.outdated.bitcoin.exchange.api.market.MarketDepth;
import at.outdated.bitcoin.exchange.api.market.TickerValue;

import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by ebirn on 06.01.14.
 */
public class CryptsyApiClient extends ExchangeApiClient implements MarketClient, TradeClient {

    Map<AssetPair,Integer> marketId = new HashMap<>();

    public CryptsyApiClient(Market market) {
        super(market);

        setMarketNum(Currency.LTC, Currency.BTC, 3); //3
        setMarketNum(Currency.NVC, Currency.BTC, 13); // 13
        setMarketNum(Currency.NMC, Currency.BTC, 29); // 29
        setMarketNum(Currency.PPC, Currency.BTC, 28); // 28
        setMarketNum(Currency.QRK, Currency.BTC, 71); // 71

        /*******************/

        setMarketNum(Currency.PPC, Currency.LTC, 125); // 125
        setMarketNum(Currency.QRK, Currency.LTC, 126); // 126

    }

    private void setMarketNum(Currency base, Currency quote, int marketNum) {
        AssetPair asset = market.getAsset(base, quote);
        marketId.put(asset, marketNum);
    }

    @Override
    public Number getLag() {
        return 0.1234;
    }

    @Override
    protected <T> Invocation.Builder setupProtectedResource(WebTarget res, Entity<T> entity) {
        return null;
    }

    @Override
    public TickerValue getTicker(AssetPair asset) {

        int marketNum = marketId.get(asset);

        WebTarget tgt = client.target("http://pubapi.cryptsy.com/api.php?method=singlemarketdata&marketid=" + marketNum);
        String raw = simpleGetRequest(tgt, String.class);
        JsonObject root = jsonFromString(raw);

        JsonObject jsonMarket = root.getJsonObject("return").getJsonObject("markets").getJsonObject(asset.getBase().name());

        JsonArray jsonSells = jsonMarket.getJsonArray("sellorders");

        JsonArray jsonBuys = jsonMarket.getJsonArray("buyorders");
        double bid = Double.parseDouble(jsonBuys.getJsonObject(0).getString("price"));
        double ask = Double.parseDouble(jsonSells.getJsonObject(0).getString("price"));


        double last = Double.parseDouble(jsonMarket.getString("lasttradeprice"));
        double volume = Double.parseDouble(jsonMarket.getString("volume"));


        TickerValue ticker = new TickerValue(asset);
        ticker.setLast(last);
        ticker.setVolume(volume);
        ticker.setBid(bid);
        ticker.setAsk(ask);

        return ticker;
    }

    @Override
    public MarketDepth getMarketDepth(AssetPair asset) {

        int marketNum = marketId.get(asset);
        // http://pubapi.cryptsy.com/api.php?method=singleorderdata&marketid=
        WebTarget tgt = client.target("http://pubapi.cryptsy.com/api.php?method=singleorderdata&marketid=" + marketNum);

        String raw = simpleGetRequest(tgt, String.class);
        try {
            JsonObject root = jsonFromString(raw);

            JsonObject jsonDepth = root.getJsonObject("return").getJsonObject(asset.getBase().name());


            MarketDepth depth = new MarketDepth(asset);

            JsonArray jsonSells = jsonDepth.getJsonArray("sellorders");
            for(int i=0; i<jsonSells.size(); i++) {
                JsonObject obj = jsonSells.getJsonObject(i);
                double price = Double.parseDouble(obj.getString("price"));
                double volume = Double.parseDouble(obj.getString("quantity"));

                depth.addAsk(volume, price);
            }

            JsonArray jsonBuys = jsonDepth.getJsonArray("buyorders");
            for(int i=0; i<jsonBuys.size(); i++) {
                JsonObject obj = jsonBuys.getJsonObject(i);
                double price = Double.parseDouble(obj.getString("price"));
                double volume = Double.parseDouble(obj.getString("quantity"));

                depth.addBid(volume, price);
            }
            return depth;
        }
        catch(Exception e) {
            log.error("failed to parse market depth", e);
        }

        return new MarketDepth(asset);
    }

    @Override
    public AccountInfo getAccountInfo() {
        return new CryptsyAccountInfo();
    }
}
