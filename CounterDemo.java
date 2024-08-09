/**
 * Program to demonstrate data race concept.
 * This program also shows that count++ is not an atomic operation
 * because it involves three operations reading the count value, incrementing the value by 1
 * and then writing the result back to memory.
 */
class CounterThread extends Thread{

    static int count = 0;

    public void run(){

        System.out.println(Thread.currentThread().getName()+ " is executing");
        for(int i=1;i<=1_00_000;i++){
            count++;
        }    
    }

}
public class CounterDemo{

    public static void main(String[] args){
     
        CounterThread counter1 = new CounterThread();
        CounterThread counter2 = new CounterThread();
        counter1.start();
        counter2.start();
        try {
            counter1.join();
        }catch (InterruptedException e) {
            e.printStackTrace();
        }
        try {
            counter2.join();
        }catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("Final count: "+ CounterThread.count);
    }

}