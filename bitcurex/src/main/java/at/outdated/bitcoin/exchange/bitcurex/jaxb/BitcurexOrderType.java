package at.outdated.bitcoin.exchange.bitcurex.jaxb;

import javax.xml.bind.annotation.XmlEnum;

/**
 * Created by ebirn on 20.10.13.
 */
@XmlEnum
public enum BitcurexOrderType {
    UNKNOWN,
    ASK,
    BID
}
