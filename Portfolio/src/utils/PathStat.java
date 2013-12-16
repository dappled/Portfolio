package utils;



/**
 * This is a concrete implementation of {@link I_PathStats}, use an arraylist to save stats
 * 
 * @author Zhenghong Dong
 * 
 */
public class PathStat<E extends Number> implements I_PathStats<E> {
	private double			_mean;
	private double			_xSquare;
	private double			_var;
	private long			_len;
	private String 			_key;

	private PathStat() {
		_mean = 0;
		_xSquare = 0;
		_var = 0;
		_len = 0;
	}
	
	private PathStat(String key) {
		this();
		_key = key;
	}

	// StaticFactory
	public static <E extends Number> I_PathStats<E> newPathStat() {
		return new PathStat<E>();
	}
	
	// StaticFactory 2 
	public static <E extends Number> I_PathStats<E> newPathStat(String name) {
			return new PathStat<E>(name);
	}	

	@Override
	public double sampleMean() {
		return _mean;
	}

	@Override
	public double sampleVariance() {
		return _var;
	}

	@Override
	public void addPathStat(final E e) {
		_mean = (_mean * _len + (e.doubleValue())) / (_len + 1);
		_xSquare = (_xSquare * _len + Math.pow( e.doubleValue(), 2 )) / (++_len);
		_var = _xSquare - Math.pow( _mean, 2 );
	}

	@Override
	public long sampleSize() {
		return _len;
	}
	
	@Override
	public String getKey() {
		return _key;
	}
}
