package at.outdated.bitcoin.exchange.kraken;

import com.sun.jersey.api.json.JSONConfiguration;
import com.sun.jersey.api.json.JSONJAXBContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

/**
 * Created with IntelliJ IDEA.
 * User: ebirn
 * Date: 25.05.13
 * Time: 13:55
 * To change this template use File | Settings | File Templates.
 */


@Provider
public class KrakenJsonResolver implements ContextResolver<JSONJAXBContext> {


    protected static final Logger log = LoggerFactory.getLogger("KrakenJsonResolver");
    protected final Class<?>[] cTypes = {KrakenTickerResponse.class};
    private final JSONJAXBContext context;
    private final Set<Class<?>> types;

    public KrakenJsonResolver() throws JAXBException {
        this.types = new HashSet<>(Arrays.asList(cTypes));
        this.context = new JSONJAXBContext(JSONConfiguration.natural().build(), cTypes);
    }

    public static String convert2Json(Object obj) {

        String result = null;

        try (StringWriter writer = new StringWriter()) {

            JAXBContext jc = JSONJAXBContext.newInstance(obj.getClass());
            Marshaller marshaller = jc.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            marshaller.marshal(obj, writer);

            writer.flush();

            result = writer.getBuffer().toString();

        } catch (IOException | JAXBException e) {
            e.printStackTrace();
        }

        return result;
    }

    @Override
    public JSONJAXBContext getContext(Class<?> objectType) {
        log.info("resolving json {}", objectType);
        return (types.contains(objectType)) ? context : null;
    }


}
