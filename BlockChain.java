import java.util.*;

class BlockChain{
    List<Block> chain =new ArrayList<Block>(); 
    Queue<transaction> pending_transactions = new LinkedList<transaction>();
    public int diff = 1;
    public double mineAward = 50;
    
    private Block getLatestBlock(){
        return chain.get(chain.size() - 1);
    }
    
    public Block getBlock(int index){
        return chain.get(index);
    }
    
    public void createGenesis(transaction trans){
        Block newBlock = new Block(trans);
        newBlock.pre_hash = null;
        newBlock.mineBlock(this.diff);
        chain.add(newBlock);
    }
    
    public void minePendingTransaction(String minerAddress){
        transaction award = new transaction(null, minerAddress, this.mineAward);
        
        if (pending_transactions.size() > 0){
            transaction trans = pending_transactions.remove();
            double sender_balance = getBalance(trans.sender);
            if ((sender_balance - trans.amount) < 0){
                System.out.println("Insufficient Balance to complete the transaction:\n{"); 
                System.out.println("    Sender: " + trans.sender); 
                System.out.println("    Reciever: " + trans.reciever); 
                System.out.println("    Amount: " + String.format("%.02f", trans.amount) + " $"); 
                System.out.println("    Current Balance: " + sender_balance + " $\n}\n"); 
            } else {
                Block newBlock = new Block(trans);
                if (chain.size() == 0){
                    newBlock.pre_hash = null;
                } else {
                    newBlock.pre_hash = getLatestBlock().hash;     
                }
                
                newBlock.mineBlock(this.diff);
                newBlock.blockInfo();
                chain.add(newBlock);
                
                System.out.println("Calculating hash for the block:\n");
                Block awardBlock = new Block(award);
                awardBlock.blockInfo();
                chain.add(awardBlock);
            }
        } else {
            System.out.println("No pending transactions");
        }
    }
    
    public void addTransaction(transaction trans){
        Boolean verify = false;
        try{
            verify = trans.verify(trans.signature, trans.sender);
        }catch(Exception e){}
        
        if (trans.sender == null || trans.reciever == null || !verify){
            System.out.println("Invalid transaction");
        } else {
            pending_transactions.add(trans);
        }
    }
    
    
    public double getBalance(String address){
        double balance = 0;
        for (int i = 0; i < chain.size(); i++){
            if (chain.get(i).trans.sender == address){
                balance -= chain.get(i).trans.amount;
            } else if (chain.get(i).trans.reciever == address){
                balance += chain.get(i).trans.amount;
            } else {
                continue;
            }
        }
        
        return balance;
    }
    
    public Boolean isChainValid(){
        for (int i = 1 ; i < chain.size(); i++){
            if (!chain.get(i).hash.equals(chain.get(i).hashCalculate())){
                return false;
            }
            if (chain.get(i).pre_hash != null && !chain.get(i).pre_hash.equals(chain.get(i-1).hashCalculate())){
                return false;
            }
            if (!chain.get(i).isValid()){
                return false;
            }
        }
        return true;
    }
}