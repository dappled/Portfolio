package utils;

/**
 * Default date is yyyyMM in Long format
 * @author Zhenghong Dong
 */
public class ParseDateLong {
	public static long addMonth(long date, int drift) {
		date += drift / 12 * 100;
		drift = drift % 12;
		long month = date % 100 + drift;
		if (month > 12) {
			date += 100 + (drift - 12);
		} else if (month <= 0) {
			date -= 100 - (12 + drift);
		} else {
			date = date + drift;
		}
		return date;
	}

	public static void main(String[] args) {
		System.out.println( addMonth( 201212, 1 ) );

		System.out.println( "End" );
	}
}
