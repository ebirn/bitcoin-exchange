package at.outdated.bitcoin.exchange.btce;

import at.outdated.bitcoin.exchange.api.account.AccountInfo;

/**
 * Created with IntelliJ IDEA.
 * User: ebirn
 * Date: 26.05.13
 * Time: 23:44
 * To change this template use File | Settings | File Templates.
 */
public class BtcEAccountInfo extends AccountInfo {

    @Override
    public String getLogin() {
        return "btce";  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public double getTradeFeePercent() {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }
}
