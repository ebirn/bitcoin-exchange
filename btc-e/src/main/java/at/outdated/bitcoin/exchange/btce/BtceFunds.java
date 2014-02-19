package at.outdated.bitcoin.exchange.btce;

import at.outdated.bitcoin.exchange.api.jaxb.StringBigDecimalAdapter;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.math.BigDecimal;

/**
 * Created by ebirn on 06.10.13.
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class BtceFunds {

    @XmlJavaTypeAdapter(StringBigDecimalAdapter.class)
    BigDecimal usd;

    @XmlJavaTypeAdapter(StringBigDecimalAdapter.class)
    BigDecimal btc;

    @XmlJavaTypeAdapter(StringBigDecimalAdapter.class)
    BigDecimal ltc;

    @XmlJavaTypeAdapter(StringBigDecimalAdapter.class)
    BigDecimal nmc;

    @XmlJavaTypeAdapter(StringBigDecimalAdapter.class)
    BigDecimal rur;

    @XmlJavaTypeAdapter(StringBigDecimalAdapter.class)
    BigDecimal eur;

    @XmlJavaTypeAdapter(StringBigDecimalAdapter.class)
    BigDecimal nvc;

    @XmlJavaTypeAdapter(StringBigDecimalAdapter.class)
    BigDecimal trc;

    @XmlJavaTypeAdapter(StringBigDecimalAdapter.class)
    BigDecimal ppc;

    @XmlJavaTypeAdapter(StringBigDecimalAdapter.class)
    BigDecimal ftc;
}
