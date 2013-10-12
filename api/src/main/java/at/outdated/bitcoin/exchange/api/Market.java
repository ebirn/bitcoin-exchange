package at.outdated.bitcoin.exchange.api;

import at.outdated.bitcoin.exchange.api.container.CurrencyContainer;
import at.outdated.bitcoin.exchange.api.currency.Currency;
import at.outdated.bitcoin.exchange.api.market.transfer.TransferMethod;
import org.apache.commons.lang3.ArrayUtils;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

    protected Set<TransferMethod> withdrawals = new HashSet<>();
    protected Set<TransferMethod> deposits = new HashSet<>();

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

    public abstract Currency[] getFiatCurrencies();

    public abstract Currency[] getCryptoCurrencies();

    public Currency[] getAllCurrencies() {
        return  ArrayUtils.addAll(getFiatCurrencies(), getCryptoCurrencies());
    }

    //TODO actually implememt this: also: decide what should be implemented here,
    // what should be further service discorvery
    // exchange rate calculaters: service
    public abstract ExchangeApiClient getApiClient();


    public Set<TransferMethod> getWithdrawalMethods() {
        return withdrawals;
    }

    public Set<TransferMethod> getDepositMethods() {
        return deposits;
    }


    @Override
    public String toString() {
        return getKey().toUpperCase();
    }

}
