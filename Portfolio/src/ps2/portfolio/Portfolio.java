package ps2.portfolio;

import java.util.ArrayList;
import java.util.List;

import ps1.trackingError.I_PathStats;
import ps1.trackingError.PathStat;
import utils.LinearRegression;

/**
 * Simple class implementation of portfolio
 * @author Zhenghong Dong
 */
public class Portfolio {
	private final List<String>			_tickers;
	private double[]					_returns;
	private double						_beta;
	private final I_PathStats<Double>	_alpha;

	/***********************************************************************
	 * Constructor
	 ***********************************************************************/
	public Portfolio() {
		_tickers = new ArrayList<>();
		_returns = null;
		_alpha = PathStat.newPathStat();
	}

	/***********************************************************************
	 * Utilities
	 ***********************************************************************/
	/**
	 * Add a stock to current portfolio, portfolio return should be equally weighted.
	 * @param stock
	 * @throws Exception
	 */
	public void addStock(final Stock stock) throws Exception {
		if (_returns == null) {
			_returns = stock.getReturn();
		} else {
			combineReturnEquallyWeighted( stock.getReturn() );
		}
		_tickers.add( stock.getTicker() );
	}

	/** combining stock's return to current portfolio */
	private void combineReturnEquallyWeighted(final double[] returns) throws Exception {
		if (_returns.length != returns.length) throw new Exception( "Portfolio: addStock: returns has difference size" );
		for (int i = 0; i < returns.length; i++) {
			_returns[ i ] = (_returns[ i ] * _tickers.size() + returns[ i ]) / (_tickers.size() + 1);
		}
	}

	/**
	 * Calculate beta of current portfolio given market portfolio and risk free rate
	 * @param market
	 * @param rfr
	 * @throws Exception
	 */
	public void calculateBeta(final double[] market, final double[] rfr) throws Exception {
		final double[] y = minus( _returns, rfr );
		final double[] x = minus( market, rfr );
		final LinearRegression regression = new LinearRegression( x, y );
		_beta = regression.slope();
		System.out.printf("Historical beta of [%s] is %.4f, alpha is %.4f\n", toString(), _beta, regression.intercept());
	}

	/* return a-b */
	private double[] minus(final double[] a, final double[] b) throws Exception {
		if (a.length != b.length) throw new Exception( "Fail to add two arrays because different size." );
		for (int i = 0; i < b.length; i++) {
			a[ i ] -= b[ i ];
		}
		return a;
	}

	/**
	 * Using historical beta and current market & risk free rate to calculate alpha
	 * @param ret
	 * @param sp500
	 * @param rfr
	 */
	public void addAlpha(final double ret, final double sp500, final double rfr) {
		_alpha.addPathStat( ret - rfr - _beta * (sp500 - rfr) );
	}

	@Override
	public String toString() {
		String ret = "";
		for (final String ticker : _tickers) {
			ret += String.format( "%6s", ticker) + ',';
		}
		return ret.substring( 0, ret.lastIndexOf( ',' ) );
	}

	/***********************************************************************
	 * Getter and Setter
	 ***********************************************************************/
	public double getAlpha() {
		return _alpha.sampleMean();
	}

	public List<String> getTicker() {
		return _tickers;
	}

	public int getSize() {
		return _tickers.size();
	}
}
