package at.outdated.bitcoin.exchange.bitstamp;

/**
 * Created with IntelliJ IDEA.
 * User: ebirn
 * Date: 25.05.13
 * Time: 13:55
 * To change this template use File | Settings | File Templates.
 */


//@Provider
public class BitstampJsonResolver
{}

        /*
        implements ContextResolver<JSONJAXBContext> {



    protected static final Logger log = LoggerFactory.getLogger("BitsampJsonResolver");
    protected final Class<?>[] cTypes = {BitstampTickerValue.class};
    private final JSONJAXBContext context;
    private final Set<Class<?>> types;

    public BitstampJsonResolver() throws JAXBException {
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
*/
