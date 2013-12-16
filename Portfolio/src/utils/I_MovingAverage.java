package utils;

public interface I_MovingAverage {

	public boolean add(double value);

	public boolean isReady();

	public double getAverage() throws Exception;
	
	public String getKey();

}
