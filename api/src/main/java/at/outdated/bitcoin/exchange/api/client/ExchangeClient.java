package at.outdated.bitcoin.exchange.api.client;

import at.outdated.bitcoin.exchange.api.OrderId;
import at.outdated.bitcoin.exchange.api.currency.Currency;
import at.outdated.bitcoin.exchange.api.currency.CurrencyAddress;
import at.outdated.bitcoin.exchange.api.currency.CurrencyValue;
import at.outdated.bitcoin.exchange.api.market.*;
import at.outdated.bitcoin.exchange.api.market.fee.Fee;
import at.outdated.bitcoin.exchange.api.market.fee.InfiniteFee;
import at.outdated.bitcoin.exchange.api.market.fee.ZeroFee;
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

    protected Fee tradeFee = new InfiniteFee();

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
    public boolean withdrawFunds(CurrencyValue volume, CurrencyAddress address) {

        if(volume == null || address == null) {
            throw new IllegalArgumentException("parameters must not be null.");
        }

        if(volume.getCurrency() != address.getReference()) {
            throw new IllegalArgumentException("Currency mismatch");
        }

        return false;
        //return performFundWithdrawal(volume, address);
    }


    @Override
    public OrderId placeOrder(MarketOrder order) {
        return placeOrder(order.getAsset(), order.getDecision(), order.getVolume(), order.getPrice());
    }

    @Override
    public Fee getDepositFee() {
        return new ZeroFee();
    }

    @Override
    public Fee getWithdrawalFee(Currency curr) {
        return this.market.getDepositMethod(curr).getFee();
    }

    @Override
    public Fee getTradeFee(TradeDecision trade) {
        return tradeFee;
    }
}
