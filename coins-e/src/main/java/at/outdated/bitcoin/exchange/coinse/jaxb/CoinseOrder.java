package at.outdated.bitcoin.exchange.coinse.jaxb;

import at.outdated.bitcoin.exchange.api.OrderId;
import at.outdated.bitcoin.exchange.api.currency.Currency;
import at.outdated.bitcoin.exchange.api.currency.CurrencyValue;
import at.outdated.bitcoin.exchange.api.jaxb.UnixTimeDateAdapter;
import at.outdated.bitcoin.exchange.api.market.AssetPair;
import at.outdated.bitcoin.exchange.api.market.Market;
import at.outdated.bitcoin.exchange.api.market.MarketOrder;
import at.outdated.bitcoin.exchange.api.market.OrderType;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by ebirn on 30.01.14.
 */
public class CoinseOrder {
    /*
            {
            "status": "cancelled",
                "order_type": "buy",
                "created": 1372847281,
                "quantity_remaining": "0.0",
                "fee_rate": "0.00300000",
                "rate": "0.00212300",
                "is_open": false,
                "pair": "WDC_BTC",
                "id": "B/0.00212300/6643661571883008",
                "quantity": "1.00000000"
        },
        */

    @XmlElement
    String status;

    @XmlElement
    String order_type;

    @XmlElement
    @XmlJavaTypeAdapter(UnixTimeDateAdapter.class)
    Date created;

    @XmlElement
    BigDecimal quantity_remaining;

    @XmlElement
    BigDecimal fee_rate;

    @XmlElement
    BigDecimal rate;

    @XmlElement
    BigDecimal quantity;

    @XmlElement
    boolean is_open;

    @XmlElement
    String pair;

    @XmlElement
    String id;

    @XmlElement
    String buy_order_no;

    @XmlElement
    String sell_order_no;

    public Date getCreated() {
        return created;
    }

    private AssetPair parseAsset(String raw) {

        String[] parts = raw.split("_");

        return new AssetPair(Currency.valueOf(parts[0]), Currency.valueOf(parts[1]));
    }

    public MarketOrder getOrder(Market market) {
        MarketOrder order = new MarketOrder();

        AssetPair asset = parseAsset(pair);

        order.setId(new OrderId(market, id));

        order.setAsset(asset);
        order.setTimestamp(created);

        order.setVolume(new CurrencyValue(quantity, asset.getBase()));
        order.setPrice(new CurrencyValue(rate, asset.getQuote()));


        if(order_type != null) {
            if(order_type.equalsIgnoreCase("buy")) {
                order.setType(OrderType.BID);
            }
            else if(order_type.equalsIgnoreCase("sell")) {
                order.setType(OrderType.ASK);
            }
            else {
                order.setType(OrderType.UNDEF);
            }
        }
        else if (buy_order_no != null) {
            order.setType(OrderType.BID);
        }
        else if(sell_order_no != null) {
            order.setType(OrderType.ASK);
        }

        return order;
    }
}
