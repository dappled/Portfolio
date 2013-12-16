package ps2;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.math3.stat.ranking.NaturalRanking;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import ps2.portfolio.Portfolio;
import ps2.portfolio.Stock;

/**
 * @author Zhenghong Dong
 */
public class Problem1 {
	private String							_fileName;
	private final List<Stock>				_stocks;
	private Portfolio[]						_portfolios;
	private final HashMap<String, Integer>	_location;

	/***********************************************************************
	 * Constructor
	 ***********************************************************************/
	public Problem1(final String fileName) {
		_fileName = fileName;
		_stocks = new ArrayList<>();
		_portfolios = new Portfolio[ 3 ];
		for (int i = 0; i < _portfolios.length; i++) {
			_portfolios[ i ] = new Portfolio();
		}
		_location = new HashMap<>();
	}

	/***********************************************************************
	 * Utilities
	 ***********************************************************************/
	/**
	 * Read the file and calculate alpha
	 * @throws IOException
	 * @throws FileNotFoundException
	 * @throws InvalidFormatException
	 * @throws Exception
	 */
	public void read() throws Exception {
		final Workbook wb = WorkbookFactory.create( new FileInputStream( _fileName ) );
		final Sheet sheet1 = wb.getSheet( "ARDATA" );
		int i = 1; // ignore header
		Row row = sheet1.getRow( i++ );
		// read funds name
		for (int j = 2; j < row.getLastCellNum(); j++) {
			final Stock stock = new Stock( row.getCell( j ).getStringCellValue() );
			stock.setMovingAverage( 24 );
			_stocks.add( stock );
			_location.put( stock.getTicker(), j );
		}

		// read monthly data until 2002
		for (row = sheet1.getRow( i ); row.getCell( 1 ).getNumericCellValue() < 2002; row = sheet1.getRow( ++i )) {
			for (int j = 2; j < row.getLastCellNum(); j++) {
				_stocks.get( j - 2 ).addRecord( row.getCell( j ).getNumericCellValue() );
			}
		}

		// rank stocks
		for (row = sheet1.getRow( i ); row.getCell( 1 ).getNumericCellValue() < 2003; row = sheet1.getRow( ++i )) {
			double[] ma = new double[ 9 ];
			for (int j = 2; j < row.getLastCellNum(); j++) {
				// add this month's data
				_stocks.get( j - 2 ).addRecord( row.getCell( j ).getNumericCellValue() );
				// calculate MA
				if (j > 2) {
					ma[ j - 3 ] = _stocks.get( j - 2 ).getMovingAverage();
				}
			}
			// rank MA
			ma = new NaturalRanking().rank( ma );

			// store rank
			for (int j = 0; j < ma.length; j++) {
				_stocks.get( j + 1 ).addRank( ma[ j ] );
			}
		}

		// get sp500 and rfr
		final double[] _sp500 = _stocks.remove( 0 ).getReturn(); // sp 500
		final double[] _rfr = _stocks.get( 6 ).getReturn(); // risk free rate

		// get overall rank in 2002
		Collections.sort( _stocks );

		/** construct portfolios and measure performance */
		System.out
		.println( "This part is assuming we combine best three performance stocks to a portfolio, middle three"
				+ " to another portfolio, and worst three performance stocks to a portfolio, then do the measurement"
				+ " test" );
		System.out.println( "For year before 2003: (this is in reverse order in terms of return performance)" );
		// generate portfolio according to overall rank
		for (int j = 0; j < _portfolios.length; j++) {
			_portfolios[ j ].addStock( _stocks.get( j * 3 ) );
			_portfolios[ j ].addStock( _stocks.get( j * 3 + 1 ) );
			_portfolios[ j ].addStock( _stocks.get( j * 3 + 2 ) );
		}

		// get portfolio historical beta
		for (final Portfolio _portfolio : _portfolios) {
			_portfolio.calculateBeta( _sp500, _rfr );
		}

		// read first 3 months of 2003
		int rev  = i;
		for (row = sheet1.getRow( i ); row.getCell( 0 ).getNumericCellValue() < 4; row = sheet1.getRow( ++i )) {
			final double sp500 = row.getCell( 2 ).getNumericCellValue();
			final double rfr = row.getCell( 9 ).getNumericCellValue();
			for (final Portfolio port : _portfolios) {
				double ret = 0;
				for (final String ticker : port.getTicker()) {
					ret += row.getCell( _location.get( ticker ) ).getNumericCellValue();
				}
				ret /= port.getSize();
				port.addAlpha( ret, sp500, rfr );
			}
		}
		System.out.println( "First three months of 2003:" );
		// output result
		for (int j = 0; j < _portfolios.length; j++) {
			System.out.printf( "Porfolio at rank %d [%s]: alpha is %.4f\n", _portfolios.length - j,
					_portfolios[ j ].toString(), _portfolios[ j ].getAlpha() );
		}

		/** use only stocks to measure performance */
		System.out.println();
		System.out
		.println( "This part is assuming we use the 9 indexes themselves only, then do the measurement job" );
		System.out.println( "For year before 2003: (this is in reverse order in terms of return performance)" );
		// generate portfolio according to overall rank
		_portfolios = new Portfolio[ 9 ];
		for (int j = 0; j < _portfolios.length; j++) {
			_portfolios[ j ] = new Portfolio();
		}
		for (int j = 0; j < _stocks.size(); j++) {
			_portfolios[ j ].addStock( _stocks.get( j ) );
		}
		

		// get portfolio historical beta
		for (final Portfolio _portfolio : _portfolios) {
			_portfolio.calculateBeta( _sp500, _rfr );
		}

		// read first 3 months of 2003
		i = rev;
		for (row = sheet1.getRow( i ); row.getCell( 0 ).getNumericCellValue() < 4; row = sheet1.getRow( ++i )) {
			final double sp500 = row.getCell( 2 ).getNumericCellValue();
			final double rfr = row.getCell( 9 ).getNumericCellValue();
			for (final Portfolio port : _portfolios) {
				double ret = 0;
				for (final String ticker : port.getTicker()) {
					ret += row.getCell( _location.get( ticker ) ).getNumericCellValue();
				}
				ret /= port.getSize();
				port.addAlpha( ret, sp500, rfr );
			}
		}
		System.out.println( "First three months of 2003:" );
		// output result
		for (int j = 0; j < _portfolios.length; j++) {
			System.out.printf( "Portfolio at rank %d [%s]: alpha is %.4f\n", _portfolios.length - j,
					_portfolios[ j ].toString(), _portfolios[ j ].getAlpha() );
		}

	}

	/***********************************************************************
	 * Getter and Setter
	 ***********************************************************************/
	public void setFileName(final String fileName) {
		_fileName = fileName;
	}

	public static void main(final String[] args) throws Exception {
		// You should change the fileName ...
		final String fileName = "C:\\Users\\Zhenghong Dong\\SkyDrive\\dappled's sky\\poly-mfe\\13f\\Portfolio\\PS2 Momentum Data.xls";
		final Problem1 p1 = new Problem1( fileName );

		p1.read();
	}
}
