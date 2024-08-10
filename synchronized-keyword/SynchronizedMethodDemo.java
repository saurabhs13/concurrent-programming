/**
 * Program to achieve mutual exclusion using synchronized method.
 * Note here that synchronized method is marked static hence all instances
 * of CounterThread will have synchronized access to the method.
 */
class CounterThread extends Thread{

    static int count;
    public static synchronized void  incrementCount(){
        count++;
    }
    /**
     * Pitfall: if this non static synchronized method is used it will
     * lead to same issues as an unsynchronized access. Because here the lock will
     * not be aquired for the CounterThread.class object rather individual instance 
     * level and hence will not have the desired result.
     */
   /*  public synchronized void  incrementCount(){
        count++;
    }*/
    public void run(){
        System.out.println(Thread.currentThread().getName()+ " is executing");
        for(int i=1;i<=10000;i++){
            incrementCount();
            
        }
    }
}
public class SynchronizedMethodDemo{

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