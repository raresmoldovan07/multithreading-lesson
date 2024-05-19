package thread.creation.example.race;

public class RaceCondition {

	public static void main( String[] args ) {
		SharedClass sharedClass = new SharedClass();
		Thread thread = new Thread( () -> {
			for ( int i = 0; i < 100000; ++i ) {
				sharedClass.increment();
			}
		} );
		Thread thread1 = new Thread( () -> {
			for ( int i = 0; i < 100000; ++i ) {
				sharedClass.checkForDataRace();
			}
		} );

		thread.start();
		thread1.start();
	}

	/**
	 * Every shared variable that is modified by at least one thread should be either
	 * - volatile
	 * - guarded by synchronized block
	 */
	private static class SharedClass {
		private volatile int x = 0;
		private int y = 0;

		public void increment() {
			x++;
			y++; // they are not dependent so the operations can be executed in parallel
		}

		/**
		 * Compiler and CPU may execute the instructions out of order to optimize performance
		 * they will do so while maintaining the logical correctness of the code
		 */
		public void checkForDataRace() {
			if ( y > x ) {
				System.out.println( "Data race is detected" );
			}
		}
	}
}
