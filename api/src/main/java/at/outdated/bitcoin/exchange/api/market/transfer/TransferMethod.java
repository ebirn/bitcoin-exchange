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

    protected TransferType transfer = TransferType.OTHER;

    protected  String address;

    public TransferMethod() {
    }

    public TransferMethod(Currency currency, TransferType transfer, String address) {
        setCurrency(currency);
        setTransfer(transfer);
        setAddress(address);
    }

    public TransferMethod(Currency currency, TransferType transfer, String address, Fee fee) {
        setCurrency(currency);
        setTransfer(transfer);
        setAddress(address);
        setFee(fee);
    }

    public Fee getFee() {
        return fee;
    }

    public Currency getCurrency() {
        return currency;
    }


    public void setFee(Fee fee) {
        this.fee = fee;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }

    public TransferType getTransfer() {
        return transfer;
    }

    public void setTransfer(TransferType transfer) {
        this.transfer = transfer;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    @Override
    public String toString() {
        return transfer + ":" + currency;
    }
}
