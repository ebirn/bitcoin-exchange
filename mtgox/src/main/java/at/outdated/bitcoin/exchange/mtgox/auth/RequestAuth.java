package at.outdated.bitcoin.exchange.mtgox.auth;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

/**
 * Created with IntelliJ IDEA.
 * User: ebirn
 * Date: 03.05.13
 * Time: 10:51
 * To change this template use File | Settings | File Templates.
 */
public class RequestAuth {
    private static final String key = "7d17ba55-a903-488c-aee7-582270c4bab7";
    private static final String secret = "yAwDpxaKqvV0JcjKmHz16CdwU1Uuviu3E4FkPUBVhv5ABSbHFSYqShHUhl16nmye40tmZKTFTUABHRsrgH4Gfw==";



    public String hmac(String path, String message) {

        try {
            // args signature
            Mac mac = Mac.getInstance("HmacSHA512");
            SecretKeySpec secret_spec = new SecretKeySpec((new BASE64Decoder()).decodeBuffer(this.secret), "HmacSHA512");
            mac.init(secret_spec);

            // path + NUL + POST (incl. nonce)
            String payload = path + "\0" + message;
            String signature = (new BASE64Encoder()).encode(mac.doFinal(payload.getBytes()));

            // cleanup string
            return signature.replaceAll("\n", "");
        }
        catch(Exception e) {
            return null;
        }
    }

    public static String getKey() {
        return key;
    }
}
