package ps2.portfolio;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import utils.I_PathStats;
import utils.I_PathStatsType;
import utils.MovingAverage;
import utils.PathStat;

/**
 * @author Zhenghong Dong
 */
public class Stock implements Comparable<Stock> {
	private final String								_tickers;
	private final List<Double>							_returns;
	private Map<I_PathStatsType, I_PathStats<Double>>	_stats;
	/***********************************************************************
	 * Constructor
	 ***********************************************************************/
	public Stock(final String ticker) {
		_tickers = ticker;
		_returns = new ArrayList<>();
		_stats = new HashMap<>();
	}

	/***********************************************************************
	 * Utilities
	 ***********************************************************************/
	public void addRecord(final double returns) throws Exception {
		_returns.add( returns );
		if (_stats.containsKey( I_PathStatsType.MovingAverage )) {
			_stats.get( I_PathStatsType.MovingAverage ).addPathStat( returns );
		}
	}

	public double getMovingAverage() throws Exception {
		if (!_stats.containsKey( I_PathStatsType.MovingAverage )) throw new Exception(
				"Moving Average not set for this stock" );
		return _stats.get( I_PathStatsType.MovingAverage ).sampleMean();
	}

	public void addRank(final double i) {
		if (!_stats.containsKey( I_PathStatsType.MeanVariance )) {
			initialRank();
		}
		_stats.get( I_PathStatsType.MeanVariance ).addPathStat( i );
	}

	private void initialRank() {
		_stats.put( I_PathStatsType.MeanVariance, PathStat.<Double> newPathStat() );
	}

	/***********************************************************************
	 * Comparable methods
	 ***********************************************************************/
	@Override
	public int compareTo(Stock o) {
		return Double.compare( getRank(), o.getRank() );
	}

	/***********************************************************************
	 * Getter and Setter
	 ***********************************************************************/
	public String getTicker() {
		return _tickers;
	}

	public double[] getReturn() {
		double[] ret = new double[ _returns.size() ];
		for (int i = 0; i < ret.length; i++) {
			ret[ i ] = _returns.get( i );
		}
		return ret;
	}

	public void setMovingAverage(final int size) {
		_stats.put( I_PathStatsType.MovingAverage, MovingAverage.newMovingAveragePathStats( _tickers, size ) );
	}

	public double getRank() {
		return _stats.get( I_PathStatsType.MeanVariance ).sampleMean();
	}

}
