package ps1;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import ps1.insertionSort.GenericInsertionSort;
import ps1.trackingError.I_PathStats;
import ps1.trackingError.PathStat;

/**
 * @author Zhenghong Dong
 */
public class Problem1 {
	private String							_fileName;
	private final List<I_PathStats<Double>>	_error;

	/***********************************************************************
	 * Constructor
	 ***********************************************************************/
	public Problem1(final String fileName) {
		_fileName = fileName;
		_error = new ArrayList<>();
	}

	/***********************************************************************
	 * Utilities
	 ***********************************************************************/
	/**
	 * Read the file and calculate tracking errors
	 * @throws Exception
	 */
	public void read() throws Exception {
		final Workbook wb = WorkbookFactory.create( new FileInputStream( _fileName ) );
		final Sheet sheet1 = wb.getSheet( "ARDATA" );
		for (final Row row : sheet1) {
			if (row.getRowNum() == 0) {
				continue; // ignore first line
			}

			for (int i = 3; i < row.getLastCellNum(); i++) {
				if (row.getRowNum() == 1) { // row 1 contains funds name
					_error.add( PathStat.<Double> newPathStat( row.getCell( i ).getStringCellValue() ) );
				} else {
					_error.get( i - 3 ).addPathStat(
							row.getCell( i ).getNumericCellValue() - row.getCell( 2 ).getNumericCellValue() );
				}
			}
		}

		// print tracking error
		System.out.println( "Before sorting:" );
		for (final I_PathStats<Double> i : _error) {
			System.out.printf( "name: %6s, tracking error: %.3f\n", ((PathStat<Double>) i).getName(),
					i.sampleTrackingError() * Math.sqrt( 12 ) );
		}

		// print sorted one
		new GenericInsertionSort<I_PathStats<Double>>().sort( _error, new Comparator<I_PathStats<Double>>() {

			@Override
			public int compare(final I_PathStats<Double> o1, final I_PathStats<Double> o2) {
				return Double.compare( o1.sampleTrackingError(), o2.sampleTrackingError() );
			}

		} );
		System.out.println( "After sorting:" );
		for (final I_PathStats<Double> i : _error) {
			System.out.printf( "name: %6s, tracking error: %.3f\n", ((PathStat<Double>) i).getName(),
					i.sampleTrackingError() * Math.sqrt( 12 ) );
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
		final String fileName = "C:\\Users\\Zhenghong Dong\\SkyDrive\\dappled's sky\\poly-mfe\\13f\\Portfolio\\PS1 Tracking Error Data.xls";
		final Problem1 p1 = new Problem1( fileName );

		p1.read();
	}
}
