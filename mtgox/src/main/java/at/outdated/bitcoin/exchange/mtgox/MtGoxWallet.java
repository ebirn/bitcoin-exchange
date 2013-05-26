package at.outdated.bitcoin.exchange.mtgox;

import at.outdated.bitcoin.exchange.api.account.Wallet;
import at.outdated.bitcoin.exchange.api.currency.Currency;
import at.outdated.bitcoin.exchange.api.currency.CurrencyValue;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

/**
 * Created with IntelliJ IDEA.
 * User: ebirn
 * Date: 03.05.13
 * Time: 14:05
 * To change this template use File | Settings | File Templates.
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class MtGoxWallet extends Wallet {

    @XmlElement(name="Operations")
    private long transactionCount;

    @XmlElement(name="Daily_Withdraw_Limit")
    private CurrencyValue Daily_Withdraw_Limit;

    @XmlElement(name="Monthly_Withdraw_Limit")
    private CurrencyValue Monthly_Withdraw_Limit;

    @XmlElement(name="Max_Withdraw")
    private CurrencyValue Max_Withdraw;

    @XmlElement(name="Balance")
    protected CurrencyValue balance;

    @XmlElement(name="Open_Orders")
    private CurrencyValue openOrders;


    @Override
    public Currency getCurrency() {
        this.currency = balance.getCurrency();
        return balance.getCurrency();
    }

    public CurrencyValue getBalance() {
        return balance;
    }

    public CurrencyValue getOpenOrders() {
        return openOrders;
    }

    public long getTransactionCount() {
        return transactionCount;
    }


    public CurrencyValue getDaily_Withdraw_Limit() {
        return Daily_Withdraw_Limit;
    }

    public CurrencyValue getMonthly_Withdraw_Limit() {
        return Monthly_Withdraw_Limit;
    }

    public CurrencyValue getMax_Withdraw() {
        return Max_Withdraw;
    }

}
