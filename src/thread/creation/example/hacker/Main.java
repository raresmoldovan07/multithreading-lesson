package thread.creation.example.hacker;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Main {

	private static final int MAX_PASSWORD = 9999;

	public static void main( String[] args ) throws InterruptedException {
		Random random = new Random();
		Vault vault = new Vault( random.nextInt(MAX_PASSWORD) );

		List<Thread> threadList = new ArrayList<>();
		threadList.add( new AscendingHackerThread( vault ) );
		threadList.add( new DescendingHackerThread( vault ) );
		threadList.add( new PoliceThread() );

		for ( final Thread thread : threadList ) {
			thread.start();
		}
	}

	private static class Vault {
		private final int password;

		public Vault( int password ) {
			System.out.println("The password is: " + password);
			this.password = password;
		}

		public boolean isCorrectPassword( int guess ) {
			try {
				Thread.sleep( 5 );
			} catch ( InterruptedException e ) {

			}
			return this.password == guess;
		}

	}

	private static abstract class HackerThread extends Thread {

		protected Vault vault;

		public HackerThread( final Vault vault ) {
			this.vault = vault;
			this.setName( this.getClass().getName() );
			this.setPriority( Thread.MAX_PRIORITY );
		}

		@Override
		public synchronized void start() {
			super.start();
		}
	}

	private static class AscendingHackerThread extends HackerThread {

		public AscendingHackerThread( final Vault vault ) {
			super( vault );
		}

		@Override
		public void run() {
			System.out.println("AscendingHacker starting");
			for ( int i = 0; i < MAX_PASSWORD; i++ ) {
				if ( vault.isCorrectPassword( i ) ) {
					System.out.println( this.getName() + " guessed the password " + i );
					System.exit( 0 );
				}
			}
		}
	}

	private static class DescendingHackerThread extends HackerThread {

		public DescendingHackerThread( final Vault vault ) {
			super( vault );
		}

		@Override
		public void run() {
			System.out.println("DescendingHacker starting");
			for ( int i = MAX_PASSWORD; i >= 0; --i ) {
				if ( vault.isCorrectPassword( i ) ) {
					System.out.println( this.getName() + " guessed the password " + i );
					System.exit( 0 );
				}
			}
		}
	}

	private static class PoliceThread extends Thread {
		@Override
		public void run() {
			System.out.println("PoliceThread starting");
			for ( int i = 10; i > 0; --i ) {
				try {
					Thread.sleep( 1000 );
				} catch ( InterruptedException e ) {
				}
				System.out.println( i );
			}
			System.out.println( "Game over for you hackers" );
			System.exit( 0 );
		}
	}
}
