package at.outdated.bitcoin.exchange.api.jaxb;

import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.client.ClientResponseContext;
import javax.ws.rs.client.ClientResponseFilter;
import javax.ws.rs.core.MediaType;
import java.io.IOException;

/**
 * Created by ebirn on 21.01.14.
 */

public class JsonEnforcingFilter implements ClientResponseFilter {


    @Override
    public void filter(ClientRequestContext requestContext, ClientResponseContext responseContext) throws IOException {

        responseContext.getHeaders().putSingle("Content-Type", MediaType.APPLICATION_JSON);

    }
}
