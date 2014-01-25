package at.outdated.bitcoin.exchange.bitstamp;

import at.outdated.bitcoin.exchange.api.jaxb.StringBigDecimalAdapter;
import at.outdated.bitcoin.exchange.api.jaxb.StringNumberAdapter;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.math.BigDecimal;

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

    @XmlElement @XmlJavaTypeAdapter(StringBigDecimalAdapter.class)
    BigDecimal fee;

    @XmlElement(name="usd_balance") @XmlJavaTypeAdapter(StringBigDecimalAdapter.class)
    BigDecimal usdBalance;

    @XmlElement(name="usd_reserved") @XmlJavaTypeAdapter(StringBigDecimalAdapter.class)
    BigDecimal usdReserved;

    @XmlElement(name="usd_available") @XmlJavaTypeAdapter(StringBigDecimalAdapter.class)
    BigDecimal usdAvailable;



    @XmlElement(name="btc_balance") @XmlJavaTypeAdapter(StringBigDecimalAdapter.class)
    BigDecimal btcBalance;

    @XmlElement(name="btc_reserved") @XmlJavaTypeAdapter(StringBigDecimalAdapter.class)
    BigDecimal btcReserved;


    @XmlElement(name="btc_available") @XmlJavaTypeAdapter(StringBigDecimalAdapter.class)
    BigDecimal btcAvailable;


    public BigDecimal getBtcReserved() {
        return btcReserved;
    }

    public BigDecimal getUsdBalance() {
        return usdBalance;
    }

    public BigDecimal getFee() {
        return fee;
    }

    public BigDecimal getUsdAvailable() {
        return usdAvailable;
    }

    public BigDecimal getBtcBalance() {
        return btcBalance;
    }

    public BigDecimal getUsdReserved() {
        return usdReserved;
    }

    public BigDecimal getBtcAvailable() {
        return btcAvailable;
    }
}
