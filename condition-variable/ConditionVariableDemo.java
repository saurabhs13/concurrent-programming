
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

class Shopper extends Thread{
    private int shopperId;
    private static Lock wallet = new ReentrantLock();
    private static Condition itemPurchased = wallet.newCondition();
    private static int stock = 11;
    private boolean useCondition = false;

    public Shopper(int shopperId,boolean useCondition){
        this.shopperId = shopperId;
        this.useCondition = useCondition;
    }

    private void buyItem(){
        int unsuccessfulAttempt = 0;
        while(stock > 0){
            wallet.lock();
            try {
                if((this.shopperId == stock%2) && stock>0){
                    stock--;
                    System.out.println(this.shopperId+ " bought 1 quantity, remaining stock = "+stock);
                }else{
                    System.out.println(this.shopperId+ " didn't buy the product, he/she will continue browsing");
                    unsuccessfulAttempt++;
                }
                    
            } catch (Exception e) {
                e.printStackTrace();
            }finally{
                wallet.unlock();
            }
          
        }
        System.out.println(this.shopperId+ " had "+unsuccessfulAttempt+ " unsuccessful buy attempts");
    }

    private void buyItemWithCondition(){
        int unsuccessfulAttempt = 0;
        while(stock > 0){
            wallet.lock();
            try {
                while((this.shopperId != stock%2) && stock>0){
                    System.out.println(this.shopperId+ " didn't buy the product, he/she will continue browsing");
                    unsuccessfulAttempt++;
                    itemPurchased.await();
                }
                if(stock>0){
                    stock--;
                    System.out.println(this.shopperId+ " bought 1 quantity, remaining stock = "+stock);
                    itemPurchased.signal();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }finally{
                wallet.unlock();
            }
          
        }
        System.out.println(this.shopperId+ " had "+unsuccessfulAttempt+ " unsuccessful buy attempts");
    }
    
    public void run(){
        if(!this.useCondition){
            buyItem();
        }else{
            buyItemWithCondition();
        }
    }
   

}
public class ConditionVariableDemo{
    public static void main(String[] args){

        boolean useCondition = Boolean.parseBoolean(args[0]);

        for(int i=0;i<2;i++){
            new Shopper(i,useCondition).start();
        }
        
    }
    
}