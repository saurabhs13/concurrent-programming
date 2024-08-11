
import java.util.concurrent.locks.ReentrantLock;

class TankFillerThread extends Thread{
    private static int maxTankVolume = 25;
    private static int currentTankVolume = 0;
    private int waterBuckets = 0;
    private static ReentrantLock tankLock = new ReentrantLock();
    public TankFillerThread(String name){
        this.setName(name);
    }

    /**
     * Run method with usual reentrant lock implementation
     * the execution time for version of code to fill the tank
     * is approx. 6-8 seconds.
     */
   /*  public void run(){
        while(currentTankVolume < maxTankVolume){
            if(waterBuckets>0){
                try {
                    tankLock.lock();
                    currentTankVolume = currentTankVolume+waterBuckets;
                    System.out.println(this.getName()+" added "+waterBuckets+" bucket(s) of water into tank");
                    waterBuckets = 0;
                    Thread.sleep(300);
                }  catch (InterruptedException ex) {
                    ex.printStackTrace();
                }finally {
                    tankLock.unlock();
                }
            }else{
                waterBuckets++;
                System.out.println(this.getName()+" fetched "+waterBuckets+" bucket(s) of water.");
                try {
                    Thread.sleep(100);
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
            }
        }
    } */

    /**
     * Try lock version of run method executes in approx. 2 seconds so 3X faster.
     * 
     */
    public void run(){
        while(currentTankVolume < maxTankVolume){
            if((waterBuckets>0) && tankLock.tryLock()){
                try {
                    currentTankVolume = currentTankVolume+waterBuckets;
                    System.out.println(this.getName()+" added "+waterBuckets+" bucket(s) of water into tank");
                    waterBuckets = 0;
                    Thread.sleep(300);
                }  catch (InterruptedException ex) {
                    ex.printStackTrace();
                }finally {
                    tankLock.unlock();
                }
            }else{
                try {
                    Thread.sleep(100);
                    waterBuckets++;
                    System.out.println(this.getName()+" fetched "+waterBuckets+" bucket(s) of water.");
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }
}

public class TryLockDemo{
    public static void main(String[] args) {
        TankFillerThread fillerThread1 = new TankFillerThread("Filler 1");
        TankFillerThread fillerThread2 = new TankFillerThread("Filler 2");
        long startTime = System.currentTimeMillis();
        fillerThread1.start();
        fillerThread2.start();
        try {
            fillerThread1.join();
            fillerThread2.join();
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }
        long endTime = System.currentTimeMillis();
        long elapsedTime = (endTime - startTime)/1000;
       // System.out.println("Final Tank Volume: "+TankFillerThread.currentTankVolume);
        System.out.println("Total time to fill tanks in seconds: "+elapsedTime );

    }
}