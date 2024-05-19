package thread.creation.example.object_methods;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Scanner;
import java.util.StringJoiner;

/**
 * Implemented a Thread safe queue between consumer and producer threads
 * Implemented back pressure to guard against OutOfMemoryException
 * Whenever using a queue to decouple multithreaded components,
 * apply back-pressure and limit the size of the queue!
 */
public class Main {

	private static final String INPUT_FILE = "./out/matrices.txt";
	private static final String OUTPUT_FILE = "./out/matrices_result.txt";
	private static final int N = 10;
	private static final int QUEUE_CAPACITY = 5; // for back pressure

	public static void main( String[] args ) throws IOException {
		ThreadSafeQueue threadSafeQueue = new ThreadSafeQueue();
		File inputFile = new File( INPUT_FILE );
		File outputFile = new File(OUTPUT_FILE);

		MatricesReaderProducer matricesReaderProducer = new MatricesReaderProducer( new FileReader( inputFile ), threadSafeQueue );
		MatricesMultiplierConsumer matricesMultiplierConsumer = new MatricesMultiplierConsumer( threadSafeQueue, new FileWriter( outputFile ) );

		matricesReaderProducer.start();
		matricesMultiplierConsumer.start();
	}

	private static class MatricesMultiplierConsumer extends Thread {
		private ThreadSafeQueue threadSafeQueue;
		private FileWriter fileWriter;

		public MatricesMultiplierConsumer( final ThreadSafeQueue threadSafeQueue,
				final FileWriter fileWriter ) {
			this.threadSafeQueue = threadSafeQueue;
			this.fileWriter = fileWriter;
		}

		private void saveMatrixToFile(float[][] matrix) throws IOException {
			for(int r = 0; r < N; ++r) {
				StringJoiner stringJoiner = new StringJoiner( ", " );
				for(int c = 0; c < N; ++c) {
					stringJoiner.add( String.format("%.2f", matrix[r][c]) );
				}
				fileWriter.write( stringJoiner.toString() );
				fileWriter.write( '\n' );
			}
			fileWriter.write( '\n' );
		}

		@Override
		public void run() {
			while(true) {
				MatricesPair matricesPair = threadSafeQueue.remove();
				if (matricesPair == null) {
					System.out.println("No more matrices to read from queue, consumer is terminating");
					break;
				}
				float[][] result = multiplyMatrices( matricesPair.matrix1, matricesPair.matrix2 );
				try {
					saveMatrixToFile(result);
				} catch ( IOException e ) {
					e.printStackTrace();
				}
			}
		}

		private float[][] multiplyMatrices(float[][] matrix1, float[][] matrix2) {
			float[][] result = new float[N][N];
			for(int r = 0; r < N; ++r) {
				for(int c = 0; c < N; ++c) {
					for(int k = 0; k < N; ++k) {
						result[r][c] += matrix1[r][k] + matrix2[k][c];
					}
				}
			}
			return result;
		}
	}

	private static class MatricesReaderProducer extends Thread {
		private Scanner scanner;
		private ThreadSafeQueue threadSafeQueue;

		public MatricesReaderProducer( final FileReader reader,
				final ThreadSafeQueue threadSafeQueue ) {
			this.scanner = new Scanner( reader );
			this.threadSafeQueue = threadSafeQueue;
		}

		@Override
		public void run() {
			while(true) {
				float[][] matrix1 = readMatrix();
				float[][] matrix2 = readMatrix();

				if (matrix1 == null || matrix2 == null) {
					threadSafeQueue.terminate();
					System.out.println("No more matrices to read");
					return;
				}

				MatricesPair matricesPair = new MatricesPair();
				matricesPair.matrix1 = matrix1;
				matricesPair.matrix2 = matrix2;

				threadSafeQueue.add(matricesPair);
			}
		}

		private float[][] readMatrix() {
			float[][] matrix = new float[N][N];
			for(int r = 0; r < N; ++r) {
				if(!scanner.hasNext()) {
					return null;
				}
				String[] line = scanner.nextLine().split( "," );
				for(int c = 0; c < N; ++c) {
					matrix[r][c] = Float.valueOf( line[c] );
				}
			}
			scanner.nextLine();
			return matrix;
		}
	}

	private static class ThreadSafeQueue {
		private Queue<MatricesPair> queue = new LinkedList<>();
		private boolean isEmpty = true;
		private boolean isTerminated = false;

		public synchronized void add(MatricesPair matricesPair) {
			while (queue.size() == QUEUE_CAPACITY) {
				try {
					wait();
				} catch ( InterruptedException e ) {
					e.printStackTrace();
				}
			}
			queue.add( matricesPair );
			isEmpty = false;
			notify();
		}

		public synchronized MatricesPair remove() {
			while (isEmpty && !isTerminated) {
				try {
					wait();
				} catch ( InterruptedException e ) {
					e.printStackTrace();
				}
			}
			if (queue.size() == 1) {
				isEmpty = true;
			}
			if (queue.size() == 0 && isTerminated) {
				return null;
			}

			System.out.println("Queue size " + queue.size());
			MatricesPair matricesPair = queue.remove();
			if (queue.size() == QUEUE_CAPACITY - 1) {
				notifyAll();
			}
			return matricesPair;
		}

		public synchronized void terminate() {
			isTerminated = true;
			notifyAll();
		}
	}

	private static class MatricesPair {
		public float[][] matrix1;
		public float[][] matrix2;
	}
}
