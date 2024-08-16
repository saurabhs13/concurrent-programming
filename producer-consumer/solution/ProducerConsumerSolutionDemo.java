import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;


class Node{

    Integer data;
    Node next;

    Node(Integer data){
        this.data = data;
    }
}
class CustomLinkedBoundedQueue{
   
   private Node head;
   private Node last;
   int capacity;

   private final AtomicInteger queueLength = new AtomicInteger(0);
   private final ReentrantLock putLock = new ReentrantLock(true);
   private final Condition notFull = putLock.newCondition();
   private final ReentrantLock takeLock = new ReentrantLock(true);
   private final Condition notEmpty = takeLock.newCondition(); 

   public CustomLinkedBoundedQueue(){
       this(Integer.MAX_VALUE);
   }
   public CustomLinkedBoundedQueue(int capacity){
       this.capacity = capacity;
       head = last = new Node(null);
   }
   private Node createNode(Integer data){
       Node node = new Node(data);
       return node;
   }
   public void put(int data) throws InterruptedException{
        
        int c = -1;
        final AtomicInteger queueLength = this.queueLength;
        final ReentrantLock putLock = this.putLock;
        putLock.lock();
        while(queueLength.get()==capacity){
        //    System.out.println("Waiting to put items in the queue as it's full");
            notFull.await();
        }
        try{
                last = last.next = createNode(data);
                c = queueLength.getAndIncrement();
          //     System.out.format("\n Adding value %d to queue",data);
           //    System.out.println(" Current Queue Length = "+c+1);
                if(c+1 < capacity){
                    notFull.signal();
                }
            
        }finally {

            putLock.unlock();
        }
        if(c ==0){
            signalNotEmpty();

        }
       
   }
   public Integer take() throws InterruptedException{
        
        int c = -1;
        final AtomicInteger queueLength = this.queueLength;
        final ReentrantLock takeLock = this.takeLock;
        takeLock.lock();
        Integer item = -1;
        while(queueLength.get() ==0){
        //    System.out.println("Waiting to take item from the queue as it's empty");
            notEmpty.await();
        }
        try {
            Node h = head;
            Node first = h.next;
            h.next = h; // help GC
            head = first;
            item = first.data;
            first.data = null;
                c = queueLength.getAndDecrement();
          //     System.out.println(" Removed element "+item+" from queue");
              System.out.println("Element Removed = "+item+" Current Queue Length = "+queueLength.get());
                if(c > 1){
                    notEmpty.signal();
                }
                
        } finally {
            takeLock.unlock();
        }
        if (c == capacity) {
            signalNotFull();
        }
        return item;
   }

    private void signalNotEmpty() {
        takeLock.lock();
        try {
        //    System.out.println(" Queue Length = "+ queueLength.get()+" Signaling not empty");
            notEmpty.signal();
          
        } finally {
            takeLock.unlock();
        }
    }

 
    private void signalNotFull() {
        putLock.lock();
        try {
        //    System.out.println("Queue Length = "+ queueLength.get()+" Signaling not full");
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
           //   System.out.format("\n Adding value %d to queue",i);
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
               Integer n = this.queue.take();
            //   System.out.println(" Removed element "+n+" from queue");
           } catch (InterruptedException ex) {
                ex.printStackTrace();
           }
       }
      
   }
}
public class ProducerConsumerSolutionDemo{
   public static void main(String[] args) throws InterruptedException{
       int capacity = Integer.parseInt(args[1]);
       CustomLinkedBoundedQueue queue = new CustomLinkedBoundedQueue(capacity);
       int startValue = 0;
       int numberOfThreads = Integer.parseInt(args[0]);
       for(int i=0;i<numberOfThreads;i++){
        ProducerThread pt = new ProducerThread(queue,capacity,startValue);
        ConsumerThread ct = new ConsumerThread(queue,capacity);
        pt.start();
        ct.start();
        startValue = startValue + capacity;
       }
   }
}