
import java.util.concurrent.Semaphore;

class ConferenceCall extends Thread{

    private static Semaphore participantLicense = new Semaphore(4);
    

    public void run(){
        try {
            participantLicense.acquire();
            
            System.out.println(this.getName()+" joined the call");
          
            Thread.sleep(2000);
        } catch (InterruptedException ie) {
            ie.printStackTrace();
        }finally{
            System.out.println(this.getName()+" leaving the call");
            participantLicense.release();
        }
       
        
    }
}
public class SemaphoreDemo{
    public static void main(String[] args){
        for(int i=0;i<=10;i++){
            new ConferenceCall().start();
        }
    }
}