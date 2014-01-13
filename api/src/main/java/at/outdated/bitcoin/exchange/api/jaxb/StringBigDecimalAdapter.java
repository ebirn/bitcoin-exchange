package at.outdated.bitcoin.exchange.api.jaxb;

import javax.xml.bind.annotation.adapters.XmlAdapter;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

/**
 * Created by ebirn on 13.01.14.
 */
public class StringBigDecimalAdapter extends XmlAdapter<String,BigDecimal> {



    @Override
    public BigDecimal unmarshal(String v) throws Exception {
        return new BigDecimal(v);
    }

    //TODO: is this good enough converting from double - BigDecimal direct would be better!
    @Override
    public String marshal(BigDecimal v) throws Exception {

        NumberFormat format = NumberFormat.getInstance(Locale.US);
        format.setGroupingUsed(false);
        format.setMinimumIntegerDigits(1);
        format.setMinimumFractionDigits(4);
        format.setMaximumFractionDigits(7);

        return format.format(v.doubleValue());
    }
}
