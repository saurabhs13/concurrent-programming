
import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;


class SequentialMergeSorter{
    int[] arr;
    public SequentialMergeSorter(int[] arr){
        this.arr = arr;
    }
    public int[] sort(){
        sort(0,arr.length-1);
        return arr;
    }
    private void sort(int startIndex,int endIndex){
        if(startIndex<endIndex){
            int mid = (startIndex+endIndex)/2;
            sort(startIndex,mid);
            sort(mid+1,endIndex);
            merge(startIndex,mid,endIndex);
        }
    }
    private void merge(int startIndex,int mid,int endIndex){
        int[] leftArray = Arrays.copyOfRange(arr, startIndex, mid+1);
        int[] rightArray = Arrays.copyOfRange(arr, mid+1, endIndex+1);
        int currentLeftIndex = 0,currentRightIndex = 0,currentMainIndex = startIndex;
        while(currentLeftIndex<leftArray.length&&currentRightIndex<rightArray.length){
            if(leftArray[currentLeftIndex]<=rightArray[currentRightIndex]){
                arr[currentMainIndex] = leftArray[currentLeftIndex];
                currentLeftIndex++;
            }else{
                arr[currentMainIndex] = rightArray[currentRightIndex];
                currentRightIndex++;
            }
            currentMainIndex++;
        }
        while(currentLeftIndex < leftArray.length){
            arr[currentMainIndex] = leftArray[currentLeftIndex];
            currentLeftIndex++;
            currentMainIndex++;
        }
        while(currentRightIndex < rightArray.length){
            arr[currentMainIndex] = rightArray[currentRightIndex];
            currentRightIndex++;
            currentMainIndex++;
        }
    }
}
class ParallelMergeSorter{
    int[] arr;
    public ParallelMergeSorter(int[] arr){
        this.arr = arr;
    }
    public int[] sort(){
        int numWorkers = Runtime.getRuntime().availableProcessors();
        ForkJoinPool pool = new ForkJoinPool(numWorkers);
        pool.invoke(new ParallelWorker(0,arr.length-1));
        return arr;
    }
    private class ParallelWorker extends RecursiveAction{

        private int startIndex,endIndex;

        public ParallelWorker(int startIndex,int endIndex){
            this.startIndex = startIndex;
            this.endIndex = endIndex;
        }

        @Override
        protected void compute(){
            if(startIndex<endIndex){
                int mid = (startIndex+endIndex)/2;
                ParallelWorker leftWorker = new ParallelWorker(startIndex,mid);
                ParallelWorker rightWorker = new ParallelWorker(mid+1,endIndex);
                invokeAll(leftWorker,rightWorker);
                merge(startIndex,mid,endIndex);
            }
        }
        private void merge(int startIndex,int mid,int endIndex){
            int[] leftArray = Arrays.copyOfRange(arr, startIndex, mid+1);
            int[] rightArray = Arrays.copyOfRange(arr, mid+1, endIndex+1);
            int currentLeftIndex = 0,currentRightIndex = 0,currentMainIndex = startIndex;
            while(currentLeftIndex<leftArray.length&&currentRightIndex<rightArray.length){
                if(leftArray[currentLeftIndex]<=rightArray[currentRightIndex]){
                    arr[currentMainIndex] = leftArray[currentLeftIndex];
                    currentLeftIndex++;
                }else{
                    arr[currentMainIndex] = rightArray[currentRightIndex];
                    currentRightIndex++;
                }
                currentMainIndex++;
            }
            while(currentLeftIndex < leftArray.length){
                arr[currentMainIndex] = leftArray[currentLeftIndex];
                currentLeftIndex++;
                currentMainIndex++;
            }
            while(currentRightIndex < rightArray.length){
                arr[currentMainIndex] = rightArray[currentRightIndex];
                currentRightIndex++;
                currentMainIndex++;
            }
        }
    }
}

public class ParallelMergeSortDemo{

    static int[] generateRandomArray(int length){
        int[] arr = new int[length];
        Random random = new Random();
        for(int i=0;i<arr.length;i++){
            arr[i] = random.nextInt(0, length);
        }
        return arr;
    }
    public static void main(String[] args) {
        final int NUM_EVALS = 5;
        int[] arr = generateRandomArray(1_00_000_000);
        SequentialMergeSorter sorter = new SequentialMergeSorter(Arrays.copyOf(arr, arr.length));
        int[] sortedArr = sorter.sort();
        double sequentialTime = 0;
        for(int i=0;i<NUM_EVALS;i++){
            sorter =  new SequentialMergeSorter(Arrays.copyOf(arr, arr.length));
            long start = System.currentTimeMillis();
            sorter.sort();
            sequentialTime += System.currentTimeMillis() - start;
        }
        sequentialTime /=NUM_EVALS;
        
        ParallelMergeSorter parallelSorter = new ParallelMergeSorter(Arrays.copyOf(arr, arr.length));

        int[] parSortedArr = parallelSorter.sort();
        double parallelTime = 0;
        for(int i=0;i<NUM_EVALS;i++){
            parallelSorter =  new ParallelMergeSorter(Arrays.copyOf(arr, arr.length));
            long start = System.currentTimeMillis();
            parallelSorter.sort();
            parallelTime += System.currentTimeMillis() - start;
        }
        parallelTime /=NUM_EVALS;
        System.out.print("\n");
        System.out.println("Parallel time = "+parallelTime+" ");
        System.out.println("Sequential time = "+sequentialTime+" ");
        System.out.println("Speedup = "+sequentialTime/parallelTime);
        System.out.println("Efficiency = "+100*(sequentialTime/parallelTime)/Runtime.getRuntime().availableProcessors());
      
    }
}