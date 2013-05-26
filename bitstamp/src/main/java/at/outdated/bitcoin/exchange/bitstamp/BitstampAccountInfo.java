package at.outdated.bitcoin.exchange.bitstamp;

import at.outdated.bitcoin.exchange.api.account.AccountInfo;

/**
 * Created with IntelliJ IDEA.
 * User: ebirn
 * Date: 26.05.13
 * Time: 17:01
 * To change this template use File | Settings | File Templates.
 */
public class BitstampAccountInfo extends AccountInfo {

    @Override
    public String getLogin() {
        return "blubb";  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public double getTradeFeePercent() {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
