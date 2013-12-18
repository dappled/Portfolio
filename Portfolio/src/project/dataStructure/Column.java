package project.dataStructure;

import java.util.ArrayList;

/**
 * @author Zhenghong Dong
 */
public class Column {
	public long					date;
	public ArrayList<Double>	ret;

	public Column(long d, ArrayList<Double> r) {
		date = d;
		ret = r;
	}

}
