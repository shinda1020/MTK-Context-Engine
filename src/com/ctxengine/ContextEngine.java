package com.ctxengine;

import com.ctxengine.sensors.ICtxUpdated;
import com.ctxengine.sensors.onboard.Camera;
import com.ctxengine.sensors.onboard.ICameraCtxUpdated;
import com.ctxengine.sensors.onboard.IIMUCtxUpdated;
import com.ctxengine.sensors.onboard.IMU;

/**
 * @author shinda
 * 
 */
public class ContextEngine implements ICtxUpdated {

	/******************************************************************
	 * On-board sensor control
	 ******************************************************************/
	private static IMU imuSensor;
	private static Camera camSensor;

	/**
	 * Naive constructor
	 */
	public ContextEngine() {

	}

	/******************************************************************
	 * Sensor control
	 ******************************************************************/

	/**
	 * This function instantiates the static imuSensor variable and starts the
	 * sensing service.
	 */
	public void startIMU() {
		if (imuSensor == null) {
			imuSensor = new IMU(this);
			imuSensor.startSensor();
		}
	}

	/**
	 * This function terminates the IMU sensing service.
	 */
	public void stopIMU() {
		if (imuSensor != null) {
			imuSensor.stopSensor();
		}
	}

	/**
	 * This function instantiates the static camSensor variable and starts the
	 * sensing service.
	 */
	public void startCamera() {
		if (camSensor == null) {
			camSensor = new Camera(this);
			camSensor.startSensor();
		}
	}

	/**
	 * This function terminates the camera sensing service.
	 */
	public void stopCamera() {
		if (camSensor != null) {
			camSensor.stopSensor();
		}
	}

	/******************************************************************
	 * IIMUCtxUpdated interface handling
	 ******************************************************************/

	/**
	 * This function handles the shaken event when a shake is detected from IMU
	 * module.
	 */
	@Override
	public void OnBoardIMUShakeDetected() {
		System.out.println("Shake detected");
	}

	/******************************************************************
	 * ICameraCtxUpdated interface handling
	 ******************************************************************/

	/**
	 * This function handles the face detected event when a face is detected
	 * from camera module.
	 */
	@Override
	public void OnBoardCameraFaceDetected() {
		System.out.println("Face detected");
	}

	/******************************************************************
	 * IActivityCtxUpdated interface handling
	 ******************************************************************/

	/**
	 * Implement this function to handle none-activity event.
	 */
	@Override
	public void OffBoardActivityNoneDetected() {

	}

	/**
	 * Implement this function to handle low-activity event.
	 */
	@Override
	public void OffBoardActivityLowDetected() {

	}

	/**
	 * Implement this function to handle high-activity event.
	 */
	@Override
	public void OffBoardActivityHighDetected() {

	}

}
