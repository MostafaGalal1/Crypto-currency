import java.io.*;  
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import java.security.*;
import java.security.spec.ECGenParameterSpec;
import java.util.Base64;

public class Main
{
	public static void main(String[] args) {
	    
		BlockChain crypto = new BlockChain();
		
		int wallets_num = 5;
        String[] wallets = new String[wallets_num];
        String[] walletskeys = new String[wallets_num];
        
        for (int i = 0; i < wallets_num;i++){
            keyGenerator KeyGen = new keyGenerator();
    		KeyPair KPair = KeyGen.getSign();
    
    		wallets[i] = Base64.getEncoder().encodeToString(KPair.getPublic().getEncoded());
    		walletskeys[i] = Base64.getEncoder().encodeToString(KPair.getPrivate().getEncoded());
        }
		
		
		transaction trans = null;
		
		trans = new transaction(null, wallets[0], 5435.43);
		crypto.createGenesis(trans);
		
		trans = new transaction(wallets[0], wallets[1], 2453.45);
		trans.sign(walletskeys[0]);
		crypto.addTransaction(trans);
		crypto.minePendingTransaction("miner X");

		trans = new transaction(wallets[1], wallets[0], 954.99);
		trans.sign(walletskeys[3]);
		crypto.addTransaction(trans);
		crypto.minePendingTransaction("miner X");
		
		trans = new transaction(wallets[0], wallets[2], 887.36);
		trans.sign(walletskeys[0]);
		crypto.addTransaction(trans);
		crypto.minePendingTransaction("miner X");
		
		trans = new transaction(wallets[2], wallets[3], 954.99);
		trans.sign(walletskeys[2]);
		crypto.addTransaction(trans);
		crypto.minePendingTransaction("miner X");
		
		trans = new transaction(wallets[1], wallets[4], 807.36);
		trans.sign(walletskeys[1]);
		crypto.addTransaction(trans);
		crypto.minePendingTransaction("miner X");
		
		crypto.minePendingTransaction("miner X");

		System.out.println("chain validity state: " + crypto.isChainValid());
		
		System.out.println("abdelrahman's balance: " +  String.format("%.02f", crypto.getBalance(wallets[0])) +" $");
		System.out.println("mostafa's balance: " +  String.format("%.02f", crypto.getBalance(wallets[1])) +" $");
		System.out.println("talha's balance: " +  String.format("%.02f", crypto.getBalance(wallets[2])) +" $");
		System.out.println("mones's balance: " +  String.format("%.02f", crypto.getBalance(wallets[3])) +" $");
		System.out.println("marwan's balance: " +  String.format("%.02f", crypto.getBalance(wallets[4])) +" $");
		System.out.println("miner X's balance: " + String.format("%.02f", crypto.getBalance("miner X")) +" $");
	}
}
