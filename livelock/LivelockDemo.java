
import java.util.Random;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
class Philosopher extends Thread{
    private Lock firstChopstick, secondChopstick;
    private static int servings = 500_000;
    private Random random = new Random();

    public Philosopher(String name,Lock first, Lock second){
        this.setName(name);
        this.firstChopstick = first;
        this.secondChopstick = second;
    }
    public void run(){

        while(servings > 0){
           
                firstChopstick.lock();
                if(!secondChopstick.tryLock()){
                    System.out.println(this.getName()+" releasing the first chopstick");
                    firstChopstick.unlock();
                    /**
                     * If you comment the sleep code then it will result 
                     * the program running into a livelock.
                     */
                    try {
                        Thread.sleep(random.nextInt(3));
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }else{
                    try {      
                        if(servings>0){
                            servings--;
                            System.out.println(this.getName()+ " took a serving, remaining servings = "+servings);
                        }
                    }catch (Exception e) {
                        e.printStackTrace();
                    }finally{
                        firstChopstick.unlock();
                        secondChopstick.unlock();
                    }
              
                }
        }
    }
}
public class LivelockDemo{
    public static void main(String[] args){
        Lock chopstickA = new ReentrantLock();
        Lock chopstickB = new ReentrantLock();
        Lock chopstickC = new ReentrantLock();

       
        new Philosopher("Sam",chopstickA,chopstickB).start();
        new Philosopher("Jeff",chopstickB,chopstickC).start();
        new Philosopher("Lee",chopstickC,chopstickA).start();
        
    }
    
}