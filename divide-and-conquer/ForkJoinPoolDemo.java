
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;

/**
 * This program demonstrates the use of fork join pool
 * to parallelize the divide and conquer algoritms. It also 
 * measure diffreence in execution time.
 * In my local system I observed the following results:
 * Parallel execution with warm up = 15 ms
 * Parallel execution without warm up = 55 ms
 * Sequential execution = 218 ms
 */
class RecursiveSum extends RecursiveTask<Long>{
    private long low,high;

    public RecursiveSum(long low,long high){
        this.low = low;
        this.high = high;
    }

    @Override
    protected Long compute() {
        long total = 0;
        if(high-low <=100_000){
            for(long i=0;i<=(high-low);i++){
                total = total +i;
            }
            return total;
        }else{
            long mid = (high+low)/2;
            RecursiveSum leftSum = new RecursiveSum(low,mid);
            RecursiveSum rightSum = new RecursiveSum(mid+1,high);
            leftSum.fork();
            return rightSum.compute()+leftSum.join();
        }
    }
}
public class ForkJoinPoolDemo{

    static long sum(long low,long high){
        long total = 0;
        if(high-low <=100_000){
            for(long i=0;i<=(high-low);i++){
                total = total +i;
            }
            return total;
        }else{
            long mid = (high+low)/2;
            return sum(low,mid)+sum(mid+1,high);
        }
    }
    public static void main(String[] args) {

        ForkJoinPool pool = ForkJoinPool.commonPool();
        long total_warm_up  = pool.invoke(new RecursiveSum(0,1_000_000_000)); //warm up

        long start = System.currentTimeMillis();
        Long total1 = 0l;
        try {
            total1  = pool.invoke(new RecursiveSum(0,1_000_000_000));
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        pool.shutdown();
        long end = System.currentTimeMillis();

        System.out.println("Total = "+total1);
        System.out.println("Parallel Execution Time: "+ (end-start));
        long start2 = System.currentTimeMillis();
        long total = sum(0,1_000_000_000);
        long end2 = System.currentTimeMillis();
        System.out.println("Total = "+total);
        System.out.println("Sequential Execution Time: "+ (end2-start2));
    }
}