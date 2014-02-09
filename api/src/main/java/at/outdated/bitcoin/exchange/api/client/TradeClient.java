package at.outdated.bitcoin.exchange.api.client;

import at.outdated.bitcoin.exchange.api.OrderId;
import at.outdated.bitcoin.exchange.api.account.AccountInfo;
import at.outdated.bitcoin.exchange.api.account.Balance;
import at.outdated.bitcoin.exchange.api.account.WalletTransaction;
import at.outdated.bitcoin.exchange.api.currency.Currency;
import at.outdated.bitcoin.exchange.api.currency.CurrencyAddress;
import at.outdated.bitcoin.exchange.api.currency.CurrencyValue;
import at.outdated.bitcoin.exchange.api.market.AssetPair;
import at.outdated.bitcoin.exchange.api.market.MarketOrder;
import at.outdated.bitcoin.exchange.api.market.OrderType;
import at.outdated.bitcoin.exchange.api.market.fee.Fee;

import java.util.Date;
import java.util.List;

/**
 * Created by ebirn on 09.11.13.
 */
public interface TradeClient {

    public Balance getBalance();
    public List<WalletTransaction> getTransactions();

    //public void updateFees();
    public Fee getTradeFee(OrderType trade);
    public Fee getDepositFee(Currency curr);
    public Fee getWithdrawalFee(Currency curr);

    public List<MarketOrder> getOpenOrders();

    public OrderId placeOrder(MarketOrder order);
    public OrderId placeOrder(AssetPair asset, OrderType type, CurrencyValue volume, CurrencyValue price);

    public boolean cancelOrder(OrderId order);

    public CurrencyAddress getDepositAddress(Currency currency);
    public boolean withdrawFunds(CurrencyValue volume, CurrencyAddress destination);


}
