package com.ctxengine.sensors.offboard;

/**
 * This interface serves as the delegate to pass the speech events to the
 * context engine.
 * 
 * @author Shinda
 * @version 1.0 05/10/2015
 */
public interface ISpeechCtxUpdated {
	/**
	 * Implement this function to handle none-speech event.
	 */
	void OffBoardSpeechNoneDetected();

	/**
	 * Implement this function to handle low-speech event.
	 */
	void OffBoardSpeechLowDetected();

	/**
	 * Implement this function to handle high-speech event.
	 */
	void OffBoardSpeechHighDetected();
}
