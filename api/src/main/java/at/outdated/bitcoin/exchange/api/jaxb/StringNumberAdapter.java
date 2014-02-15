package at.outdated.bitcoin.exchange.api.jaxb;

import javax.xml.bind.annotation.adapters.XmlAdapter;
import java.text.NumberFormat;
import java.util.Locale;

/**
 * Created with IntelliJ IDEA.
 * User: ebirn
 * Date: 25.05.13
 * Time: 16:25
 * @deprecated should not be used in favor of StringBigDecimalAdapter
 */
@Deprecated
public class StringNumberAdapter extends XmlAdapter<String,Number> {

    NumberFormat nf = NumberFormat.getInstance(Locale.US);

    @Override
    public Number unmarshal(String v) throws Exception {
        return nf.parse(v);
    }

    @Override
    public String marshal(Number v) throws Exception {
        return nf.format(v);
    }
}
