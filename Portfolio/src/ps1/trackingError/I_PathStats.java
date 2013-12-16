package ps1.trackingError;

/**
 * The interface to track given data's statistics, currently only calculate sample mean and variance.
 * 
 * @author Zhenghong Dong
 * 
 */
public interface I_PathStats<E extends Number> {
	/**
	 * add the value of a new path, update data's statistics
	 * 
	 * @param t: the value of a new path
	 */
	void addPathStat(E e);

	/**
	 * @return current sample mean
	 */
	double sampleMean();

	/**
	 * @return sample variance
	 */
	double sampleVariance();

	/**
	 * @return sample tracking error
	 */
	double sampleTrackingError();

	/** 
	 * @return the size of sample
	 */
	long sampleSize();

}
