package thread.creation.example.atomic;

import java.util.Random;

/**
 * Atomic operations: getters, setters, all assignments to primitive types except long and double,
 * assignments to references
 * Long and double are 64 bits long (requires 2 operations)
 * Use volatile keyword (volatile double x = 1.0;)
 */
public class Main {

	public static void main( String[] args ) {
		Metrics metrics = new Metrics();

		BusinessLogic businessLogic = new BusinessLogic( metrics );
		BusinessLogic businessLogic2 = new BusinessLogic( metrics );
		MetricsPrinter metricsPrinter = new MetricsPrinter( metrics );

		businessLogic.start();
		businessLogic2.start();
		metricsPrinter.start();
	}

	private static class MetricsPrinter extends Thread {
		private Metrics metrics;

		private MetricsPrinter( final Metrics metrics ) {this.metrics = metrics;}

		@Override
		public void run() {
			while ( true ) {
				try {
					Thread.sleep( 100 );
				} catch ( InterruptedException e ) {
					e.printStackTrace();
				}
				double currentAverage = metrics.getAverage();
				System.out.println( currentAverage );
			}
		}
	}

	private static class BusinessLogic extends Thread {
		private Metrics metrics;
		private Random random = new Random();

		public BusinessLogic( final Metrics metrics ) {
			this.metrics = metrics;
		}

		@Override
		public void run() {
			while ( true ) {
				long start = System.currentTimeMillis();
				try {
					Thread.sleep( random.nextInt( 10 ) ); //we except an average of 5 milliseconds
				} catch ( InterruptedException e ) {
					e.printStackTrace();
				}
				long end = System.currentTimeMillis();
				metrics.addSample( end - start );
			}
		}
	}

	private static class Metrics {
		private long count = 0;
		private volatile double average = 0.0;

		public synchronized void addSample( long sample ) {
			double currentSum = average * count;
			count++;
			average = ( currentSum + sample ) / count; // is atomic because of "volatile"
		}

		public double getAverage() {
			return average;
		}
	}
}
