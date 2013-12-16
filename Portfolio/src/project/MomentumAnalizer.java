package project;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;

import project.dataParser.DataParserManager;
import project.dataParser.I_DBClock;
import project.dataParser.I_DBProcessor;
import project.dataParser.I_DBReader;
import project.dataParser.IndustryPortfolioProcessor;
import project.dataParser.IndustryPortfolioReader;
import project.dataParser.MonthlyClock;
import project.dataStructure.IndustryPortfolio;
import project.dataStructure.MomentumPortfolio;

/**
 * @author Zhenghong Dong
 */
public class MomentumAnalizer {
	/* class stuff */
	private String									_portHisReturnFile	= "C:\\Users\\Zhenghong Dong\\SkyDrive\\dappled's sky\\poly-mfe\\13f\\Portfolio\\project data\\industry value average portfolio monthly return 96_13.txt";
	private final long								_startDate;

	/* industry portfolio stuff */
	private List<IndustryPortfolio>					_industryPort;

	/* momentum strategy stuff */
	private LinkedHashMap<Long, MomentumPortfolio>	_momentumPort;

	public MomentumAnalizer(long startDate) {
		_startDate = startDate;
	}

	public MomentumAnalizer(long startDate, String returnFile) {
		this( startDate );
		_portHisReturnFile = returnFile;
	}

	public void analyze() throws IOException {
		if (_industryPort != null) {
			System.err.println( "ImportData: raw portfolio return already imported!" );
			return;
		}

		// reader
		final IndustryPortfolioReader reader = new IndustryPortfolioReader( "1", _portHisReturnFile );
		final LinkedList<I_DBReader> readers = new LinkedList<>();
		readers.add( reader );

		// processor
		final IndustryPortfolioProcessor processor = new IndustryPortfolioProcessor( reader.getNames() );
		final LinkedList<I_DBProcessor> processors = new LinkedList<>();
		processors.add( processor );

		// Make a db clock

		final I_DBClock clock = new MonthlyClock( _startDate, readers );

		// Make a new db manager and hand off db readers, db processors, and clock
		final DataParserManager dbManager = new DataParserManager(
				readers, // List of readers
				processors, // List of processors
				clock // Clock
		);

		// Launch db manager to
		dbManager.launch();

		_industryPort = processor.getIndustryPortfolio();
	}

	public void industryMomentumAnalize(int l, int h) {

	}

	public static void main(String[] args) throws IOException {
		MomentumAnalizer a = new MomentumAnalizer( 199608 );
		a.analyze();

		System.out.println( "End" );
	}
}
