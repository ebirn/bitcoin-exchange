package at.outdated.bitcoin.exchange.api.jaxb;

import org.eclipse.persistence.jaxb.rs.MOXyJsonProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.Provider;
import javax.xml.bind.JAXBElement;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: ebirn
 * Date: 30.05.13
 * Time: 13:32
 * To change this template use File | Settings | File Templates.
 */
@Provider
public abstract class JSONResolver implements ContextResolver<MOXyJsonProvider> {
    protected static final Logger log = LoggerFactory.getLogger("JsonContextResolver");
    // does not contain InfoMessage, which is only returned in case of error and should throw exception

    protected MOXyJsonProvider context;
    protected Set<Class<?>> types;

    public JSONResolver() {
        this.types = new HashSet<>(Arrays.asList(getTypes()));
        try {
            this.context = new MOXyJsonProvider();
        }
        catch(Exception je) {
            log.error("failed to init JSON resolver for: " + getTypes(), je);
        }
    }

    protected abstract Class<?>[] getTypes();

    public static <T> T convertFromJson(String jsonString, Class<T> target) {

        T result = null;


        try {
            MOXyJsonProvider jacksonJAXBProvider = new MOXyJsonProvider();
            InputStream is = new ByteArrayInputStream(jsonString.getBytes());
            result = ((JAXBElement<T>) jacksonJAXBProvider.readFrom(Object.class, target, null, MediaType.APPLICATION_JSON_TYPE, null, is)).getValue();
        }catch (IOException ioe) {
            ioe.printStackTrace();
        }

        return result;
    }

    public static <S> String convert2Json(Object obj) {

        String result = null;

        try(StringWriter writer = new StringWriter()) {

            MOXyJsonProvider jacksonJAXBProvider = new MOXyJsonProvider();
            ///FIXME

            writer.flush();

            result = writer.getBuffer().toString();

        }
        catch (IOException /*| JAXBException */ e) {
            e.printStackTrace();
        }

        return result;
    }


    @Override
    public MOXyJsonProvider getContext(Class<?> objectType) {
        log.info("resolving json {0}", objectType);
        return (types.contains(objectType)) ? context : null;
    }
}
