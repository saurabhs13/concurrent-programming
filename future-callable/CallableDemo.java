
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

class FactorialTask implements Callable<Long>{
    int number;
    

    public FactorialTask(int number) {
        this.number = number;
    }
    @Override
    public Long call() throws InterruptedException{
        final int number = this.number;
        long result = 1;
        for(int i=number;i>0;i--){
            result  = result * i;
        }
        Thread.sleep(3000);
        return result;
    }
    
}
public class CallableDemo{
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        FactorialTask task = new FactorialTask(10);
        System.out.println("Calculating factorial");
        
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future<Long> result  = executor.submit(task);
        System.out.println("Doing other stuff");
        System.out.println("Factorial result = "+result.get());
        executor.shutdown();
   }
}