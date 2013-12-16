package utils;

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
	 * @return the size of sample
	 */
	long sampleSize();
	
	/**
	 * @return key String of this path stats
	 */
	String getKey();

}
