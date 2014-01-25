package at.outdated.bitcoin.exchange.api.account;

import at.outdated.bitcoin.exchange.api.OrderId;
import at.outdated.bitcoin.exchange.api.currency.CurrencyValue;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: ebirn
 * Date: 10.05.13
 * Time: 20:23
 * To change this template use File | Settings | File Templates.
 */
public class WalletTransaction implements Comparable<WalletTransaction> {



    private Date timestamp;

    private OrderId id;

    private String info;

    private TransactionType type;

    private CurrencyValue value;

    private CurrencyValue balance;


    public WalletTransaction() {

    }

    public WalletTransaction(TransactionType type, CurrencyValue volume) {
        this.type = type;
        this.value = volume;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setId(OrderId id) {
        this.id = id;
    }

    public OrderId getId() {
        return id;
    }

    public String getInfo() {
        return info;
    }

    public TransactionType getType() {
        return type;
    }

    public CurrencyValue getValue() {
        return value;
    }

    public CurrencyValue getBalance() {
        return balance;
    }

    @Override
    public String toString() {
        return type + ": " + value;
    }

    public void setValue(CurrencyValue value) {
        this.value = value;
    }

    public void setBalance(CurrencyValue balance) {
        this.balance = balance;
    }

    public void setType(TransactionType type) {
        this.type = type;
    }

    public void setTimestamp(Date datestamp) {
        this.timestamp = datestamp;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    @Override
    public int compareTo(WalletTransaction other) {
        return getTimestamp().compareTo(other.getTimestamp());
    }
}
