package at.outdated.bitcoin.exchange.bter;

import at.outdated.bitcoin.exchange.api.OrderId;
import at.outdated.bitcoin.exchange.api.jaxb.OrderTypeAdapter;
import at.outdated.bitcoin.exchange.api.jaxb.UnixTimeDateAdapter;
import at.outdated.bitcoin.exchange.api.market.AssetPair;
import at.outdated.bitcoin.exchange.api.market.Market;
import at.outdated.bitcoin.exchange.api.market.MarketOrder;
import at.outdated.bitcoin.exchange.api.market.OrderType;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by ebirn on 09.02.14.
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class BterTrade {

    // {"date":"1391940727","price":4576,"amount":0.1962,"tid":"4422029","type":"buy"},

    @XmlJavaTypeAdapter(UnixTimeDateAdapter.class)
    Date date;


    BigDecimal price;

    BigDecimal amount;

    String tid;

    @XmlJavaTypeAdapter(OrderTypeAdapter.class)
    OrderType type;

    public MarketOrder getOrder(Market m, AssetPair asset) {

        MarketOrder o = new MarketOrder();
        o.setAsset(asset);

        o.setId(new OrderId(m, tid));
        o.setType(type);
        o.setTimestamp(date);

        o.setVolume(amount);
        o.setPrice(price);

        return o;
    }
}
