package at.outdated.bitcoin.exchange.icbit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.websocket.*;

/**
 * Created by ebirn on 31.10.13.
 */

@ClientEndpoint
public class IcbitWebsocketClient {

    Logger log = LoggerFactory.getLogger("icbit.ws");

    Session session;
    @OnOpen
    public void connect(Session session) {
        log.info("opened with session: {}", session);
    }

    @OnClose
    public void disconnect(CloseReason reason) {
        log.info("bye. - {}", reason);
    }

    @OnMessage
    public void message(String textMsg) {
        log.info("receiving: {}", textMsg);
    }

    @OnError
    public void error(Throwable t) {
        log.error("problem: {}", t);
    }

    public void sendMessage(String msg) {
        try {
            log.info("sending: {}", msg);
            session.getBasicRemote().sendText(msg);
        }
        catch(Exception e) {
            log.error("failed to send", e);
        }
    }

}
