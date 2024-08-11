
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.ReadLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.WriteLock;

class TimerThread extends Thread{
    static long currentTimeMillis = System.currentTimeMillis();
    private static ReentrantReadWriteLock timerLock = new ReentrantReadWriteLock();
    private static ReadLock readLock = timerLock.readLock();
    private static WriteLock writeLock = timerLock.writeLock();

    static int totalUpdates = 0;

    public TimerThread(String name){
        this.setName(name);
    }
    public void run(){
        while(totalUpdates<10){
            if(this.getName().contains("Writer")){
                try {
                    System.out.println(this.getName() + " trying to acquire write lock.");
                    writeLock.lock();
                    System.out.println(this.getName() + " acquired write lock.");
                    currentTimeMillis = System.currentTimeMillis();
                    System.out.println(this.getName()+ " updated the current time.");
                 //   Thread.sleep(300);
                    totalUpdates++;
                } catch (Exception e) {
                    e.printStackTrace();
                }finally{
                    writeLock.unlock();
                    System.out.println(this.getName() + " released write lock.");
                }
            }else{
                try {
                  //  System.out.println(this.getName() + " trying to acquire read lock.");
                    readLock.lock();
                    Thread.sleep(200);
                  //  System.out.println(this.getName() + " acquired read lock.");
                    Instant instant = Instant.ofEpochMilli(currentTimeMillis);
                    LocalDateTime dateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
                    System.out.println(this.getName()+ " read local date time: " +dateTime +" no. of reader "+ timerLock.getReadLockCount());
                } catch (Exception e) {
                    e.printStackTrace();
                }finally{
                    readLock.unlock();
                   // System.out.println(this.getName() + " released read lock.");
                }
            }
        }   
    }
}
public class ReadWriteLockDemo{
    public static void main(String[] args){
        for(int i=0;i<10;i++){
            new TimerThread("Reader"+i).start();
        }
        for(int i=0;i<2;i++){
            new TimerThread("Writer"+i).start();
        }
    }
}