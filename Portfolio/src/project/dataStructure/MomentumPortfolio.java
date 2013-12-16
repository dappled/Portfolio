package project.dataStructure;

import java.util.ArrayList;
import java.util.List;

import org.joda.time.LocalDate;

/**
 * @author Zhenghong Dong
 */
public class MomentumPortfolio {
	private class longShort {
		private final List<String> _longNames;
		private final List<String> _shortNames;
		public longShort(List<String> longNames, List<String> shortNames) {
			_longNames = longNames;
			_shortNames = shortNames;
		}
		
		public List<String> getLong() { return _longNames; }
		public List<String> getShort() { return _shortNames; }
	}
	
	private longShort components;
	private LocalDate startEstimateDate, endEstimateDate, startHoldingDate, endHoldingDate;
	private List<Double> holdingPeriodReturn;
	
	public MomentumPortfolio(LocalDate startDate, int l, int h, int longNum, int shortNum) {
		startEstimateDate = startDate;
		endEstimateDate = startEstimateDate.minusMonths( -l );
		startHoldingDate =  endEstimateDate.minusMonths( -1 );
		endHoldingDate = startHoldingDate.minusMonths( -h );
		holdingPeriodReturn = new ArrayList<>();
	}
	
	

}
