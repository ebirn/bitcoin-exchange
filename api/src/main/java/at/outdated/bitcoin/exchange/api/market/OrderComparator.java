package at.outdated.bitcoin.exchange.api.market;

import at.outdated.bitcoin.exchange.api.currency.CurrencyValue;

import java.util.Comparator;

/**
 * Created by ebirn on 11.01.14.
 */
public class OrderComparator implements Comparator<MarketOrder> {

    OrderType orderType;

    public OrderComparator() {
    }

    public OrderComparator(OrderType orderType) {
        this.orderType = orderType;
    }

    public void setOrderType(OrderType type) {
        this.orderType = type;
    }


    @Override
    public int compare(MarketOrder o1, MarketOrder o2) {

        switch (orderType) {
            case  ASK:
                return compareAsk(o1.getPrice(), o2.getPrice());

            case BID:
                return compareBid(o1.getPrice(), o2.getPrice());
        }

        return 0;
    }


    // sort ascending
    private int compareAsk(CurrencyValue price1, CurrencyValue price2) {

        if(price1.getValue() == price2.getValue()) return 0;

        if(price1.isMoreThan(price2)) {
            return 1;
        }

        return -1;
    }

    // sort descending
    private int compareBid(CurrencyValue price1, CurrencyValue price2) {

        if(price1.getValue() == price2.getValue()) return 0;

        if(price1.isLessThan(price2)) {
            return 1;
        }
        return -1;
    }
}
