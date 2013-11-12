package at.outdated.bitcoin.exchange.api.market;

import at.outdated.bitcoin.exchange.api.client.ExchangeApiClient;
import at.outdated.bitcoin.exchange.api.currency.Currency;
import at.outdated.bitcoin.exchange.api.market.AssetPair;
import at.outdated.bitcoin.exchange.api.market.transfer.TransferMethod;

import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: ebirn
 * Date: 10.05.13
 * Time: 22:53
 * To change this template use File | Settings | File Templates.
 */
public abstract class Market {
    /*
    MTGOX("http://www.mtgox.com/", "Mt.Gox", Currency.EUR, "mtgox"),
    BTCE("http://btc-e.com", "BTC-E Bitcoin Exchange", Currency.EUR, "btce"),
    BTCDE("https://www.bitcoin.de/", "bitcoin.de", Currency.EUR, "btcde"),
    BITSTAMP("https://www.bitstamp.net/", "Bitstamp", Currency.USD, "bitstamp");
*/

    protected String url;
    protected String description;
    protected String key;

    protected Map<Currency,TransferMethod> withdrawals = new HashMap<>();
    protected Map<Currency,TransferMethod> deposits = new HashMap<>();

    protected Set<AssetPair> assets = new HashSet<>();

    protected Market(String key, String url, String description) {
        this.key = key;
        this.url = url;
        this.description = description;
    }

    public String getUrl() {
        return url;
    }

    public String getDescription() {
        return description;
    }

    public String getKey() {
        return key;
    }

    public Set<AssetPair> getTradedAssets() {
        return assets;
    }

    //TODO actually implememt this: also: decide what should be implemented here,
    // what should be further service discorvery
    // exchange rate calculaters: service
    public abstract ExchangeApiClient getApiClient();


    public Collection<TransferMethod> getWithdrawalMethods() {
        return withdrawals.values();
    }
    public TransferMethod getWithdrawalMethod(Currency currency) {
        return withdrawals.get(currency);
    }

    public Collection<TransferMethod> getDepositMethods() {
        return deposits.values();
    }
    public TransferMethod getDepositMethod(Currency currency) {
        return deposits.get(currency);
    }


    protected void addWithdrawal(TransferMethod method) {
        withdrawals.put(method.getCurrency(), method);
    }

    protected void addDeposit(TransferMethod method) {
        deposits.put(method.getCurrency(), method);
    }

    protected void addAsset(Currency base, Currency quote) {
        assets.add(new AssetPair(base, quote));
    }


    // find asset pair that contains both currencies, in whatever order
    public AssetPair getAsset(Currency one, Currency other) {

        AssetPair candidate = null;

        for(AssetPair ap : assets) {
            // exact match, return
            if(ap.equals(new AssetPair(one, other))) return ap;
            // set candidate, because we iterate in unknown order, full match may come later
            else if(ap.equals(new AssetPair(other, one))) return candidate = ap;

            //if(ap.getBase() == one && ap.getQuote() == other) return ap;
            //if(ap.isMember(one) && ap.isMember(other)) candidate = ap;
        }

        return candidate;
    }

    @Override
    public String toString() {
        return getKey().toUpperCase();
    }

}
