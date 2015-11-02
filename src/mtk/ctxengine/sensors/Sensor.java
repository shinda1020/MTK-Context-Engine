package mtk.ctxengine.sensors;

/**
 * Memory Toolkit Sensor
 * <p>
 * This class is an abstract class of all sensor services. It defines the basic
 * behaviors of both local/on-board and remote/off-board sensors.
 * </p>
 * 
 * @author Xinda Zeng <xinda@umich.edu>
 * @version 1.1 10/20/2015
 */

public abstract class Sensor {

	/**
	 * This abstract function defines the behavior that starts the sensor
	 * module.
	 * 
	 */
	public abstract void start();

	/**
	 * This abstract function defines the behavior that stops the sensor module.
	 * 
	 */
	public abstract void stop();

	/**
	 * This abstract function returns the sensor name.
	 * 
	 */
	public abstract String getSensorName();

}
