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



    public String hmac(String path, String message, String secret) {

        try {
            // args signature
            Mac mac = Mac.getInstance("HmacSHA512");
            SecretKeySpec secret_spec = new SecretKeySpec((new BASE64Decoder()).decodeBuffer(secret), "HmacSHA512");
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

}
