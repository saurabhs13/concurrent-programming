
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

class Philosopher extends Thread{
    private Lock firstChopstick, secondChopstick;
    private static int servings = 5000000;

    public Philosopher(String name,Lock first, Lock second){
        this.setName(name);
        this.firstChopstick = first;
        this.secondChopstick = second;
    }
    public void run(){

        while(servings > 0){
            try {
                firstChopstick.lock();
                secondChopstick.lock();
                if(servings>0){
                    servings--;
                    System.out.println(this.getName()+ " took a serving, remaining servings = "+servings);
                }
                    
            } catch (Exception e) {
                e.printStackTrace();
            }finally{
                firstChopstick.unlock();
                secondChopstick.unlock();
            }
          
        }
    }
}
public class DeadlockDemo{
    public static void main(String[] args){
        Lock chopstickA = new ReentrantLock();
        Lock chopstickB = new ReentrantLock();
        Lock chopstickC = new ReentrantLock();

        /** Below code causes deadlock.
         * 
        new Philosopher("Sam",chopstickA,chopstickB).start();
        new Philosopher("Jeff",chopstickB,chopstickC).start();
        new Philosopher("Lee",chopstickC,chopstickA).start();
        */
        /**Lock Ordering to avoid deadlock.
         * ChopstickA - 1st Priority, ChopstickB - 2nd Priority, ChopstickC - 3rd Priority
         * So any philosopher should try to aquire highest priority chopstick first, this avoids deadlocks.
         */
        new Philosopher("Sam",chopstickA,chopstickB).start();
        new Philosopher("Jeff",chopstickB,chopstickC).start();
        new Philosopher("Lee",chopstickA,chopstickC).start();
    }
    
}