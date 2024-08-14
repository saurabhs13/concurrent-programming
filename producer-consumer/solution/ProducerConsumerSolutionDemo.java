import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
class Node{
    int data;
    Node next;
}
class CustomLinkedBoundedQueue{
   Node head;
   int capacity;
   AtomicInteger queueLength = new AtomicInteger(0);

   Lock putLock = new ReentrantLock();
   Condition notFull = putLock.newCondition();
   Lock takeLock = new ReentrantLock();
   Condition notEmpty = takeLock.newCondition(); 

   public CustomLinkedBoundedQueue(){
       this(Integer.MAX_VALUE);
   }
   public CustomLinkedBoundedQueue(int capacity){
       this.capacity = capacity;
   }
   private Node createNode(int data){
       Node node = new Node();
       node.data = data;
       return node;
   }
   public void put(int data) throws InterruptedException{
        putLock.lock();
        while(queueLength.get()==capacity){
            System.out.println("Waiting to put items in the queue as it's full");
            notFull.await();
        }
        try {
            if(head ==null){
                head = createNode(data);
                queueLength.getAndIncrement();
                System.out.format("\n Adding value %d to queue",data);
                System.out.println(" Current Queue Length = "+queueLength.get());
               
            }else if(queueLength.get()<capacity){
                Node current = head;
                while(current.next !=null){
                    current = current.next;
                }
                current.next = createNode(data);
                queueLength.getAndIncrement();
                System.out.format("\n Adding value %d to queue",data);
                System.out.println(" Current Queue Length = "+queueLength.get());
               
            }else{
               throw new RuntimeException("Queue is full");
            }
        } finally {

            putLock.unlock();
        }
        signalNotEmpty();
       
   }
   public Node take() throws InterruptedException{
        takeLock.lock();
        Node current;
        while(queueLength.get() ==0){
            System.out.println("Waiting to take item from the queue as it's empty");
            notEmpty.await();
        }
        try {
                current = head;
                head = head.next;
                queueLength.getAndDecrement();
                System.out.println(" Removed element "+current.data+" from queue");
                System.out.println(" Current Queue Length = "+queueLength.get());
                
        } finally {
            takeLock.unlock();
        }
        signalNotFull();
        return current;
   }

    private void signalNotEmpty() {
        takeLock.lock();
        try {
            notEmpty.signal();
        } finally {
            takeLock.unlock();
        }
    }

 
    private void signalNotFull() {
        putLock.lock();
        try {
            notFull.signal();
        } finally {
            putLock.unlock();
        }
    }
   public void printQueueContents(){
       System.out.print("\n Queue Contents: \n");
       if(null == head){
           System.out.print("Queue is empty!");
       }else{
           Node current  = head;
           while(current.next!=null){
               System.out.print(current.data + " ");
               current = current.next;
           }
           System.out.println(current.data);
       }
   
   }


}
class ProducerThread extends Thread{

   CustomLinkedBoundedQueue queue;
   int capacity;
   int startValue;

   public ProducerThread(CustomLinkedBoundedQueue queue,int capacity,int startValue){
       this.queue = queue;
       this.capacity = capacity;
       this.startValue  =startValue;
   }

   public void run(){
       for(int i=this.startValue;i<(this.startValue+this.capacity);i++){
           try {
          //     System.out.format("\n Adding value %d to queue",i);
               this.queue.put(i);
              
           } catch (InterruptedException ex) {
                ex.printStackTrace();
           }   
   
       }
      
   }
}
class ConsumerThread extends Thread{

   CustomLinkedBoundedQueue queue;
   int capacity;


   public ConsumerThread(CustomLinkedBoundedQueue queue,int capacity){
       this.queue = queue;
       this.capacity = capacity;
   }

   public void run(){
       for(int i=0;i<this.capacity;i++){
           try {
               Node n = this.queue.take();
          //     System.out.println(" Removed element "+n.data+" from queue");
           } catch (InterruptedException ex) {
                ex.printStackTrace();
           }
       }
      
   }
}
public class ProducerConsumerSolutionDemo{
   public static void main(String[] args) throws InterruptedException{
       int capacity = 100;
       CustomLinkedBoundedQueue queue = 
           new CustomLinkedBoundedQueue(capacity);
       int startValue = 0;
       for(int i=0;i<5;i++){
        ProducerThread pt = new ProducerThread(queue,capacity,startValue);
        ConsumerThread ct = new ConsumerThread(queue,capacity);
        pt.start();
        ct.start();
        startValue = startValue + capacity;
       }
   }
}