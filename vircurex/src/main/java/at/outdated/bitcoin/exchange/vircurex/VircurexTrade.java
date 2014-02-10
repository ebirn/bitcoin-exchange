package at.outdated.bitcoin.exchange.vircurex;

import at.outdated.bitcoin.exchange.api.OrderId;
import at.outdated.bitcoin.exchange.api.jaxb.UnixTimeDateAdapter;
import at.outdated.bitcoin.exchange.api.market.AssetPair;
import at.outdated.bitcoin.exchange.api.market.Market;
import at.outdated.bitcoin.exchange.api.market.MarketOrder;
import at.outdated.bitcoin.exchange.api.market.OrderType;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by ebirn on 09.02.14.
 */
@XmlRootElement
public class VircurexTrade {

    /*
    [{"date":1391011966,"tid":1331206,"amount":"1.0","price":"0.02669999"},
    {"date":1391011966,"tid":1331208,"amount":"2.9995","price":"0.0267"},
     */

    @XmlElement
    int tid;

    @XmlElement
    @XmlJavaTypeAdapter(UnixTimeDateAdapter.class)
    Date date;

    @XmlElement
    BigDecimal amount;

    @XmlElement
    BigDecimal price;

    public MarketOrder getOrder(Market m, AssetPair asset) {

        MarketOrder order = new MarketOrder();

        order.setId(new OrderId(m, Integer.toString(tid)));
        order.setAsset(asset);

        order.setVolume(amount);
        order.setPrice(price);

        order.setTimestamp(date);

        order.setType(OrderType.UNDEF);

        return order;
    }
}
