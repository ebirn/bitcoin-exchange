package at.outdated.bitcoin.exchange.api;

import at.outdated.bitcoin.exchange.api.currency.Currency;
import at.outdated.bitcoin.exchange.api.market.AssetPair;
import at.outdated.bitcoin.exchange.api.market.transfer.TransferMethod;
import org.apache.commons.lang3.ArrayUtils;

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
    protected Currency primaryCurrency;
    protected String key;

    protected Map<Currency,TransferMethod> withdrawals = new HashMap<>();
    protected Map<Currency,TransferMethod> deposits = new HashMap<>();

    protected Market(String key, String url, String description, Currency primaryCurrency) {
        this.key = key;
        this.url = url;
        this.description = description;
        this.primaryCurrency = primaryCurrency;
    }

    public String getUrl() {
        return url;
    }

    public String getDescription() {
        return description;
    }

    public Currency getPrimaryCurrency() {
        return primaryCurrency;
    }

    public String getKey() {
        return key;
    }

    public abstract AssetPair[] getTradedAssets();

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

    @Override
    public String toString() {
        return getKey().toUpperCase();
    }

}
