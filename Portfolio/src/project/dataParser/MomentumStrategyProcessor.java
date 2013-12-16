package project.dataParser;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;

import utils.ParseDateLong;

/**
 * This is an implementation of {@link I_DBProcessor}, it merges several quote files to a new GZfile
 * @author Zhenghong Dong
 */
public class MomentumStrategyProcessor implements I_DBProcessor {
	private boolean									_isFinished;

	private final int								_lag, _hold;
	private LinkedHashMap<Long, ArrayList<Double>>	_buffer;

	private LinkedHashMap<Long, Double>				_portReturn;

	/***********************************************************************
	 * Constructor
	 ***********************************************************************/
	public MomentumStrategyProcessor(int lag, int hold) throws IOException {
		_isFinished = false;

		_lag = lag;
		_hold = hold;

		_buffer = new LinkedHashMap<>();
	}

	/***********************************************************************
	 * {@link I_DBProcessor} methods
	 ***********************************************************************/
	@Override
	public boolean processReaders(final long sequenceNumber, final int numReadersWithNewData,
			final LinkedList<I_DBReader> readers) {
		if (_isFinished) return false;

		// add newest data to _buffer
		_buffer.put( sequenceNumber, ((IndustryPortfolioReader) readers.getFirst()).getRecordsBuffer() );
		// if buffer still not enough data
		if (_buffer.size() != _lag + _hold) { return true; }
		// remove outdated data from buffer
		_buffer.remove( ParseDateLong.addMonth( sequenceNumber, -(_lag + _hold + 1) ) );

		ArrayList<Double> todayData = _buffer.get( ParseDateLong.addMonth( sequenceNumber, -_hold ) );
		//get holding period return;
		
		return true; // Not finished
	}

	@Override
	public void stop() {
		_isFinished = true;
	}

	public LinkedHashMap<Long, Double> getReturn() {
		return _portReturn;
	}
}