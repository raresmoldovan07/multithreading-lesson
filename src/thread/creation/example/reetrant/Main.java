package thread.creation.example.reetrant;

import java.util.Random;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * ReentrantLock work like synchronized applied to an object
 * Requires explicit lock and unlock
 */
public class Main {

	private static class PricesContainer {
		private Lock lockObject  = new ReentrantLock();
		private double bitcoinPrice;
		private double etherPrice;

		public Lock getLockObject() {
			return lockObject;
		}

		public void setLockObject( final Lock lockObject ) {
			this.lockObject = lockObject;
		}

		public double getBitcoinPrice() {
			return bitcoinPrice;
		}

		public void setBitcoinPrice( final double bitcoinPrice ) {
			this.bitcoinPrice = bitcoinPrice;
		}

		public double getEtherPrice() {
			return etherPrice;
		}

		public void setEtherPrice( final double etherPrice ) {
			this.etherPrice = etherPrice;
		}
	}

	private static class PriceUpdater extends Thread {
		private PricesContainer pricesContainer;
		private Random random = new Random();

		public PriceUpdater( final PricesContainer pricesContainer ) {
			this.pricesContainer = pricesContainer;
		}

		@Override
		public void run() {
			while(true) {
				pricesContainer.getLockObject().lock();
				try{
					try{
						Thread.sleep( 1000 );
					} catch (InterruptedException e) {

					}
					pricesContainer.setBitcoinPrice( random.nextInt(20000) );
					pricesContainer.setEtherPrice( random.nextInt(20000) );
				} finally {
					pricesContainer.getLockObject().unlock();
				}
				try {
					Thread.sleep( 1000 );
				} catch ( InterruptedException e ) {
					e.printStackTrace();
				}
			}
		}
	}
}
