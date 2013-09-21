package at.outdated.bitcoin.exchange.btce;


import org.glassfish.jersey.jettison.JettisonConfig;
import org.glassfish.jersey.jettison.JettisonJaxbContext;
import org.glassfish.jersey.jettison.JettisonUnmarshaller;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.Provider;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: ebirn
 * Date: 02.05.13
 * Time: 20:32
 * To change this template use File | Settings | File Templates.
 */
@Provider
public class BtcEJSONResolver implements ContextResolver<JettisonJaxbContext> {

    private final JettisonJaxbContext context;
    private final Set<Class<?>> types;
    protected static final Logger log = LoggerFactory.getLogger("BtcEContextResolver");

    

    // does not contain InfoMessage, which is only returned in case of error and should throw exception
    protected static final Class<?>[] cTypes = { TickerResponse.class };

    public BtcEJSONResolver() throws JAXBException {
        this.types = new HashSet<>(Arrays.asList(cTypes));
        this.context = new JettisonJaxbContext(JettisonConfig.DEFAULT, cTypes);
    }

    @Override
    public JettisonJaxbContext getContext(Class<?> objectType) {
        log.info("resolving json {0}", objectType);
        return (types.contains(objectType)) ? context : null;
    }

    public static <T> T convertFromJson(String jsonString, Class<T> target) {

        T result = null;

        try {

            JettisonJaxbContext jc = new JettisonJaxbContext(JettisonConfig.DEFAULT, cTypes);
            JettisonUnmarshaller um = jc.createJsonUnmarshaller();


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

            JAXBContext jc = JettisonJaxbContext.newInstance(obj.getClass());
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
