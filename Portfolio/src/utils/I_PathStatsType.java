package utils;

/**
 * @author Zhenghong Dong
 */
public class I_PathStatsType {
	public static final I_PathStatsType	MovingAverage	= new I_PathStatsType( "MovingAverage" );
	public static final I_PathStatsType	MeanVariance	= new I_PathStatsType( "MeanVariance" );

	private final String				_type;

	private I_PathStatsType(final String type) {
		_type = type;
	}

	@Override
	public String toString() {
		return String.format( "path stats type: %s", _type );
	}
}
