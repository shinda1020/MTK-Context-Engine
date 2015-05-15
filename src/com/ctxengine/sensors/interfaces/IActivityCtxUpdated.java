package com.ctxengine.sensors.interfaces;

/**
 * This interface serves as the delegate to pass the activity sensor events to
 * the context engine.
 * 
 * @author Shinda
 * @version 1.0 05/10/2015
 */
public interface IActivityCtxUpdated {
	/**
	 * Implement this function to handle none-activity event.
	 */
	void OffBoardActivityNoneDetected();

	/**
	 * Implement this function to handle low-activity event.
	 */
	void OffBoardActivityLowDetected();

	/**
	 * Implement this function to handle high-activity event.
	 */
	void OffBoardActivityHighDetected();
}
