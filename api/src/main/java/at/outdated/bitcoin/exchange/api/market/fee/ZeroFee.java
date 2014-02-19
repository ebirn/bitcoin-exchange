package at.outdated.bitcoin.exchange.api.market.fee;

/**
 * Created with IntelliJ IDEA.
 * User: ebirn
 * Date: 10.06.13
 * Time: 16:30
 * To change this template use File | Settings | File Templates.
 */
public class ZeroFee extends ConstantFee {

    public ZeroFee() {
        super("0.0");
    }

    @Override
    public String toString() {
        return super.toString() + " 0.";
    }
}
