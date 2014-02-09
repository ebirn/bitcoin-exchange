package at.outdated.bitcoin.exchange.btce;

import at.outdated.bitcoin.exchange.api.OrderId;
import at.outdated.bitcoin.exchange.api.currency.Currency;
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
public class BtceTrade {
/*
    {"date":1391940472,"price":701.39,"amount":0.01,"tid":29283411,"price_currency":"USD","item":"BTC","trade_type":"ask"},
    {"date":1391940441,"price":698.213,"amount":0.13,"tid":29283401,"price_currency":"USD","item":"BTC","trade_type":"ask"},{
    */

    @XmlJavaTypeAdapter(UnixTimeDateAdapter.class)
    Date date;

    BigDecimal price;

    BigDecimal amount;

    long tid;

    Currency price_currency;

    Currency item;

    @XmlJavaTypeAdapter(OrderTypeAdapter.class)
    OrderType trade_type;

    public MarketOrder getOrder(Market m) {
        MarketOrder order = new MarketOrder();

        order.setId(new OrderId(m, Long.toString(tid)));

        order.setAsset(new AssetPair(item, price_currency));
        order.setTimestamp(date);

        order.setPrice(price);
        order.setVolume(amount);

        order.setType(trade_type);

        return order;
    }
}
