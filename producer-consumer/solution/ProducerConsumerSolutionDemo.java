import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
class Node{
    Integer data;
    Node next;

    Node(Integer data){
        this.data = data;
    }
}
class CustomLinkedBoundedQueue{
   Node head;
   int capacity;
   AtomicInteger queueLength = new AtomicInteger(0);

   Lock putLock = new ReentrantLock(true);
   Condition notFull = putLock.newCondition();
   Lock takeLock = new ReentrantLock(true);
   Condition notEmpty = takeLock.newCondition(); 

   public CustomLinkedBoundedQueue(){
       this(Integer.MAX_VALUE);
   }
   public CustomLinkedBoundedQueue(int capacity){
       this.capacity = capacity;
       head = new Node(null);
   }
   private Node createNode(Integer data){
       Node node = new Node(data);
       node.data = data;
       return node;
   }
   public void put(int data) throws InterruptedException{
        
        int c = -1;
        putLock.lock();
        while(queueLength.get()==capacity){
            System.out.println("Waiting to put items in the queue as it's full");
            notFull.await();
        }
        Node h = head;
        try {
            if(h.data ==null){
                h.data = data;
                c = queueLength.getAndIncrement();
                System.out.format("\n Adding value %d to queue",data);
                System.out.println(" Current Queue Length = "+c+1);
               
            }else{
                while(h.next !=null){
                    h = h.next;
                }
                h.next = createNode(data);
                c = queueLength.getAndIncrement();
                System.out.format("\n Adding value %d to queue",data);
                System.out.println(" Current Queue Length = "+c+1);
                if(c+1 < capacity){
                    notFull.signal();
                }
            }
        } finally {

            putLock.unlock();
        }
        if(c ==0){
            signalNotEmpty();

        }
       
   }
   public Integer take() throws InterruptedException{
        int c = -1;
        takeLock.lock();
        int item = -1;
        AtomicInteger queueLength = this.queueLength;
        while(queueLength.get() ==0){
            System.out.println("Waiting to take item from the queue as it's empty");
            notEmpty.await();
        }
        try {
                Node current = head;
                item = current.data;
                Node first = head.next;
                if(null == first){
                    head = createNode(null);
                }
                c = queueLength.getAndDecrement();
                System.out.println(" Removed element "+current.data+" from queue");
                System.out.println(" Current Queue Length = "+queueLength.get());
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
            System.out.println("Queue Length = "+ queueLength.get()+" Signaling not empty");
            notEmpty.signal();
          
        } finally {
            takeLock.unlock();
        }
    }

 
    private void signalNotFull() {
        putLock.lock();
        try {
            System.out.println("Queue Length = "+ queueLength.get()+" Signaling not full");
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
               int n = this.queue.take();
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
       for(int i=0;i<1000;i++){
        ProducerThread pt = new ProducerThread(queue,capacity,startValue);
        ConsumerThread ct = new ConsumerThread(queue,capacity);
        pt.start();
        ct.start();
        startValue = startValue + capacity;
       }
   }
}