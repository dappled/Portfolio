package ps1.insertionSort;

import java.util.Comparator;
import java.util.List;

public class GenericInsertionSort<ValType> implements I_SortAlgorithm<ValType> {

	/***********************************************************************
	 * Constructor
	 ***********************************************************************/
	public GenericInsertionSort() {}

	/***********************************************************************
	 * {@link I_SortAlgorithm} methods
	 ***********************************************************************/
	@Override
	public void sort(final ValType[] values, final Comparator<ValType> comparator) throws Exception {
		final int N = values.length;
		for (int j = 1; j < N; j++) {
			final ValType key = values[ j ];
			int i = j - 1;
			while (i >= 0 && less( comparator, key, values[ i ] )) {
				values[ i + 1 ] = values[ i ];
				i--;
			}
			values[ i + 1 ] = key;
		}
	}

	@Override
	public void sort(final List<ValType> values, final Comparator<ValType> comparator) throws Exception {
		final int N = values.size();
		for (int j = 1; j < N; j++) {
			final ValType key = values.get( j );
			int i = j - 1;
			while (i >= 0 && less( comparator, key, values.get( i ) )) {
				values.set( i + 1, values.get( i ) );
				i--;
			}
			values.set( i + 1, key );
		}
	}


	/***********************************************************************
	 * Helper sorting functions
	 ***********************************************************************/
	// is v < w ?
	private boolean less(final Comparator<ValType> c, final ValType v, final ValType w) {
		return (c.compare( v, w ) < 0);
	}

}
