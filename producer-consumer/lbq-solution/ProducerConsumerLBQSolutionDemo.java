import java.util.concurrent.LinkedBlockingQueue;


class ProducerThread extends Thread{

   LinkedBlockingQueue<Integer> queue;
   int capacity;
   int startValue;

   public ProducerThread(LinkedBlockingQueue queue,int capacity,int startValue){
       this.queue = queue;
       this.capacity = capacity;
       this.startValue  =startValue;
   }

   public void run(){
       for(int i=this.startValue;i<(this.startValue+this.capacity);i++){
           try {
               System.out.format("\n Adding value %d to queue",i);
               this.queue.put(i);
              
           } catch (InterruptedException ex) {
                ex.printStackTrace();
           }   
   
       }
      
   }
}
class ConsumerThread extends Thread{

   LinkedBlockingQueue<Integer> queue;
   int capacity;


   public ConsumerThread(LinkedBlockingQueue queue,int capacity){
       this.queue = queue;
       this.capacity = capacity;
   }

   public void run(){
       for(int i=0;i<this.capacity;i++){
           try {
               Integer node = this.queue.take();
               System.out.println(" Removed element "+node+" from queue");
           } catch (InterruptedException ex) {
                ex.printStackTrace();
           }
       }
      
   }
}
public class ProducerConsumerLBQSolutionDemo{
   public static void main(String[] args) throws InterruptedException{
       int capacity = 100;
       LinkedBlockingQueue<Integer> queue = 
           new LinkedBlockingQueue(capacity);
       int startValue = 0;
       for(int i=0;i<1000;i++){
        ProducerThread pt = new ProducerThread(queue,capacity,startValue);
        ConsumerThread ct = new ConsumerThread(queue,capacity);
        pt.start();
        ct.start();
        startValue = startValue + capacity;
       }
   }
}