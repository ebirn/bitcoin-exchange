package at.outdated.bitcoin.exchange.api.market;

/**
 * Created with IntelliJ IDEA.
 * User: ebirn
 * Date: 10.05.13
 * Time: 20:09
 *
 * http://en.wikipedia.org/wiki/Ask_price
 * http://en.wikipedia.org/wiki/Bid_price
 */
public enum OrderType {
    BID("buy"), // BUY, A bid price is the highest price that a buyer (i.e., bidder) is willing to pay for a good.
    ASK("sell"), // SELL, Ask price, also called offer price, offer, asking price, or simply ask, is the price a seller states she or he will accept for a good.
    UNDEF("unknown") //no mans land value
    ;


    private OrderType(String v) {
        this.verb = v;
    }
    String verb;

    public String verb() {
        return verb;
    }

    public static OrderType parse(String str) {
        OrderType parsed = UNDEF;

        str = str.toLowerCase();
        switch(str) {
            case "buy":
            case "bid":
                parsed = BID;
                break;

            case "sell":
            case "ask":
                parsed = ASK;
                break;
        }

        return parsed;
    }
}
