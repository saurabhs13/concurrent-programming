
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
/**
 * Program to achieve mutual exclusion using reentrant lock.
 */
class CounterThread extends Thread{
    static int count;
    static Lock countingDevice = new ReentrantLock();
    
    public void run(){
        System.out.println(Thread.currentThread().getName()+ " is executing");
        for(int i=1;i<=10000;i++){
            countingDevice.lock();
              count++;
            countingDevice.unlock();
            
        }
    }
}
public class MutexDemo{

    public static void main(String[] args) {

        CounterThread counter1 = new CounterThread();
        CounterThread counter2 = new CounterThread();
        counter1.start();
        counter2.start();
        try {
            counter1.join();
        }catch (InterruptedException e) {
            e.printStackTrace();
        }
        try {
            counter2.join();
        }catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("Final count: "+ CounterThread.count);
    }
}