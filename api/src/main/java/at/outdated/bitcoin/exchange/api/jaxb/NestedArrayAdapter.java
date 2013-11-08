package at.outdated.bitcoin.exchange.api.jaxb;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonReader;
import javax.xml.bind.annotation.adapters.XmlAdapter;
import java.io.StringReader;

/**
 * Created by ebirn on 30.09.13.
 */
public class NestedArrayAdapter extends XmlAdapter<String,float[][]> {

    @Override
    public float[][] unmarshal(String v) throws Exception {

        JsonReader jsonReader = Json.createReader(new StringReader(v));
        JsonArray jsonArray = jsonReader.readArray();
        jsonReader.close();

        int len = jsonArray.size();
        float[][] resultArray = new float[len][];

        for(int i=0; i<len; i++) {


            JsonArray innerJsonArray = jsonArray.getJsonArray(i);
            int innerLen = innerJsonArray.size();
            float[] inner = new float[innerLen];

            for(int j=0; j<innerLen; j++) {
                inner[j] = Float.parseFloat(innerJsonArray.getString(j));
            }
            resultArray[i] = inner;
        }

        return resultArray;
    }

    @Override
    public String marshal(float[][] v) throws Exception {
        return null;
    }
}
