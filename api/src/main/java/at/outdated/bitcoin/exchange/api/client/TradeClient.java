package at.outdated.bitcoin.exchange.api.client;

import at.outdated.bitcoin.exchange.api.account.AccountInfo;
import at.outdated.bitcoin.exchange.api.currency.Currency;
import at.outdated.bitcoin.exchange.api.currency.CurrencyAddress;
import at.outdated.bitcoin.exchange.api.currency.CurrencyValue;

/**
 * Created by ebirn on 09.11.13.
 */
public interface TradeClient {

    public AccountInfo getAccountInfo();


    //public List<Order> getPendingOrders();
    //public String placeOrder();
    //public String cancelOrder();

    public CurrencyAddress getDepositAddress(Currency currency);
    //public String withdrawFunds(CurrencyValue volume, CurrencyAddress address);

}
