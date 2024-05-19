package thread.creation.example.atomic;

public class MinMaxMetrics {

	private volatile long min = Long.MAX_VALUE;
	private volatile long max = 0;

	public MinMaxMetrics() {
	}

	public void addSample(long newSample) {
		synchronized ( this ) {
			if ( newSample < min ) {
				min = newSample;
			}
			if ( newSample > max ) {
				max = newSample;
			}
		}
	}

	public long getMin() {
		return min;
	}

	public long getMax() {
		return max;
	}
}
