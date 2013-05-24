package at.outdated.bitcoin.exchange.mtgox.wallet;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;

/**
 * Created with IntelliJ IDEA.
 * User: ebirn
 * Date: 10.05.13
 * Time: 20:32
 * To change this template use File | Settings | File Templates.
 */

@XmlEnum
public enum TransactionType {
    @XmlEnumValue("withdraw") WITHDRAW, // Money being withdrawn (eg to bank account)
    @XmlEnumValue("deposit") DEPOSIT, // Incoming deposit of currency
    @XmlEnumValue("in") IN, // BTC gained after a bid order
    @XmlEnumValue("spent") SPENT, // Auxiliary currency removed after a bid order
    @XmlEnumValue("out") OUT, // BTC removed after an ask order
    @XmlEnumValue("earned") EARNED, // Auxiliary currency gained after an ask order
    @XmlEnumValue("fee") FEE; //Fee deducted from balance, e.g. during an order. The currency from which the fee is taken depends on the type of order and whether you specified for the fee to be taken from your order or added on top.
}
