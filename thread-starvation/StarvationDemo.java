
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

class Philosopher extends Thread{
    private Lock firstChopstick, secondChopstick;
    private static int servings = 500_000;

    public Philosopher(String name,Lock first, Lock second){
        this.setName(name);
        this.firstChopstick = first;
        this.secondChopstick = second;
    }
    public void run(){
        int philosopherConsumption = 0;
        while(servings > 0){
            try {
                firstChopstick.lock();
                secondChopstick.lock();
                if(servings>0){
                    servings--;
                    philosopherConsumption++;
                    System.out.println(this.getName()+ " took a serving, remaining servings = "+servings);
                }
                    
            } catch (Exception e) {
                e.printStackTrace();
            }finally{
                firstChopstick.unlock();
                secondChopstick.unlock();
            }
          
        }
        System.out.println(this.getName()+" has consumed "+philosopherConsumption+" servings");
    }
}
public class StarvationDemo{
    public static void main(String[] args){
        Lock chopstickA = new ReentrantLock();
        Lock chopstickB = new ReentrantLock();
        Lock chopstickC = new ReentrantLock();

        /**
         * When you run the program and observe the output
         * you will see many threads get zero servings. To avoid thread stravation
         * there are many ways but they are contextual. Some best practices can be to avoid
         * nested locks, using lock timeouts etc.
         */
        for(int i=0;i<5000;i++){
            new Philosopher("Sam",chopstickA,chopstickB).start();
            new Philosopher("Jeff",chopstickB,chopstickC).start();
            new Philosopher("Lee",chopstickA,chopstickC).start();
        }
        
    }
}