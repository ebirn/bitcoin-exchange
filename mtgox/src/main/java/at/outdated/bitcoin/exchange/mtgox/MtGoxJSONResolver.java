package at.outdated.bitcoin.exchange.mtgox;

import com.sun.jersey.api.json.JSONConfiguration;
import com.sun.jersey.api.json.JSONJAXBContext;

import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.Provider;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import java.io.IOException;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created with IntelliJ IDEA.
 * User: ebirn
 * Date: 02.05.13
 * Time: 20:32
 * To change this template use File | Settings | File Templates.
 */
@Provider
public class MtGoxJSONResolver implements ContextResolver<JSONJAXBContext> {

    private final JSONJAXBContext context;
    private final Set<Class<?>> types;
    protected static final Logger log = Logger.getLogger("MtGoxContextResolver");

    // does not contain InfoMessage, which is only returned in case of error and should throw exception
    protected final Class<?>[] cTypes = { ApiTickerResponse.class, ApiLagResponse.class, ApiAccountInfo.class, ApiWalletHistory.class };

    public MtGoxJSONResolver() throws JAXBException {

        this.types = new HashSet<>(Arrays.asList(cTypes));
        this.context = new JSONJAXBContext(JSONConfiguration.natural().build(), cTypes);


    }

    @Override
    public JSONJAXBContext getContext(Class<?> objectType) {
        log.log(Level.INFO, "resolving json {0}", objectType);
        return (types.contains(objectType)) ? context : null;
    }


    public static String convert2Json(Object obj) {

        String result = null;

        try(StringWriter writer = new StringWriter()) {

            JAXBContext jc = JSONJAXBContext.newInstance(obj.getClass());
            Marshaller marshaller = jc.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            marshaller.marshal(obj, writer);

            writer.flush();

            result = writer.getBuffer().toString();

        }
        catch (IOException | JAXBException e) {
            e.printStackTrace();
        }

        return result;
    }
}
