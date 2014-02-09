package at.outdated.bitcoin.exchange.cryptsy;

import at.outdated.bitcoin.exchange.api.OrderId;
import at.outdated.bitcoin.exchange.api.market.AssetPair;
import at.outdated.bitcoin.exchange.api.market.Market;
import at.outdated.bitcoin.exchange.api.market.MarketOrder;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by ebirn on 09.02.14.
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class CryptsyTrade {
/*

tradeid	A unique ID for the trade
datetime	Server datetime trade occurred
tradeprice	The price the trade occurred at
quantity	Quantity traded
total	Total value of trade (tradeprice * quantity)
initiate_ordertype	The type of order which initiated this trade
 */

    String tradeid;

    Date datetime;

    BigDecimal tradeprice;

    BigDecimal quantity;

    BigDecimal total;

    String initiate_ordertype;



    public MarketOrder getOrder(Market m, AssetPair asset) {

        MarketOrder order = new MarketOrder(new OrderId(m, tradeid));

        order.setAsset(asset);
        order.setTimestamp(datetime);

        order.setPrice(tradeprice);
        order.setVolume(quantity);

        //TODO: set order type based on initiate_ordertype value

        return order;
    }
}
