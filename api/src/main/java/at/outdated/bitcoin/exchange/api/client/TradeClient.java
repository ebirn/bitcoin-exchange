package at.outdated.bitcoin.exchange.api.client;

import at.outdated.bitcoin.exchange.api.OrderId;
import at.outdated.bitcoin.exchange.api.account.AccountInfo;
import at.outdated.bitcoin.exchange.api.currency.Currency;
import at.outdated.bitcoin.exchange.api.currency.CurrencyAddress;
import at.outdated.bitcoin.exchange.api.currency.CurrencyValue;
import at.outdated.bitcoin.exchange.api.market.AssetPair;
import at.outdated.bitcoin.exchange.api.market.MarketOrder;
import at.outdated.bitcoin.exchange.api.market.OrderType;
import at.outdated.bitcoin.exchange.api.market.fee.Fee;

import java.util.List;

/**
 * Created by ebirn on 09.11.13.
 */
public interface TradeClient {

    // TODO: refactor AccountInfo directly into this interface
    public AccountInfo getAccountInfo();

    //public void getBalance();
    // public void getOpenBalance();
    // public void getOrders(Date since);

    public Fee getTradeFee(OrderType trade);
    public Fee getDepositFee();
    public Fee getWithdrawalFee(Currency curr);

    public List<MarketOrder> getOpenOrders();

    public OrderId placeOrder(MarketOrder order);
    public OrderId placeOrder(AssetPair asset, OrderType type, CurrencyValue volume, CurrencyValue price);

    public boolean cancelOrder(OrderId order);

    public CurrencyAddress getDepositAddress(Currency currency);
    public boolean withdrawFunds(CurrencyValue volume, CurrencyAddress address);


}
