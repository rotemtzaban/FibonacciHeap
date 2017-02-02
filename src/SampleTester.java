import java.util.concurrent.TimeUnit;

public class SampleTester {
    public static void testTime1(int m){
		FibonacciHeap h =  new FibonacciHeap();
		long start_time = System.nanoTime();
        for (int i = m; i >= 1; i--) {
            h.insert(m - i);
        }
        long end_time = System.nanoTime();
        double difference = (end_time - start_time)/1e6;
//        System.out.println(difference);
	}

	public static void testTime(int m){
		FibonacciHeap h =  new FibonacciHeap();
		long start_time = System.nanoTime();
		for (int i = m; i >= 1; i--) {
			h.insert(m - i);
		}
		long end_time = System.nanoTime();
		double difference = ((double)(end_time - start_time))/1e6;
		System.out.println(difference);
	}

    public static void main(String[] args) throws InterruptedException {
        FibonacciHeap h1 = new FibonacciHeap();
        FibonacciHeap h2 = new FibonacciHeap();
        FibonacciHeap h3 = new FibonacciHeap();
        //TimeUnit.SECONDS.sleep(2);
		for (int i = 1; i <= 10; i++) {
			testTime1(1000 * i);
		}
		for (int i = 1; i <= 10; i++) {
			testTime(1000 * i);
		}
    }
}
