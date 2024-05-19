package thread.creation.example.synchronize;

/**
 * Synchronized block is Reentrant
 * A thread cannot prevent itself from entering a critical section
 * We should use it as less as possible
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

		System.out.println(counter.getItems());
	}

	private static class DecrementingThread extends Thread {
		private Counter counter;

		public DecrementingThread( final Counter counter ) {
			this.counter = counter;
		}

		@Override
		public void run() {
			for (int i = 0; i < 10000; ++i) {
				counter.decrement();
			}
		}
	}

	private static class IncrementingThread extends Thread {
		private Counter counter;

		public IncrementingThread( final Counter counter ) {
			this.counter = counter;
		}

		@Override
		public void run() {
			for (int i = 0; i < 10000; ++i) {
				counter.increment();
			}
		}
	}

	private static class Counter {
		private int items = 0;
		Object lock = new Object();

		public synchronized void increment() {
			synchronized ( lock ) {
				items++;
			}
		}

		public synchronized void decrement() {
			synchronized ( lock ) {
				items--;
			}
		}

		public int getItems() {
			return items;
		}
	}
}
