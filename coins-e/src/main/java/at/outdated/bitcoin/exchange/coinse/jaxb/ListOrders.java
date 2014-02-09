package at.outdated.bitcoin.exchange.coinse.jaxb;

import javax.xml.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ebirn on 30.01.14.
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class ListOrders extends BaseResponse {
/*
    {
        "status": true,
            "message": "success",
            "next_cursor": "E-ABAOsB8gEHY3JlYXRlZPoBCQiI-ab9jJO4AuwBggJiagtkZXZ-Qw",
            "orders": [
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

    @XmlElement(name = "next_cursor")
    String nextCursor;

    @XmlElements({
        @XmlElement(name="orders"),
        @XmlElement(name="trades")
    })
    List<CoinseOrder> orders = new ArrayList<>();

    public String getNextCursor() {
        return nextCursor;
    }

    public List<CoinseOrder> getOrders() {
        return orders;
    }
}
