package at.outdated.bitcoin.exchange.api.jaxb;

import com.sun.jersey.api.json.JSONConfiguration;
import com.sun.jersey.api.json.JSONJAXBContext;
import com.sun.jersey.api.json.JSONUnmarshaller;

import javax.ws.rs.ext.ContextResolver;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created with IntelliJ IDEA.
 * User: ebirn
 * Date: 30.05.13
 * Time: 13:32
 * To change this template use File | Settings | File Templates.
 */
public abstract class JSONResolver implements ContextResolver<JSONJAXBContext> {
    protected static final Logger log = Logger.getLogger("BtcEContextResolver");
    // does not contain InfoMessage, which is only returned in case of error and should throw exception

    protected JSONJAXBContext context;
    protected Set<Class<?>> types;

    private static Class<?>[] cTypes = {};

    public JSONResolver() throws JAXBException {
        this.types = new HashSet<>(Arrays.asList(cTypes));
        this.context = new JSONJAXBContext(JSONConfiguration.natural().build(), cTypes);
    }

    public static <T> T convertFromJson(String jsonString, Class<T> target) {

        T result = null;

        try {
            JSONJAXBContext jc = new JSONJAXBContext(JSONConfiguration.natural().build(), cTypes);
            JSONUnmarshaller um = jc.createJSONUnmarshaller();

            StringReader reader = new StringReader(jsonString);

            result = um.unmarshalFromJSON(reader, target);

        }
        catch (JAXBException je ) {
            return null;
        }

        return result;
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

    @Override
    public JSONJAXBContext getContext(Class<?> objectType) {
        log.log(Level.INFO, "resolving json {0}", objectType);
        return (types.contains(objectType)) ? context : null;
    }
}
