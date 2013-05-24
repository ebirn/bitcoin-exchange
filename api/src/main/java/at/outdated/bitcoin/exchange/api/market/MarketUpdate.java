package at.outdated.bitcoin.exchange.api.market;

import at.outdated.bitcoin.exchange.api.Market;
import at.outdated.bitcoin.exchange.api.currency.Currency;

import java.text.NumberFormat;

/**
 * Created with IntelliJ IDEA.
 * User: ebirn
 * Date: 10.05.13
 * Time: 22:53
 * To change this template use File | Settings | File Templates.
 */
public class MarketUpdate {

    private Market market;

    private TickerValue ticker;

    private double apiLag;

    private double orderLag;


    public MarketUpdate() {

    }

    public MarketUpdate(Market market, TickerValue value, double apiLag, double orderLag) {
        this.market = market;
        this.ticker = value;
        this.apiLag = apiLag;
        this.orderLag = orderLag;
    }

    public Market getMarket() {
        return market;
    }

    public void setMarket(Market market) {
        this.market = market;
    }

    public TickerValue getTicker() {
        return ticker;
    }

    public void setTicker(TickerValue ticker) {
        this.ticker = ticker;
    }

    public double getApiLag() {
        return apiLag;
    }

    public void setApiLag(double apiLag) {
        this.apiLag = apiLag;
    }

    public double getOrderLag() {
        return orderLag;
    }

    public void setOrderLag(double orderLag) {
        this.orderLag = orderLag;
    }

    public Currency getCurrency() {
        return getTicker().getCurrency();
    }

    @Override
    public String toString() {

        NumberFormat secFmt = NumberFormat.getInstance();
        return market + ": curr:" + ticker.getLast() + " " + getCurrency() + " tradeLag:" + secFmt.format(orderLag) + "s, apiLag:" + secFmt.format(apiLag) + "s";
    }
}
