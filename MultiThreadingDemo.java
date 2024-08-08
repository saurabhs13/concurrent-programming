/**
 * This is a simple demo that starts few infinitely running threads 
 * we can then use the os utilities like ps, top n system monitor to
 * see CPU utilization and number of threads utilized by java process.
 */
class InfinitelyRunningThread extends Thread{

    public void run(){
        while (true) { }
    }
}

public class MultiThreadingDemo{

    public static void main(String[] args){

        Runtime rt = Runtime.getRuntime();
        long usedKB = (rt.totalMemory() - rt.freeMemory()) /1024;
        System.out.format("  Process ID: %d\n",ProcessHandle.current().pid());
        System.out.format("  Thread Count: %d\n",Thread.activeCount());
        System.out.format("  Memory Usage: %d KB\n",usedKB);

        // Start few infinitely running threads

        for(int i=0;i<=2;i++){
            new InfinitelyRunningThread().start();
        }
        try {
            Thread.sleep(20000);
        } catch (InterruptedException ex) {
        }
        usedKB = (rt.totalMemory() - rt.freeMemory()) /1024;
        System.out.format("  Process ID: %d\n",ProcessHandle.current().pid());
        System.out.format("  Thread Count: %d\n",Thread.activeCount());
        System.out.format("  Memory Usage: %d KB\n",usedKB);
    }
}