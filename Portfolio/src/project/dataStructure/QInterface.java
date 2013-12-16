package project.dataStructure;

public interface QInterface<ValType> {

	public ValType addLast(ValType valType) throws Exception;

	public ValType removeFirst() throws Exception;

	public boolean isReady();

	public boolean hasElementsToRemove();

}
