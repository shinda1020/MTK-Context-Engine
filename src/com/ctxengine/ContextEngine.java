package com.ctxengine;

import com.ctxengine.sensors.OffBoardSensorClient;
import com.ctxengine.sensors.interfaces.ICtxUpdated;
import com.ctxengine.sensors.onboard.Camera;
import com.ctxengine.sensors.onboard.IMU;

/**
 * @author shinda
 * 
 */
public class ContextEngine implements ICtxUpdated {

	// The host where Redis server is running
	String hostName = "localhost";

	/******************************************************************
	 * On-board sensor
	 ******************************************************************/
	private static IMU imuSensor;
	private static Camera camSensor;

	/******************************************************************
	 * Off-board sensor
	 ******************************************************************/

	/**
	 * Naive constructor
	 */
	public ContextEngine() {
		OffBoardSensorClient.setHostName(hostName);
	}

	/******************************************************************
	 * On-board sensor control
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
	 * Off-board sensor subscription
	 ******************************************************************/

	/**
	 * This function instantiates the static actSensor variable and starts the
	 * sensing service.
	 */
	public void startActivity() {
		OffBoardSensorClient act = new OffBoardSensorClient("Activity", this);
		act.startSensor();
	}

	/**
	 * This function terminates the activity sensing service.
	 */
	public void stopActivity() {

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
		System.out.println("None");
	}

	/**
	 * Implement this function to handle low-activity event.
	 */
	@Override
	public void OffBoardActivityLowDetected() {
		System.out.println("Low");
	}

	/**
	 * Implement this function to handle high-activity event.
	 */
	@Override
	public void OffBoardActivityHighDetected() {
		System.out.println("High");
	}

}
