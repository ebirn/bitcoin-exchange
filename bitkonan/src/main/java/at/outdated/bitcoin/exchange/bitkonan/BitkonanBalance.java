package at.outdated.bitcoin.exchange.bitkonan;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by ebirn on 05.10.13.
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class BitkonanBalance {

    /*
    usd_balance - USD balance
    btc_balance - BTC balance
    usd_reserved - USD reserved in open orders
    btc_reserved - BTC reserved in open orders
    usd_available- USD available for trading
    btc_available - BTC available for trading
     */

    @XmlElement
    double usd_balance;

    @XmlElement
    double btc_balance;

    @XmlElement
    double usd_reserved;

    @XmlElement
    double btc_reserved;

    @XmlElement
    double usd_available;

    @XmlElement
    double btc_available;

}
