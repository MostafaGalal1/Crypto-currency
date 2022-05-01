import java.security.*;
import java.util.Base64;
import java.security.spec.X509EncodedKeySpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.interfaces.RSAPublicKey;
import java.security.interfaces.RSAPrivateKey;

class transaction{
    String sender;
    String reciever;
    double amount;
    String signature;
    
    //Transaction Constructor
    public transaction(String sender, String reciever, double amount){
        this.sender = sender;
        this.reciever = reciever;
        this.amount = amount;
    }
    
    private String hashCalculate(){
        
        String hash_source = this.sender + this.reciever + this.amount;

        //Digesting the message
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("SHA-256");
        } catch(Exception e){}
		md.update(hash_source.getBytes());
		byte[] hash_byte = md.digest();      

        //Stringifing the hash
        StringBuffer hash_buffer = new StringBuffer();
      
        for (int i = 0; i < hash_byte.length; i++) {
           hash_buffer.append(Integer.toHexString(0xFF & hash_byte[i]));
        }
        
        String hash = hash_buffer.toString();
        return hash; 
    }
    
    public void sign(String privateKeyStr){
        try{
            
            //Getting PrivateKey from the String
            byte[] privateBytes = Base64.getDecoder().decode(privateKeyStr);
            KeyFactory keypr = KeyFactory.getInstance("RSA");
            PrivateKey privateKey = keypr.generatePrivate(new PKCS8EncodedKeySpec(privateBytes));
            
            //Getting PublicKey from the String
            byte[] publicBytes = Base64.getDecoder().decode(sender);
            KeyFactory keypp = KeyFactory.getInstance("RSA");
            PublicKey publicKey = keypp.generatePublic(new X509EncodedKeySpec(publicBytes));
            
            RSAPrivateKey rsaPri = (RSAPrivateKey) privateKey;
            RSAPublicKey rsaPub = (RSAPublicKey) publicKey;

            //Verifying PrivateKey with PublicKey
            if(!rsaPri.getModulus().equals(rsaPub.getModulus()) ){
                System.out.println("Incorrect Private Key\n");
                signature = "0";
                return;
            }
            
            //Initializng signature
            Signature privateSign = Signature.getInstance("SHA256withRSA");
            privateSign.initSign(privateKey);
            
            //Calculating the hash for the transaction
            String trans_hash = hashCalculate();
            privateSign.update(trans_hash.getBytes("UTF-8"));
            
            //Signing the transaction with PrivateKey
            byte[] sign_bytes = privateSign.sign();
            this.signature = Base64.getEncoder().encodeToString(sign_bytes);
            
        }catch(Exception e){}
    }
    
    public boolean verify(String signature, String publicKeyStr){
        
        //Verifying block isn't a genesis or reward block
        if (sender == null){
            return true;
        }
        
        //Verifying there is a signature for the transaction
        if (signature == null){
            return false;
        }
        
        try{
            
            //Getting PublicKey from the String
            byte[] publicBytes = Base64.getDecoder().decode(publicKeyStr);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            PublicKey publicKey = keyFactory.generatePublic(new X509EncodedKeySpec(publicBytes));
            
            //Initializng signature
            Signature publicSign = Signature.getInstance("SHA256withRSA");
            publicSign.initVerify(publicKey);
            
            //Calculating the hash for the transaction
            String trans_hash = hashCalculate();
            publicSign.update(trans_hash.getBytes("UTF-8"));
            
            //Verifying publicKey with the signature
            byte[] sign_bytes = Base64.getDecoder().decode(signature);
            return publicSign.verify(sign_bytes);
            
        }catch(Exception e){}
        
        return false;
    }
    
}
