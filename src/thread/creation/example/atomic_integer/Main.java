package thread.creation.example.atomic_integer;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * The result must be 0
 * Atomic integer should be used only when atomic operations are needed
 */
public class Main {

	public static void main( String[] args ) throws InterruptedException {
		Counter counter = new Counter();
		IncrementingThread incrementingThread = new IncrementingThread( counter );
		DecrementingThread decrementingThread = new DecrementingThread( counter );

		incrementingThread.start();
		decrementingThread.start();

		incrementingThread.join();
		decrementingThread.join();

		System.out.println( counter.getItems() );
	}

	private static class DecrementingThread extends Thread {
		private final Counter counter;

		public DecrementingThread( final Counter counter ) {
			this.counter = counter;
		}

		@Override
		public void run() {
			for ( int i = 0; i < 10000; ++i ) {
				counter.decrement();
			}
		}
	}

	private static class IncrementingThread extends Thread {
		private final Counter counter;

		public IncrementingThread( final Counter counter ) {
			this.counter = counter;
		}

		@Override
		public void run() {
			for ( int i = 0; i < 10000; ++i ) {
				counter.increment();
			}
		}
	}

	private static class Counter {
		private final AtomicInteger items = new AtomicInteger( 0 );

		public void increment() {
			items.incrementAndGet();
		}

		public void decrement() {
			items.decrementAndGet();
		}

		public int getItems() {
			return items.get();
		}
	}
}
