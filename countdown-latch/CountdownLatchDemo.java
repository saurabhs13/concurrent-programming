
import java.util.concurrent.CountDownLatch;

class ConferenceCall extends Thread{

    private static CountDownLatch latch = new CountDownLatch(11);
    

    public void run(){
        try {
            System.out.println(this.getName()+" waiting for everyone to join to start the call");
            latch.countDown();
            latch.await();
            System.out.println(this.getName()+" joined the call");
            Thread.sleep(2000);
        } catch (InterruptedException ie) {
            ie.printStackTrace();
        }finally{
            System.out.println(this.getName()+" leaving the call");
           
        }
        
    }
}
public class CountdownLatchDemo{
    public static void main(String[] args){
        for(int i=0;i<=10;i++){
            new ConferenceCall().start();
        }
    }
}