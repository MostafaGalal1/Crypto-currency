import java.security.*;
import java.util.Base64;

class keyGenerator{
    
    public KeyPair getSign() {
        
        //Generating KeyPair of public and private keys
        KeyPair pair = null;
        try {
            KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
            keyGen.initialize(1024);
            pair = keyGen.generateKeyPair();
        
        } catch(Exception e){}
        return pair;
    }
}
