package utils;

import java.util.LinkedList;

/**
 * In a moving average, a changing list of values is used to compute an average.
 * When the queue of values is full, adding an additional value causes a value
 * to be dropped from the back of the queue. An object of this class will report
 * that it is ready only when the queue is full.
 * 
 * @author minddrill
 * 
 */
public class MovingAverage extends LinkedList<Double> implements I_PathStats<Double>, I_MovingAverage {

	private static final long	serialVersionUID	= 1L;	// Ignore - we never plan to serialize this

	protected int				_size;
	protected double			_sum;
	private final String		_key;

	/**
	 * Static factory method for moving average object.
	 * @param key the key that identifies this moving average list
	 * @param size Size of queue at which moving average is considered ready.
	 * @return An empty moving average object ready to accept additions of values.
	 *         Returning the interface eliminates visibility of LinkedList methods.
	 */
	public static I_MovingAverage newMovingAverage(final String key, final int size) {
		return new MovingAverage( key, size );
	}
	
	public static I_PathStats<Double> newMovingAveragePathStats(final String key, final int size) {
		return new MovingAverage( key, size );
	}

	/**
	 * Private constructor of moving average. Saves size and instantiates internal data store for future additions.
	 * @param size Size of queue at which moving average is considered "ready"
	 */
	private MovingAverage(final String key, final int size) {
		super();
		_key = key;
		_size = size;
		_sum = 0;
	}

	/***********************************************************************
	 * {@link I_MovingAverage} methods
	 ***********************************************************************/
	/**
	 * This is where values are added to the analysis.
	 * Note that if the queue is full, adding a value here
	 * will cause the oldest value to be dropped from the
	 * queue
	 */
	@Override
	public boolean add(final double value) {
		addToAnalysis( value );
		removeFromAnalysis();
		return isReady();
	}

	/**
	 * Add the effect of this value to the calculation
	 * @param value
	 */
	protected void addToAnalysis(final double value) {
		_sum += value;
		addLast( value );
	}

	/**
	 * If the queue has excess elements, remove thei
	 * effects from this calculation
	 */
	protected void removeFromAnalysis() {
		while (hasElementsToRemove()) {
			_sum -= removeFirst();
		}
	}

	/**
	 * @return Moving average of prices in queue
	 * @throws Exception Thrown if the number of elements in the queue is zero
	 */
	@Override
	public double getAverage() throws Exception {
		if (size() < 1) throw new Exception( "Can't take moving average of empty data set" );
		return _sum / size();
	}

	/**
	 * @return True if the number of elements in the queue is number desired
	 */
	@Override
	public boolean isReady() {
		return size() == _size;
	}

	/**
	 * @return True if que is greater than desired size
	 */
	protected boolean hasElementsToRemove() {
		return size() > _size;
	}

	/***********************************************************************
	 * {@link I_PathStats} methods
	 ***********************************************************************/
	@Override
	public String getKey() {
		return _key;
	}

	@Override
	public void addPathStat(Double value) {
		addToAnalysis( value );
		removeFromAnalysis();
	}

	@Override
	public double sampleMean() {
		try {
			return getAverage();
		} catch (Exception e) { // empty list
			return 0;
		}
	}

	@Override
	public double sampleVariance() { return -1; } // we don't need this

	@Override
	public long sampleSize() {
		return size();
	}
}
