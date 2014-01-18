package at.outdated.bitcoin.exchange.api.client;

import at.outdated.bitcoin.exchange.api.OrderId;
import at.outdated.bitcoin.exchange.api.currency.Currency;
import at.outdated.bitcoin.exchange.api.currency.CurrencyAddress;
import at.outdated.bitcoin.exchange.api.currency.CurrencyValue;
import at.outdated.bitcoin.exchange.api.market.*;
import at.outdated.bitcoin.exchange.api.market.transfer.TransferMethod;
import at.outdated.bitcoin.exchange.api.track.NumberTrack;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Created by ebirn on 18.01.14.
 */
public abstract class ExchangeClient implements MarketClient, TradeClient {
    protected Logger log = LoggerFactory.getLogger("client");
    protected NumberTrack apiLagTrack = new NumberTrack(5);
    protected Market market;

    public ExchangeClient(Market market) {
        log = LoggerFactory.getLogger("client." + market.getKey());
        this.market = market;
    }

    @Override
    public double getQuote(Currency base, Currency quote) {

        double rate = Double.NaN;

        AssetPair asset = market.getAsset(base, quote);

        if(asset != null) {
            TickerValue ticker = getTicker(asset);

            if(asset.getBase() == base) {
                rate = ticker.getBid();
            }
            else {
                rate = 1.0/ticker.getAsk();
            }
        }

        return rate;
    }

    public abstract Number getLag();

    final public double getApiLag() {
        return apiLagTrack.getStatistics().getGeometricMean();  //To change body of implemented methods use File | Settings | File Templates.
    }

    protected String getSecret() {
        return getPropertyString("secret");
    }

    protected String getUserId() {
       return getPropertyString("userid");
    }

    protected String getPropertyString(String key) {
        ResourceBundle bundle = ResourceBundle.getBundle("bitcoin-exchange");

        String fullKey = market.getKey() + "." + key;
        String value = bundle.getString(fullKey);

        return value;
    }

    protected CurrencyAddress lookupUpDepositAddress(Currency curr) {
        String addrString = null;

        addrString = getPropertyString("deposit."+curr.name().toLowerCase());

        return new CurrencyAddress(curr, addrString);
    }

    protected void sortDepth(MarketDepth depth) {
        OrderComparator comparator = new OrderComparator();

        comparator.setOrderType(OrderType.ASK);
        Collections.sort(depth.getAsks(), comparator);

        comparator.setOrderType(OrderType.BID);
        Collections.sort(depth.getBids(), comparator);
    }


    @Override
    public final CurrencyAddress getDepositAddress(Currency currency) {

        TransferMethod withdrawal = market.getWithdrawalMethod(currency);

        if(withdrawal == null) {
            throw new IllegalArgumentException("cannot withdraw " + currency);
        }

        // TODO: make this better
        if(withdrawal.getCurrency().isCrypto() == false) {
            throw new IllegalArgumentException("currently only crypto currencies are supported, NOT " + currency);
        }

        return lookupUpDepositAddress(currency);
    }



    @Override
    public String withdrawFunds(CurrencyValue volume, CurrencyAddress address) {

        if(volume == null || address == null) {
            throw new IllegalArgumentException("parameters must not be null.");
        }

        if(volume.getCurrency() != address.getReference()) {
            throw new IllegalArgumentException("Currency mismatch");
        }

        return null;
        //return performFundWithdrawal(volume, address);
    }





    //FIXME: remove these
    @Override
    public List<MarketOrder> getOpenOrders() {
        return null;
    }

    //FIXME: remove these
    @Override
    public OrderId placeOrder(AssetPair asset, TradeDecision decision, CurrencyValue volume, CurrencyValue price) {
        return null;
    }

    //FIXME: remove these
    @Override
    public boolean cancelOrder(OrderId order) {
        return false;
    }
}
