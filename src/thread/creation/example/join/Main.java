package thread.creation.example.join;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Do not rely on the order of execution
 * Always use thread coordination
 * Design code for worst case scenario
 * Threads may take unreasonably long time
 * Always use the Thread.join() with a time limit
 * Stop the thread if it is not done in time
 */
public class Main {

	public static void main( String[] args ) throws InterruptedException {
		List<Long> numbers = Arrays.asList( 1000000L, 323L, 35435L, 2324L, 4656L);
		List<FactorialThread> threads = new ArrayList<>();

		for (long inputNumber: numbers) {
			threads.add( new FactorialThread(inputNumber) );
		}

		for ( FactorialThread thread : threads ) {
			thread.setDaemon( true );
			thread.start();
		}

		for ( FactorialThread factorialThread : threads ) {
			factorialThread.join(10000);
		}

		for (FactorialThread thread : threads) {
			if (thread.isFinished) {
				System.out.println("Factorial of " + thread.getInputNumber() + " is done");
			} else {
				System.out.println("The calculation of " + thread.getInputNumber() + " is still in progress...");
			}
		}

	}

	static class FactorialThread extends Thread {
		private long inputNumber;
		private BigInteger result = BigInteger.ZERO;
		private boolean isFinished = false;

		public FactorialThread( final long inputNumber ) {
			this.inputNumber = inputNumber;
		}

		@Override
		public void run() {
			this.result = factorial(inputNumber);
			this.isFinished = true;
		}

		public BigInteger factorial(long n) {
			BigInteger result = BigInteger.ONE;
			for (long i = n; i > 0; --i) {
				result = result.multiply( new BigInteger( Long.toString( i ) ) );
			}
			return result;
		}

		public boolean isFinished() {
			return isFinished;
		}

		public BigInteger getResult() {
			return result;
		}

		public long getInputNumber() {
			return inputNumber;
		}
	}
}
