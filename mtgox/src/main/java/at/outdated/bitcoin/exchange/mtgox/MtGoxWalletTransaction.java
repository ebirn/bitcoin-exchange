package at.outdated.bitcoin.exchange.mtgox;

import at.outdated.bitcoin.exchange.api.account.TransactionType;
import at.outdated.bitcoin.exchange.api.account.Wallet;
import at.outdated.bitcoin.exchange.api.account.WalletTransaction;
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
@XmlAccessorType(XmlAccessType.FIELD)
public class MtGoxWalletTransaction extends WalletTransaction {

    /*
        "Index": "16",
        "Date": 1366218001,
        "Type": "deposit",
        "Value":   **Currency Object**,
        "Balance": **Currency Object**,
        "Info": "1BitcoinAddress789fhjka890jkl",
        "Link":[
        "123456789-0abc-def0-1234-567890abcdef",
        "Money_Bitcoin_Block_Tx_Out",
        "1BitcoinTransaction780gfsd8970fg:9"]
    */

    @XmlElement(name = "Date")
    private long datestamp;

    @XmlElement(name = "Info")
    private String info;


    @XmlElement(name="Type")
    private TransactionType type;

    @XmlElement(name="Value")
    private CurrencyValue value;

    @XmlElement(name="Balance")
    private CurrencyValue balance;


    public MtGoxWalletTransaction() {
        this.datestamp = (new Date()).getTime()/1000L;
    }

    public MtGoxWalletTransaction(TransactionType type, CurrencyValue volume) {
        this.datestamp = (new Date()).getTime()/1000L;

        this.type = type;
        this.value = volume;

    }

    public Date getTimestamp() {
        Date ts = new Date();
        ts.setTime(this.datestamp * 1000L);

        return ts;
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

    public void setDatestamp(Date datestamp) {
        this.datestamp = datestamp.getTime()/1000L;
    }

    public void setInfo(String info) {
        this.info = info;
    }

}
