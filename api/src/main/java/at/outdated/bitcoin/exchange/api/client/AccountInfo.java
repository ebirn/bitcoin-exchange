package at.outdated.bitcoin.exchange.api.client;

import at.outdated.bitcoin.exchange.api.account.Wallets;

/**
 * Created with IntelliJ IDEA.
 * User: ebirn
 * Date: 24.05.13
 * Time: 17:09
 * To change this template use File | Settings | File Templates.
 */
public interface AccountInfo {

    public String getLogin();

    public double getTradeFee();

    public Wallets getWallets();
}
