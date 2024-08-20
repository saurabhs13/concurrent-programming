
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
/**
 * Barrier in this program ensures that first all threads must finish addition
 * in order to start the multiplication process.
 */
class Shopper extends Thread{

    public static int cartTotal = 0;
    private static CyclicBarrier barrier = new CyclicBarrier(10);
    private static Lock addToCart  = new ReentrantLock();
    

    public void run(){
        addToCart.lock();
        try {
            
            cartTotal++;
        }finally{
            addToCart.unlock();
        }
        try {
            barrier.await();
            addToCart.lock();
            cartTotal = cartTotal*2;
        } catch (InterruptedException|BrokenBarrierException ie) {
            ie.printStackTrace();
        }finally{
            addToCart.unlock();
          
        }
        
    }
}
public class BarrierDemo{
    public static void main(String[] args) throws InterruptedException{
        Shopper[] s = new Shopper[10];

        for(int i=0;i<10;i++){
            s[i] = new Shopper();
        }
        for(int i=0;i<10;i++){
            s[i].start();
        }
        for(int i=0;i<10;i++){
            s[i].join();
        }
        System.err.println("Cart total = "+Shopper.cartTotal);
    }
}