package project;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.math3.stat.StatUtils;
import org.apache.commons.math3.stat.inference.TestUtils;

import project.dataParser.DataParserManager;
import project.dataParser.I_DBClock;
import project.dataParser.I_DBProcessor;
import project.dataParser.I_DBReader;
import project.dataParser.IndustryPortfolioProcessor;
import project.dataParser.IndustryPortfolioReader;
import project.dataParser.MomentumStrategyProcessor;
import project.dataParser.MonthlyClock;
import project.dataStructure.IndustryPortfolio;
import utils.CollectionUtils;

/**
 * @author Zhenghong Dong
 */
public class MomentumAnalyze {
	/* class stuff */
	private String					_portHisReturnFile	= "C:\\Users\\Zhenghong Dong\\SkyDrive\\dappled's sky\\poly-mfe\\13f\\Portfolio\\project data\\industry value average portfolio monthly return 95_13.txt";
	private String					_outputFile			= "C:\\Users\\Zhenghong Dong\\SkyDrive\\dappled's sky\\poly-mfe\\13f\\Portfolio\\project data\\analysis 95_13.csv";
	private final long				_startDate;

	/* industry portfolio stuff */
	private List<IndustryPortfolio>	_industryPorts;

	public MomentumAnalyze(long startDate) {
		_startDate = startDate;
	}

	public MomentumAnalyze(long startDate, String returnFile) {
		this( startDate );
		_portHisReturnFile = returnFile;
	}

	public void analyze() throws Exception {
		if (_industryPorts != null) {
			System.err.println( "ImportData: raw portfolio return already imported!" );
			return;
		}

		// reader
		final IndustryPortfolioReader reader = new IndustryPortfolioReader( "1", _portHisReturnFile );
		final LinkedList<I_DBReader> readers = new LinkedList<>();
		readers.add( reader );

		// processor
		final IndustryPortfolioProcessor processor = new IndustryPortfolioProcessor( reader.getNames() );
		final MomentumStrategyProcessor processor2 = new MomentumStrategyProcessor( 6, 0, 6, reader.getNames().size(),
				3, 0, 0, 3 );
		final LinkedList<I_DBProcessor> processors = new LinkedList<>();
		processors.add( processor );
		processors.add( processor2 );

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

		_industryPorts = processor.getIndustryPortfolio();
		int size = _industryPorts.get( 0 ).getRet().size();
		for (IndustryPortfolio p : _industryPorts) {
			Double[] ret = p.getRet().values().toArray( new Double[ size ] );
			double[] ret2 = CollectionUtils.doubleFromDouble( ret );
			System.out.printf( "%s : excess return (%.4f); t-stat (%.2f)\n", p.getName(), StatUtils.mean( ret2
					), TestUtils.t( 0.0, ret2 ) );
		}

		double[] best = processor2.getBestPortReturn();
		// double[] middle = processor2.getMiddlePortReturn();
		double[] worst = processor2.getWorstPortReturn();
		// since our portfolio is high - low
		best = substractAll( best, worst );
		System.out.printf( "Long best three short worst three : excess return (%.4f); t-stat (%.2f)\n",
				StatUtils.mean( best ), TestUtils.t( 0.0, best ) );
	}


	public double[] substractAll(double[] o1, double[] o2) {
		double[] res = o1.clone();
		for (int i = 0; i < o2.length; i++) {
			res[ i ] -= o2[ i ];
		}
		return res;
	}

	public void analyze2() throws Exception {
		// reader
		final IndustryPortfolioReader reader = new IndustryPortfolioReader( "1", _portHisReturnFile );
		final LinkedList<I_DBReader> readers = new LinkedList<>();
		readers.add( reader );

		// processors
		final LinkedList<I_DBProcessor> processors = new LinkedList<>();
		for (int lag : Arrays.asList( 1, 6, 12 )) {
			for (int hold : Arrays.asList( 1, 6, 12, 24, 36 )) {
				for (int gap : Arrays.asList( 0, 1 )) {
					processors.add( new MomentumStrategyProcessor( lag, gap, hold, reader.getNames().size(), 3, 14, 16,
							3 ) );
				}
			}
		}

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

		try {
			FileWriter writer = new FileWriter( _outputFile );
			// header
			writer.append( "L," );
			writer.append( "G," );
			writer.append( "H=," );
			writer.append( "Wi," );
			writer.append( "Lo," );
			writer.append( "Wi - Lo," );
			writer.append( ",Buy Wi - Mid," );
			writer.append( ",Sell Mid - Lo,,\n" );

			for (I_DBProcessor p : processors) {

				double[] best = ((MomentumStrategyProcessor) p).getBestPortReturn();
				double[] middle = ((MomentumStrategyProcessor) p).getMiddlePortReturn();
				double[] worst = ((MomentumStrategyProcessor) p).getWorstPortReturn();
				double[] bw = substractAll( best, worst );
				double[] bm = substractAll( best, middle );
				double[] mw = substractAll( middle, worst );
				writer.append( String.format( "%d", ((MomentumStrategyProcessor) p).getLagTime() ) );
				writer.append( ',' );
				writer.append( String.format( "%d", ((MomentumStrategyProcessor) p).getGapTime() ) );
				writer.append( ',' );
				writer.append( String.format( "%d", ((MomentumStrategyProcessor) p).getHoldTime() ) );
				writer.append( ',' );
				writer.append( String.format( "%.4f", StatUtils.mean( best ) ) );
				writer.append( ',' );
				writer.append( String.format( "%.2f", StatUtils.mean( worst ) ) );
				writer.append( ',' );
				writer.append( String.format( "%.4f", StatUtils.mean( bw ) ) );
				writer.append( ',' );
				writer.append( String.format( "%.2f", TestUtils.t( 0.0, bw ) ) );
				writer.append( ',' );
				writer.append( String.format( "%.4f", StatUtils.mean( bm ) ) );
				writer.append( ',' );
				writer.append( String.format( "%.2f", TestUtils.t( 0.0, bm ) ) );
				writer.append( ',' );
				writer.append( String.format( "%.4f", StatUtils.mean( mw ) ) );
				writer.append( ',' );
				writer.append( String.format( "%.2f", TestUtils.t( 0.0, mw ) ) );
				writer.append( '\n' );
			}
			writer.flush();
			writer.close();
		} catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	public static void main(String[] args) throws Exception {
		MomentumAnalyze a = new MomentumAnalyze( 199507 );
		a.analyze();
		a.analyze2();

		System.out.println( "End" );
	}
}
