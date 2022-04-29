import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import java.security.*;
import java.security.spec.ECGenParameterSpec;
import java.util.Base64;

class keyGenerator{
    
    public KeyPair getSign() {
        KeyPair pair = null;
        try {
            KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
            keyGen.initialize(1024);
            pair = keyGen.generateKeyPair();
        
        } catch(Exception e){}
        return pair;
    }
}