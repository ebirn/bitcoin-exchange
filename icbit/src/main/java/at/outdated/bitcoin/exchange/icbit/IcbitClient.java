package at.outdated.bitcoin.exchange.icbit;

import at.outdated.bitcoin.exchange.api.OrderId;
import at.outdated.bitcoin.exchange.api.account.Balance;
import at.outdated.bitcoin.exchange.api.account.WalletTransaction;
import at.outdated.bitcoin.exchange.api.client.RestExchangeClient;
import at.outdated.bitcoin.exchange.api.currency.CurrencyValue;
import at.outdated.bitcoin.exchange.api.market.*;
import at.outdated.bitcoin.exchange.api.account.AccountInfo;
import javax.websocket.*;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.WebTarget;
import java.net.URI;
import java.util.List;
import java.util.Set;

/**
 * Created by ebirn on 31.10.13.
 */
public class IcbitClient extends RestExchangeClient {

    Session session;
    IcbitWebsocketClient socketClient;

    public IcbitClient(Market market) {
        super(market);

        try {
            WebSocketContainer container = ContainerProvider.getWebSocketContainer();

            Set<Extension> extensions = container.getInstalledExtensions();
            for(Extension e : extensions) {
                log.info("extension: {}", e);
            }


            String key = getPropertyString("authkey");
            String userId = getUserId();

            URI uri = new URI("wss://api.icbit.se:443/?AuthKey=" + key + "&UserId=" + userId);
            //URI uri = new URI("wss://echo.websocket.org");

            //URI uri =  new URI("ws://echo.websocket.org");
            socketClient = new IcbitWebsocketClient();
            session = container.connectToServer(socketClient, uri);
            socketClient.session = session;
    }
        catch(Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<WalletTransaction> getTransactions() {
        return null;
    }

    @Override
    public TickerValue getTicker(AssetPair asset) {

        try {
            for(int i=0; i<1; i++) {
                socketClient.sendMessage("blubb");
                Thread.sleep(500L);
            }

            session.close(new CloseReason(CloseReason.CloseCodes.NORMAL_CLOSURE, "done."));
        }
        catch(Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Number getLag() {
        return null;
    }

    @Override
    public Balance getBalance() {
        return null;
    }

    @Override
    public MarketDepth getMarketDepth(AssetPair asset) {
        return null;
    }

    @Override
    protected <T> Invocation.Builder setupProtectedResource(WebTarget res, Entity<T> entity) {
        return null;
    }

    @Override
    public List<MarketOrder> getOpenOrders() {
        return null;
    }

    @Override
    public OrderId placeOrder(AssetPair asset, OrderType type, CurrencyValue volume, CurrencyValue price) {
        return null;
    }

    @Override
    public boolean cancelOrder(OrderId order) {
        return false;
    }
}
