package project.dataStructure;


public class CircularList<ValType> implements QInterface<ValType> {
	
	protected ValType[] _data;
	int                 _start;
	int                 _count;
	int                 _fullSize;
	
	public CircularList( 
		int fullSize		// Initial size of queue when it is considered full 
	) throws Exception {
		if( fullSize < 1 )
			throw new Exception( "Invalid size specification in CircularList" );
		_data = allocateSpace( fullSize );
		_start = 0;
		_count = 0;
		_fullSize = fullSize;
	}

	public void addLast(ValType value) {
		if( _count == _data.length ) {
			growTo( _count * 2 );
		}
		if( _count == _fullSize )
			_fullSize++;
		_data[ ( _start + _count ) % _fullSize ] = value;
		_count++;
	}

	// We have to allocate space like this because we don't know
	// the type of ValType.
	protected ValType[] allocateSpace( int size ) {
		@SuppressWarnings("unchecked")
		ValType[] newData = (ValType[]) new Object[ size ];
		return newData;
	}
	
	protected void growTo( int newSize ) {
		ValType[] newData = allocateSpace( _data.length * 2 );
		for( int i = 0; i < _data.length; i++ ) {
			newData[ i ] = this.getValue( i );
		}
		_data = newData;
		_start = 0;
	}

	public ValType removeFirst() throws Exception {
		ValType value = getValue( 0 );
		_start = ( _start + 1 ) % _fullSize;
		_count--;
		return value;
	}

	public ValType getValue( int index ) throws IndexOutOfBoundsException {
		if( ( index < 0 ) || ( index >= _count ) )
			throw new IndexOutOfBoundsException( "Can't get element at index " + index + " in CircularList of size " + _count );
		return _data[ ( index + _start ) % _fullSize ];
	}
	
	public ValType getFirstValue() { return getValue( 0 ); }
	public ValType getLastValue() { return getValue( _count - 1 ); }

	public int size() { return _count; }
	
	public boolean isReady() { return _count == _fullSize; }
	
	// Note that hasElements to remove may be different from isFull
	// but not in this case.
	public boolean hasElementsToRemove() { return isReady(); }

}
