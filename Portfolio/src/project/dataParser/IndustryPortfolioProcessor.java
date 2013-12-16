package project.dataParser;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import project.dataStructure.IndustryPortfolio;

/**
 * This is an implementation of {@link I_DBProcessor}, it merges several quote files to a new GZfile
 * @author Zhenghong Dong
 */
public class IndustryPortfolioProcessor implements I_DBProcessor {
	private boolean								_isFinished;
	private final ArrayList<IndustryPortfolio>	_ports	= new ArrayList<>();

	/***********************************************************************
	 * Constructor
	 ***********************************************************************/
	public IndustryPortfolioProcessor(final List<String> industryNames) {
		_isFinished = false;
		for (String name : industryNames) {
			_ports.add( new IndustryPortfolio( name ) );
		}
	}

	/***********************************************************************
	 * {@link I_DBProcessor} methods
	 ***********************************************************************/
	/**
	 * This method gets called by db manager when a chunk of records has
	 * been read by all of the db readers. This is where this db processor
	 * writes all records to one merged output file.
	 */
	@Override
	public boolean processReaders(final long sequenceNumber, final int numReadersWithNewData,
			final LinkedList<I_DBReader> readers) {
		if (_isFinished) return false;

		final Iterator<I_DBReader> readerIterator = readers.iterator();
		for (int i = 0; i < numReadersWithNewData; i++) {
			final I_DBReader reader = readerIterator.next();
			ArrayList<Double> ret = ((IndustryPortfolioReader) reader).getRecordsBuffer();
			for (int j = 0; j < ret.size(); j++) {
				_ports.get( j ).addMonthlyReturn( sequenceNumber, ret.get( j ) );
			}
		}

		return true; // Not finished
	}

	/** Called by db manager to give this processor a chance to tie up loose ends */
	@Override
	public void stop() {
		_isFinished = true;
	}

	public ArrayList<IndustryPortfolio> getIndustryPortfolio() {
		return _ports;
	}
}
