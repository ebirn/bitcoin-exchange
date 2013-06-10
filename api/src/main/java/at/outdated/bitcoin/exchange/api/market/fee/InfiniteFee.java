package at.outdated.bitcoin.exchange.api.market.fee;

/**
 * Created with IntelliJ IDEA.
 * User: ebirn
 * Date: 10.06.13
 * Time: 16:31
 * To change this template use File | Settings | File Templates.
 */
public class InfiniteFee extends ConstantFee {

    public InfiniteFee() {
        super(Double.MAX_VALUE);
    }
}
