class Node{
    int data;
    Node next;
}
class CustomLinkedBoundedQueue{
   Node head;
   int capacity;
   int queueLength;
 /**  
   Lock putLock = new ReentrantLock();
   Condition notFull = putLock.newCondition();
   Lock takeLock = new ReentrantLock();
   Condition notEmpty = takeLock.newCondition(); */

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
   public void put(int data){
       if(head ==null){
           head = createNode(data);
       }else if(queueLength<capacity){
           Node current = head;
           while(current.next !=null){
               current = current.next;
           }
           current.next = createNode(data);
           queueLength++;
       }else{
          throw new RuntimeException("Queue is full");
       }
   }
   public Node take(){
       if(head !=null){
           Node current = head;
           head = head.next;
           queueLength--;
           return current;
       }else{
           throw new RuntimeException("Queue is empty");
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
           System.out.format("\n Adding value %d to queue",i);
           this.queue.put(i);
       /**  try {
               Thread.sleep(1);
           } catch (InterruptedException ex) {
           }*/   
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
           Node n = this.queue.take();
           System.out.println(" Removed element "+n.data+" from queue");
           try {
               Thread.sleep(10);
           } catch (InterruptedException ex) {
           }
       }
      
   }
}
public class ProducerConsumerProblemDemo{
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