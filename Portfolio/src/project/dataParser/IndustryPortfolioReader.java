package project.dataParser;

import java.io.BufferedReader;
import java.io.EOFException;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class IndustryPortfolioReader implements I_DBReader, Comparable<IndustryPortfolioReader> {
	private final String		_id;
	private long				_sequenceNumber;
	private long				_lastSequeneNumberRead;
	private boolean				_isFinished;

	private ArrayList<Double>	_recordsBuffer;
	private ArrayList<Double>	_fields;
	private List<String>		_names	= new ArrayList<>();

	private BufferedReader		_in		= null;

	/**
	 * Constructor - Opens a csv cds data file and reads entire contents into memory.
	 * 
	 * @param filePathName Name of gzipped TAQ quotes file to read
	 * @throws IOException
	 */
	public IndustryPortfolioReader(final String id, final String filePathName) throws IOException {
		_id = id;
		_isFinished = false;
		_lastSequeneNumberRead = 0;

		_recordsBuffer = new ArrayList<>();

		// Open file
		String str = null;
		String[] line = null;
		try {
			_in = new BufferedReader( new FileReader( filePathName ) );

			str = _in.readLine(); // ignore header
			line = str.split( " +" );
			for (int i = 1; i < line.length; i++) {
				_names.add( line[ i ].trim() );
			}

			if (readOneRecord() == -1) {
				stop();
			}
		} catch (final FileNotFoundException e) {
			throw e;
		} catch (final Exception e) {
			e.printStackTrace();
		}
	}

	/***********************************************************************
	 * {@link I_DBReader} methods
	 ***********************************************************************/
	/**
	 * Parse next record of data into fields. Save sequence number.
	 * @return -1 if failed to read (due to end of file); 0 otherwise.
	 * @throws IOException
	 */
	private int readOneRecord() throws IOException {
		String str = null;
		String[] line = null;
		try {
			str = _in.readLine();
			// EOF
			if (str == null) return -1;
			
			line = str.split( " +" );
			_sequenceNumber = Long.parseLong( line[ 0 ].trim() );

			_fields = new ArrayList<>();
			for (int i = 1; i < line.length; i++) {
				_fields.add( Double.parseDouble( line[ i ].trim() ) );
			}
		} catch (final Exception e) {
			e.printStackTrace();
		}
		return 0;
	}

	@Override
	public int readChunk(final long targetSequenceNum) {

		// Check for finished in a previous read

		if (isFinished()) return 0;

		// Clear list of records to prepare for new records
		/**
		 * Note that I move this line up because we want to clear the buffer even later we find out we have nothing to
		 * read (targetSequenceNum < _sequenceNumber)
		 */
		_recordsBuffer.clear();

		// Check for sequence number that is ahead of
		// specified sequence number - Nothing to read

		if (targetSequenceNum < _sequenceNumber) return 0;

		// Record reading loop - Exit when we encounter a sequence number
		// that is above the one we want

		do {
			_recordsBuffer = _fields;
			_lastSequeneNumberRead = _sequenceNumber;
			try {
				if (readOneRecord() == -1) {
					stop();
					return 1;
				}
			} catch (final IOException e) {
				stop();
				return _recordsBuffer.size();
			}
		} while (_sequenceNumber <= targetSequenceNum);

		// Return number of records in our records buffer

		return _recordsBuffer.size();
	}

	@Override
	public long getSequenceNumber() {
		return _lastSequeneNumberRead;
	}

	@Override
	public void stop() {
		_isFinished = true;
		if (_in != null) {
			try {
				_in.close();
			} catch (IOException e) {
				System.err.println( "Fail to close " + _in );
				e.printStackTrace();
			}
		}
	}

	@Override
	public boolean isFinished() {
		return _isFinished;
	}

	@Override
	public long getLastSequenceNumberRead() {
		return _lastSequeneNumberRead - 1;
	}

	@Override
	public int compareTo(final IndustryPortfolioReader o) {
		return Long.compare( getSequenceNumber(), o.getSequenceNumber() );
	}

	/***********************************************************************
	 * Getter and Setter
	 ***********************************************************************/
	public ArrayList<Double> getRecordsBuffer() {
		return _recordsBuffer;
	}

	public String getId() {
		return _id;
	}

	public List<String> getNames() {
		return _names;
	}
}
