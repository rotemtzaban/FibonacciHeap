import java.util.concurrent.TimeUnit;

public class SampleTester {
    public static void testTime(FibonacciHeap h, int m){
        long start_time = System.nanoTime();
        for (int i = 0; i < m; i++) {
            h.insert(m - i);
        }
        long end_time = System.nanoTime();
        double difference = (end_time - start_time)/1e6;
        System.out.println(difference);
    }

    public static void main(String[] args) throws InterruptedException {
        FibonacciHeap h1 = new FibonacciHeap();
        FibonacciHeap h2 = new FibonacciHeap();
        FibonacciHeap h3 = new FibonacciHeap();
        TimeUnit.SECONDS.sleep(2);
        testTime(h1,1000); //change m to 1000 2000 and 3000. my problem was with 3000 that was too similar to 2000.
    }
}
