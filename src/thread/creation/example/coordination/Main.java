package thread.creation.example.coordination;

import java.math.BigInteger;

public class Main {

	public static void main( String[] args ) {
		Thread thread = new Thread(new LongComputationTask( new BigInteger( "2000000" ), new BigInteger( "10000000" ) ));
		thread.setDaemon( true );
		thread.start();
		thread.interrupt();
	}

	private static class BlockingTask implements Runnable {

		@Override
		public void run() {
			try {
				Thread.sleep( 1000 );
			} catch ( InterruptedException e ) {
				System.out.println("Exiting blocking thread");
			}
		}
	}

	private static class LongComputationTask implements Runnable {

		private BigInteger base;
		private BigInteger power;

		public LongComputationTask( final BigInteger base, final BigInteger power ) {
			this.base = base;
			this.power = power;
		}

		@Override
		public void run() {
			System.out.println(base + "^" + power + " = " + pow(base,  power));
		}

		private BigInteger pow(BigInteger base, BigInteger power) {
			BigInteger result = BigInteger.ONE;

			for ( BigInteger i = BigInteger.ZERO; i.compareTo( power ) != 0 ;  i = i.add( BigInteger.ONE ) ) {
				result = result.multiply( base );
			}
			return result;
		}
	}
}
