package at.outdated.bitcoin.exchange.bitstamp;

import at.outdated.bitcoin.exchange.api.jaxb.StringNumberAdapter;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

/**
 * Created with IntelliJ IDEA.
 * User: ebirn
 * Date: 25.05.13
 * Time: 16:23
 * To change this template use File | Settings | File Templates.
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class BitstampAccountBalance {
/*
    {
        "btc_reserved": "0",
            "usd_balance": "0.00",
            "fee": "0.5000",
            "usd_available": "0.00",
            "btc_balance": "0",
            "usd_reserved": "0",
            "btc_available": "0"
    }
    */

    @XmlElement @XmlJavaTypeAdapter(StringNumberAdapter.class)
    Number fee;

    @XmlElement(name="usd_balance") @XmlJavaTypeAdapter(StringNumberAdapter.class)
    Number usdBalance;

    @XmlElement(name="usd_reserved") @XmlJavaTypeAdapter(StringNumberAdapter.class)
    Number usdReserved;

    @XmlElement(name="usd_available") @XmlJavaTypeAdapter(StringNumberAdapter.class)
    Number usdAvailable;



    @XmlElement(name="btc_balance") @XmlJavaTypeAdapter(StringNumberAdapter.class)
    Number btcBalance;

    @XmlElement(name="btc_reserved") @XmlJavaTypeAdapter(StringNumberAdapter.class)
    Number btcReserved;


    @XmlElement(name="btc_available") @XmlJavaTypeAdapter(StringNumberAdapter.class)
    Number btcAvailable;


    public Number getBtcReserved() {
        return btcReserved;
    }

    public Number getUsdBalance() {
        return usdBalance;
    }

    public Number getFee() {
        return fee;
    }

    public Number getUsdAvailable() {
        return usdAvailable;
    }

    public Number getBtcBalance() {
        return btcBalance;
    }

    public Number getUsdReserved() {
        return usdReserved;
    }

    public Number getBtcAvailable() {
        return btcAvailable;
    }
}
