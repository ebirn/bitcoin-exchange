package at.outdated.bitcoin.exchange.api.market.transfer;

import at.outdated.bitcoin.exchange.api.currency.Currency;
import at.outdated.bitcoin.exchange.api.currency.CurrencyAddress;
import at.outdated.bitcoin.exchange.api.market.fee.Fee;
import at.outdated.bitcoin.exchange.api.market.fee.ZeroFee;

/**
 * Created by ebirn on 12.10.13.
 */
public class TransferMethod {

    protected Fee fee = new ZeroFee();

    protected Currency currency;

    protected TransferType transfer = TransferType.OTHER;

    protected CurrencyAddress address;

    public TransferMethod() {
    }

    public TransferMethod(Currency currency, TransferType transfer, CurrencyAddress address) {
        setCurrency(currency);
        setTransfer(transfer);
        setAddress(address);
    }

    public TransferMethod(Currency currency, TransferType transfer, CurrencyAddress address, Fee fee) {
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

    public CurrencyAddress getAddress() {
        return address;
    }

    public void setAddress(CurrencyAddress address) {

        if(address != null && address.getReference() != currency) {
            throw new IllegalArgumentException("currency address does not match currency");
        }

        this.address = address;
    }

    @Override
    public String toString() {
        return transfer + ":" + currency;
    }
}
