package at.outdated.bitcoin.exchange.api.market.transfer;

import at.outdated.bitcoin.exchange.api.account.TransactionType;
import at.outdated.bitcoin.exchange.api.currency.Currency;
import at.outdated.bitcoin.exchange.api.market.fee.Fee;
import at.outdated.bitcoin.exchange.api.market.fee.ZeroFee;

/**
 * Created by ebirn on 12.10.13.
 */
public class TransferMethod {

    protected Fee fee = new ZeroFee();

    protected Currency currency;

    protected TransactionType type;

    protected  String address;

    public TransferMethod() {

    }

    public TransferMethod(Currency currency, TransactionType type, String address) {
        setCurrency(currency);
        setType(type);
        setAddress(address);
    }

    public Fee getFee() {
        return fee;
    }

    public Currency getCurrency() {
        return currency;
    }

    public TransactionType getType() {
        return type;
    }

    public void setFee(Fee fee) {
        this.fee = fee;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }

    public void setType(TransactionType type) {

        switch (type) {
            case DEPOSIT:
            case WITHDRAW:
                this.type = type;
                break;

            default:
                throw new IllegalArgumentException("type " + type + " not allowed");
        }
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
