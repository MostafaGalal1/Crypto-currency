import java.util.*;
import java.sql.Timestamp;
import java.util.Random;
import java.security.MessageDigest;

class Block{
    public String hash;
    public String pre_hash;
    public Timestamp date;
    public transaction trans;
    public String nonce;
    
    //Block Constructor
    public Block(transaction trans){
        this.pre_hash = pre_hash;
        this.date = new Timestamp(System.currentTimeMillis());
        this.trans = trans;
        this.nonce = nonceGenerate();
        this.hash = hashCalculate();
    }
    
    public String nonceGenerate(){
		int n = new Random().nextInt();
		String nonce = Integer.toHexString(n);
		return nonce;
    }
    
    public String hashCalculate(){
        String hash_source = this.pre_hash + this.date + this.trans.sender + this.trans.reciever + this.trans.amount + this.nonce;
        
        //Digesting the message
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("SHA-256");
        } catch(Exception e) {}
		md.update(hash_source.getBytes());
		byte[] hash_byte = md.digest();      
        
        //Stringifing the hash
        StringBuffer hash_buffer = new StringBuffer();
      
        for (int i = 0; i < hash_byte.length; i++) {
           hash_buffer.append(Integer.toHexString(0xFF & hash_byte[i]));
        }
        
        hash = hash_buffer.toString();
        return hash; 
    }
    
    public void mineBlock(int diff){
        
        String[] zeroArr = new String[diff];
        
        for (int i = 0; i < diff; i++){
            zeroArr[i] = "0";
        }
        
        //Generating hash with the required number of leading zeros;
        while(!hash.substring(0,diff).equals(String.join("", zeroArr))){
            this.nonce = nonceGenerate();
            this.hash = hashCalculate();
        }
    }
    
    public void blockInfo(){
        System.out.println("Block: " + this.nonce + "\n{");
        System.out.println("    Previous hash: " + this.pre_hash);
        System.out.println("    Block hash: " + this.hash);
        System.out.println("    Date created: " + this.date);
        System.out.println("    Sender: " + this.trans.sender);
        System.out.println("    Reciever: " + this.trans.reciever);
        System.out.println("    Amount: " + String.format("%.02f", this.trans.amount) + " $\n}\n");
    }
    
    //Verifying publicKey with the signature
    public boolean isValid(){
        if (trans.verify(trans.signature, trans.sender)){
            return true;
        }
        return false;
    }
}
