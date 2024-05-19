package thread.creation.example.pow;

import java.math.BigInteger;

public class ComplexCalculation {

	public static void main( String[] args ) throws InterruptedException {
		BigInteger a1 = BigInteger.valueOf( 1 );
		BigInteger a2 = BigInteger.valueOf( 2 );
		BigInteger a3 = BigInteger.valueOf( 3 );
		BigInteger a4 = BigInteger.valueOf( 0 );
		System.out.println( calculateResult( a1, a2, a3, a4 ) );
		System.out.println( calculateResult( a2, a1, a3, a4 ) );
		System.out.println( calculateResult( a1, a3, a3, a2 ) );
	}

	public static BigInteger calculateResult( BigInteger base1, BigInteger power1, BigInteger base2,
			BigInteger power2 ) throws InterruptedException {
		BigInteger result;
		PowerCalculatingThread thread1 = new PowerCalculatingThread( base1, power1 );
		PowerCalculatingThread thread2 = new PowerCalculatingThread( base2, power2 );

		thread1.start();
		thread2.start();

		thread1.join();
		thread2.join();

		return thread1.getResult().add( thread2.getResult() );
	}

	private static class PowerCalculatingThread extends Thread {
		private BigInteger result = BigInteger.ONE;
		private BigInteger base;
		private BigInteger power;

		public PowerCalculatingThread( BigInteger base, BigInteger power ) {
			this.base = base;
			this.power = power;
		}

		@Override
		public void run() {
			if ( power.equals( BigInteger.ZERO ) ) {
				return;
			}

			for ( BigInteger i = BigInteger.ONE; i.compareTo( power ) <= 0; i = i.add(
					BigInteger.ONE ) ) {
				this.result = result.multiply( base );
			}
		}

		public BigInteger getResult() {return result;}
	}
}