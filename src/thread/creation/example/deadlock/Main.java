package thread.creation.example.deadlock;

import java.util.Random;

/**
 * Conditions to deadlock:
 * mutual exclusion - only one thread can have exclusive access to a resource
 * hold and wait - at least one thread is holding a resource and is waiting for another resource
 * non-preemptive allocation - a resource is released only after the thread is done using it
 * circular wait - a chain of at least 2 threads each one is holding one resource and waiting for another resource
 *
 * Best solutions:
 * enforcing a strict order on lock acquisition prevents deadlocks (easy to do with a small number of locks,
 * but hard to accomplish if there are many locks in different places
 */
public class Main {

	public static void main( String[] args ) {
		Intersection intersection = new Intersection();
		Thread thread = new Thread(new TrainA( intersection ));
		Thread thread1 = new Thread(new TrainB( intersection ));

		thread.start();
		thread1.start();
	}

	private static class TrainA implements Runnable {
		private Intersection intersection;
		private final Random random = new Random();

		private TrainA( final Intersection intersection ) {this.intersection = intersection;}

		@Override
		public void run() {
			while(true) {
				long sleepingTime = random.nextInt(5);
				try {
					Thread.sleep( sleepingTime );
				} catch ( InterruptedException e ) {
					e.printStackTrace();
				}
				try {
					intersection.takeRoadA();
				} catch ( InterruptedException e ) {
					e.printStackTrace();
				}
			}
		}
	}

	private static class TrainB implements Runnable {
		private Intersection intersection;
		private final Random random = new Random();

		private TrainB( final Intersection intersection ) {this.intersection = intersection;}

		@Override
		public void run() {
			while(true) {
				long sleepingTime = random.nextInt(5);
				try {
					Thread.sleep( sleepingTime );
				} catch ( InterruptedException e ) {
					e.printStackTrace();
				}
				try {
					intersection.takeRoadB();
				} catch ( InterruptedException e ) {
					e.printStackTrace();
				}
			}
		}
	}

	private static class Intersection {
		private Object roadA = new Object();
		private Object roadB = new Object();

		public void takeRoadA() throws InterruptedException {
			synchronized ( roadA ) {
				System.out.println("Road A is locked");
				synchronized ( roadB ) {
					System.out.println("Train is passing through road A");
					Thread.sleep( 1 );
				}
			}
		}

		public void takeRoadB() throws InterruptedException {
			synchronized ( roadB ) {
				System.out.println("Road B is locked");
				synchronized ( roadA ) {
					System.out.println("Train is passing through road B");
					Thread.sleep( 1 );
				}
			}
		}
	}
}
