/**
 * 
 */
package com.ctxengine.sensors;

/**
 * This interface serves as the delegate to pass IMU sensor events to the
 * context engine.
 * 
 * @author Shinda
 * @version 1.0 05/10/2015
 */
public interface IIMUCtxUpdated {

	/**
	 * Implement this function to handle shaken event.
	 */
	void shakeDetected();
}
