package project.dataParser;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;

import project.dataStructure.Column;
import project.dataStructure.PeriodReturn;
import utils.CollectionUtils;
import utils.ParseDateLong;

/**
 * This is an implementation of {@link I_DBProcessor}, it merges several quote files to a new GZfile
 * @author Zhenghong Dong
 */
public class MomentumStrategyProcessor implements I_DBProcessor {
	private boolean						_isFinished;

	private int							_best, _middleStart, _middleEnd, _worst;
	private PeriodReturn				_lag, _gap, _hold;
	private int							_l, _g, _h;

	private LinkedHashMap<Long, Double>	_bestPortReturn;
	private LinkedHashMap<Long, Double>	_middlePortReturn;
	private LinkedHashMap<Long, Double>	_worstPortReturn;

	/***********************************************************************
	 * Constructor
	 * @throws Exception
	 ***********************************************************************/
	public MomentumStrategyProcessor(int lag, int gap, int hold, int numPortfolios, int best, int middleStart,
			int middleEnd, int worst)
			throws Exception {
		_isFinished = false;

		_l = lag;
		_h = hold;
		_g = gap;
		_lag = new PeriodReturn( _l, numPortfolios );
		_hold = new PeriodReturn( _h, numPortfolios );
		if (_g != 0) {
			_gap = new PeriodReturn( _g, numPortfolios );
		}

		_best = best;
		_middleStart = middleStart;
		_middleEnd = middleEnd + 1;
		_worst = worst;

		_bestPortReturn = new LinkedHashMap<>();
		_middlePortReturn = new LinkedHashMap<>();
		_worstPortReturn = new LinkedHashMap<>();
	}

	/***********************************************************************
	 * {@link I_DBProcessor} methods
	 * @throws Exception
	 ***********************************************************************/
	@Override
	public boolean processReaders(final long sequenceNumber, final int numReadersWithNewData,
			final LinkedList<I_DBReader> readers) throws Exception {
		if (_isFinished) return false;

		// add newest data to hold
		Column removed = _hold.add( sequenceNumber, ((IndustryPortfolioReader) readers.getFirst())
				.getRecordsBuffer() );
		// add older data to gap
		if (_gap != null && removed != null) {
			removed = _gap.add( removed.date, removed.ret );
		}
		// add oldest data to lag
		if (removed != null) {
			_lag.add( removed.date, removed.ret );
		}
		// if buffer still not enough data
		if (!_lag.isReady()) { return true; }

		// consturct portfolio given holding period return
		List<Integer[]> ranks = _lag.partition( _best, _middleStart, _middleEnd, _worst );
		long startHoldingDate = ParseDateLong.addMonth( sequenceNumber, -(_h - 1) );
		addReturn( _bestPortReturn, startHoldingDate, _h, _hold, ranks.get( 0 ) );
		addReturn( _middlePortReturn, startHoldingDate, _h, _hold, ranks.get( 1 ) );
		addReturn( _worstPortReturn, startHoldingDate, _h, _hold, ranks.get( 2 ) );

		return true; // Not finished
	}

	@Override
	public void stop() {
		_isFinished = true;
	}

	public double[] getBestPortReturn() {
		return summarizeReturn( _bestPortReturn );
	}

	public double[] getMiddlePortReturn() {
		return summarizeReturn( _middlePortReturn );
	}

	public double[] getWorstPortReturn() {
		return summarizeReturn( _worstPortReturn );
	}

	private void addReturn(LinkedHashMap<Long, Double> portReturn, long startPeriod, int numPeriods, PeriodReturn hold,
			Integer[] idx) {
		for (int i = 0; i < numPeriods; i++) {
			long date = startPeriod + i;
			double ret = hold.getEquallyWeightedReturn( idx, i );
			if (!portReturn.containsKey( date )) {
				portReturn.put( date, ret );
			} else {
				portReturn.put( date, (Double) portReturn.get( date ) + ret );
			}
		}
	}

	private double[] summarizeReturn(LinkedHashMap<Long, Double> portReturn) {
		Double[] ret = portReturn.values().toArray( new Double[ portReturn.size() ] );
		double[] ret2 = CollectionUtils.doubleFromDouble( ret );
		divideAll( ret2, _h );

		return Arrays.copyOfRange( ret2, _h - 1, ret2.length );
	}

	/* private void addAll(double[] ret, Double double1, int start, int limit) {
	 * for (int i = start; i < start + limit; i++) {
	 * try {
	 * ret[ i ] += double1;
	 * } catch (IndexOutOfBoundsException e) {
	 * return; // end of array
	 * }
	 * }
	 * } */

	private void divideAll(double[] ret, int num) {
		for (int i = 0; i < ret.length; i++) {
			ret[ i ] /= num;
		}
	}

	public int getLagTime() {
		return _l;
	}

	public int getHoldTime() {
		return _h;
	}

	public int getGapTime() {
		return _g;
	}
}
