package com.ctxengine.sensors.interfaces;

/**
 * This interface serves as the delegate to pass the camera sensor events to the
 * context engine.
 * 
 * @author Shinda
 * @version 1.0 05/10/2015
 */
public interface ICameraCtxUpdated {
	/**
	 * Implement this function to handle shaken event.
	 */
	void OnBoardCameraFaceDetected();
}
