package utils;

/**
 * @author Zhenghong Dong
 */
public class CollectionUtils {

	public static double[] doubleFromDouble(Double[] inp) {
		double[] ret = new double[inp.length];
		for (int i = 0; i < ret.length; i++) {
			ret[i] = inp[i];
		}
		return ret;
	}

}
