package ps1.insertionSort;

import java.util.Comparator;
import java.util.List;

public interface I_SortAlgorithm<ValType> {

	public void sort( ValType[] values, Comparator<ValType> comparator ) throws Exception;
	
	public void sort( List<ValType> values, Comparator<ValType> comparator) throws Exception;
}
