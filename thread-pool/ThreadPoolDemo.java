

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

class WorkerThread extends Thread{

     public void run(){
         try {
             System.out.println(this.getName()+" is working");
             Thread.sleep(100);
         } catch (InterruptedException ex) {
         }
    }
}

public class ThreadPoolDemo{
    public static void main(String[] args) {
     
        int processors = Runtime.getRuntime().availableProcessors();
        System.out.println("Processors = "+processors);
        ExecutorService threadPool = Executors.newFixedThreadPool(processors);
        for(int i=0;i<=1000;i++){
            threadPool.submit(new WorkerThread());
        }

        threadPool.shutdown();
    }
}