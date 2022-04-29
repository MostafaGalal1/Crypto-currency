import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import java.security.*;
import java.security.spec.ECGenParameterSpec;
import java.util.Base64;
import java.security.spec.X509EncodedKeySpec;
import java.security.spec.PKCS8EncodedKeySpec;

class transaction{
    String sender;
    String reciever;
    double amount;
    String signature;
    
    public transaction(String sender, String reciever, double amount){
        this.sender = sender;
        this.reciever = reciever;
        this.amount = amount;
    }
    
    private String hashCalculate(){
        String hash_source = this.sender + this.reciever + this.amount;

        MessageDigest md = null;
        
        try {
            md = MessageDigest.getInstance("SHA-256");
        } catch(Exception e){
        }
            
		md.update(hash_source.getBytes());
		
		byte[] hash_byte = md.digest();      

        StringBuffer hash_buffer = new StringBuffer();
      
        for (int i = 0; i < hash_byte.length; i++) {
           hash_buffer.append(Integer.toHexString(0xFF & hash_byte[i]));
        }
        
        String hash = hash_buffer.toString();
        return hash; 
    }
    
    public void sign(String privateKeyStr){
        try{
            byte[] privateBytes = Base64.getDecoder().decode(privateKeyStr);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            PrivateKey privateKey = keyFactory.generatePrivate(new PKCS8EncodedKeySpec(privateBytes));
            Signature privateSignature = Signature.getInstance("SHA256withRSA");
            privateSignature.initSign(privateKey);
            
            String trans_hash = hashCalculate();
            Signature privateSign = Signature.getInstance("SHA256withRSA");
            privateSign.initSign(privateKey);
            privateSign.update(trans_hash.getBytes("UTF-8"));
        
            byte[] sign_bytes = privateSign.sign();
        
            this.signature = Base64.getEncoder().encodeToString(sign_bytes);
            
        }catch(Exception e){}
    }
    
    public boolean verify(String signature, String publicKeyStr){
        if (sender == null){
            return true;
        }
        if (signature == null){
            return false;
        }
        try{
            byte[] publicBytes = Base64.getDecoder().decode(publicKeyStr);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            PublicKey publicKey = keyFactory.generatePublic(new X509EncodedKeySpec(publicBytes));
            
            Signature publicSignature = Signature.getInstance("SHA256withRSA");
            publicSignature.initVerify(publicKey);
            publicSignature.update(hashCalculate().getBytes("UTF-8"));
        
            byte[] sign_bytes = Base64.getDecoder().decode(signature);
        
            return publicSignature.verify(sign_bytes);
        }catch(Exception e){}
        
        return false;
    }
    
}