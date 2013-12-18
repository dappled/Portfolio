package project.dataStructure;

public class CircularList<ValType> implements QInterface<ValType> {

	protected ValType[]	_data;
	int					_start;
	int					_count;
	int					_fullSize;

	public CircularList(
			int fullSize // Initial size of queue when it is considered full
	) throws Exception {
		if (fullSize < 1) throw new Exception( "Invalid size specification in CircularList" );
		_data = allocateSpace( fullSize );
		_start = 0;
		_count = 0;
		_fullSize = fullSize;
	}

	public ValType addLast(ValType value) throws Exception {
		ValType ret = null;
		if (_count == _data.length) {
			ret = removeFirst();
		}
		_data[ (_start + _count) % _fullSize ] = value;
		_count++;
		return ret;
	}

	// We have to allocate space like this because we don't know
	// the type of ValType.
	protected ValType[] allocateSpace(int size) {
		@SuppressWarnings("unchecked")
		ValType[] newData = (ValType[]) new Object[ size ];
		return newData;
	}

	public ValType removeFirst() throws Exception {
		ValType value = getValue( 0 );
		_start = (_start + 1) % _fullSize;
		_count--;
		return value;
	}

	public ValType getValue(int index) throws IndexOutOfBoundsException {
		if ((index < 0) || (index >= _count)) throw new IndexOutOfBoundsException( "Can't get element at index " + index + " in CircularList of size " + _count );
		return _data[ (index + _start) % _fullSize ];
	}

	public ValType getFirstValue() {
		return getValue( 0 );
	}

	public ValType getLastValue() {
		return getValue( _count - 1 );
	}

	public int size() {
		return _count;
	}
	

	public boolean isReady() {
		return _count == _fullSize;
	}

	// Note that hasElements to remove may be different from isFull
	// but not in this case.
	public boolean hasElementsToRemove() {
		return isReady();
	}
	
	public static void main(String[] args) throws Exception {
		CircularList<Double> test = new CircularList<>( 3 );
		
		test.addLast( 3.0 );
		System.out.println(test.isReady());
		test.addLast( 2.0 );
		test.addLast( 1.0 );
		System.out.println(test.isReady());
		test.removeFirst(); // should now be {2;1}
		System.out.println(test.isReady());
		
		test.addLast( 4.0 ); // should now be {2;1;4}
		System.out.println(test.addLast( 10.0 )); // should remove 2, and be {1;4;10}
		System.out.println(test.addLast( 20.0 )); // should remove 1, and be {4;10;20}
		System.out.println(test.addLast( 30.0 )); // should remove 4, and be {10;20;30}
		
		System.out.println(test.getFirstValue()); // should return 10
		System.out.println(test.getLastValue()); // should return 30
		
		int[] a = {1,2,3};
		int[] b = a.clone();
		b[0] = 9;
		System.out.println(a[0]);
		System.out.println(b[0]);
		System.out.printf( "End");
	}

}
