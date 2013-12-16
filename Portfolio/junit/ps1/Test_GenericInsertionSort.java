package ps1;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

import org.junit.Assert;
import org.junit.Test;

import ps1.insertionSort.GenericInsertionSort;

/**
 * @author Zhenghong Dong
 */
public class Test_GenericInsertionSort {

	private final Comparator<Integer>	intComparator	= new Comparator<Integer>() {

		@Override
		public int compare(final Integer i1, final Integer i2) {
			return i1.compareTo( i2 );
		}

	};
	
	@Test
	/**
	 * Make sure our validation method works
	 */
	public void testTheTester() {
		Integer[] values = { 1, 0, 2, 3, 4, 6, 5 };
		assertFalse( validate( values ) );
		Integer[] values1 = { 0, 1, 2, 3, 4, 5, 6 };
		assertTrue( validate( values1 ) );
	}
	
	@Test
	/**
	 * Test sorting of empty array - Should just leave it alone
	 * @throws Exception If the array of values passed to the sort is null
	 */
	public void testEmpty() throws Exception {
		(new GenericInsertionSort<Integer>()).sort( new Integer[0], intComparator );
	}

	@Test
	/**
	 * Test sorting of array with one element
	 * @throws Exception If the array of values passed to the sort is null
	 */
	public void testSimpleElement() throws Exception {
		Integer[] values = new Integer[ 1 ];
		values[ 0 ] = 5;
		(new GenericInsertionSort<Integer>()).sort( values, intComparator );
	}

	@Test
	/**
	 * Test sorting of array with duplicates
	 * @throws Exception Exception is thrown if array passed to sorts is null
	 */
	public void testSpecialSort() throws Exception {
		// Test insertion sort
		final Integer[] test = { 5, 5, 6, 6, 4, 4, 5, 5, 4, 4, 6, 6, 5, 5 };
		(new GenericInsertionSort<Integer>()).sort( test, intComparator );
		Assert.assertTrue( validate( test ) );

		final List<Integer> test2 = new ArrayList<>();
		test2.add( 31 );test2.add( 41 );test2.add( 59 );test2.add( 26 );test2.add( 41 );test2.add( 58 );
		(new GenericInsertionSort<Integer>()).sort( test2, intComparator );
		Assert.assertTrue( validate( test2 ) );
	}
	
	/**
	 * Test sorting of random arrays
	 * @throws Exception Exception is thrown if array passed to sorts is null 
	 */
	public void testRandomSort() throws Exception {
		Random generator = new Random();
		for( int iNumRuns = 0; iNumRuns < 100; iNumRuns++ ) {
			int arraySize = ( int )( 20 * generator.nextDouble() );
			Integer[] values = new Integer[ arraySize ];
			for (int i = 0; i < values.length; i++)
				values[i] = generator.nextInt( 5000 ) - 2500;
			(new GenericInsertionSort<Integer>()).sort( values, intComparator );
			assertTrue( validate(values) );
		}		
	}
	
	/**
	 * Method to validate that an array is in order
	 * @param values Array that is supposed to be in ascending order
	 * @return True if array is in order or false if it is not
	 */
	private <ValType extends Comparable<ValType>> boolean validate(final ValType[] values) {
		for (int i = 0; i < values.length - 1; i++)
			if (values[ i ].compareTo( values[ i + 1 ] ) > 0) return false;
		return true;
	}
	private <ValType extends Comparable<ValType>> boolean validate(final List<ValType> values) {
		for (int i = 0; i < values.size() - 1; i++)
			if (values.get( i ).compareTo( values.get( i + 1 ) ) > 0) return false;
		return true;
	}

}
