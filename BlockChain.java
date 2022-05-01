import java.util.*;

class BlockChain{
    List<Block> chain =new ArrayList<Block>(); 
    Queue<transaction> pending_transactions = new LinkedList<transaction>();
    public int diff = 1;
    public double mineReward = 50;
    
    private Block getLatestBlock(){
        return chain.get(chain.size() - 1);
    }
    
    public Block getBlock(int index){
        return chain.get(index);
    }
    
    //Creating genesis block for the chain
    public void createGenesis(transaction trans){
        Block newBlock = new Block(trans);
        newBlock.pre_hash = null;
        newBlock.mineBlock(this.diff);
        chain.add(newBlock);
    }
    
    public void minePendingTransaction(String minerAddress){
        
        //Setting miner reward transaction
        transaction reward = new transaction(null, minerAddress, this.mineReward);
        
        //Checking if there is any pending transactions
        if (pending_transactions.size() > 0){
            transaction trans = pending_transactions.remove();
            
            //Setting previous hash of the block
            Block newBlock = new Block(trans);
            if (chain.size() == 0){
                newBlock.pre_hash = null;
            } else {
                newBlock.pre_hash = getLatestBlock().hash;     
            }
            
            //Mining the block and adding it to the chain
            newBlock.mineBlock(this.diff);
            newBlock.blockInfo();
            chain.add(newBlock);
            
            //Adding reward block to the chain
            Block rewardBlock = new Block(reward);
            rewardBlock.blockInfo();
            chain.add(rewardBlock);
        } else {
            System.out.println("No pending transactions\n");
        }
    }
    
    public void addTransaction(transaction trans){
        
        Boolean verify = false;
        double sender_balance = getBalance(trans.sender);
            
        //Checking trans validity
        try{
            verify = trans.verify(trans.signature, trans.sender);
        }catch(Exception e){}
        if (trans.sender == null || trans.reciever == null || !verify){
            System.out.println("Invalid transaction\n");
        
        //Checking balance sufficiency
        } else if  ((sender_balance - trans.amount) < 0){
            System.out.println("Insufficient Balance to complete the transaction:\n{"); 
            System.out.println("    Sender: " + trans.sender); 
            System.out.println("    Reciever: " + trans.reciever); 
            System.out.println("    Amount: " + String.format("%.02f", trans.amount) + " $"); 
            System.out.println("    Current Balance: " + sender_balance + " $\n}\n"); 
        } else {
            pending_transactions.add(trans);
        }
    }
    
    
    public double getBalance(String address){
        
        double balance = 0;
        
        //Iterating through all blocks in the chain
        for (int i = 0; i < chain.size(); i++){
            if (chain.get(i).trans.sender == address){
                balance -= chain.get(i).trans.amount;
            } else if (chain.get(i).trans.reciever == address){
                balance += chain.get(i).trans.amount;
            } else {
                continue;
            }
        }
        
        String balanceStr = String.format("%.02f", balance);
        balance = Double.parseDouble(balanceStr);
        return balance;
    }
    
    public Boolean isChainValid(){
        
        //Iterating through all blocks in the chain
        for (int i = 1 ; i < chain.size(); i++){
            
            //Checking for hash equality
            if (!chain.get(i).hash.equals(chain.get(i).hashCalculate())){
                return false;
            }
            
            //Checking for previous hash equality
            if (chain.get(i).pre_hash != null && !chain.get(i).pre_hash.equals(chain.get(i-1).hashCalculate())){
                return false;
            }
            
            //Checking block validity
            if (!chain.get(i).isValid()){
                return false;
            }
        }
        return true;
    }
}
