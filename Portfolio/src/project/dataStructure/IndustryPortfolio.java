package project.dataStructure;

import java.util.LinkedHashMap;

/**
 * @author Zhenghong Dong
 */
public class IndustryPortfolio {
	private final String _name;
	private LinkedHashMap<Long, Double> _ret;
	
	/***********************************************************************
	 * Constructor
	 ***********************************************************************/
	public IndustryPortfolio(String name) {
		_name = name;
		_ret = new LinkedHashMap<>();
	}
	
	
	/***********************************************************************
	 * Utilities
	 ***********************************************************************/
	public void addMonthlyReturn(long date, double ret) {
		_ret.put( date, ret );
	}
	
	
	/***********************************************************************
	 * Getter and Setter
	 ***********************************************************************/
	public LinkedHashMap<Long, Double> getRet() {
		return _ret;
	}

	public String getName() {
		return _name;
	}
}
