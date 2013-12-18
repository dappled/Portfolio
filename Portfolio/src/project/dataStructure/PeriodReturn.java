package project.dataStructure;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

/**
 * @author Zhenghong Dong
 */
public class PeriodReturn extends CircularList<Column> {
	Double[]		_ret;
	int				_nPort;
	final Integer[]	_indx;

	public PeriodReturn(int period, int n) throws Exception {
		super( period );
		_nPort = n;
		_ret = new Double[ _nPort ];
		for (int i = 0; i < _nPort; i++) {
			_ret[ i ] = 0.0;
		}
		_indx = new Integer[ _nPort ];
		for (int i = 0; i < _nPort; i++) {
			_indx[ i ] = i;
		}
	}

	@SuppressWarnings("unchecked")
	public Column add(long date, ArrayList<Double> ret) throws Exception {
		Column remove = addLast( new Column( date, (ArrayList<Double>) ret.clone() ) );
		addReturn( ret, remove );
		return remove;
	}

	private void addReturn(ArrayList<Double> add, Column remove) {
		for (int i = 0; i < _nPort; i++) {
			_ret[ i ] += add.get( i );
			if (remove != null) {
				_ret[ i ] -= remove.ret.get( i );
			}
		}
	}

	public List<Integer[]> partition(int h, int ms, int me, int l) {
		Integer[] res = _indx.clone();
		Arrays.sort( res, new Comparator<Integer>() {
			@Override
			public int compare(final Integer o1, final Integer o2) {
				return -1 * Double.compare( _ret[ o1 ], _ret[ o2 ] );
			}
		} );
		return Arrays.asList( Arrays.copyOfRange( res, 0, h ), Arrays.copyOfRange( res, ms, me ),
				Arrays.copyOfRange( res, res.length - l, res.length ) );
	}

	public double getEquallyWeightedReturn(Integer[] indx, int period) {
		double res = 0;
		for (int i : indx) {
			res += getValue( period).ret.get( i );
		}
		return res / indx.length;
	}
}
