package com.ctxengine.sensors.onboard;

import com.ctxengine.sensors.OnBoardSensor;

/**
 * This class is a camera sensor class inherited from OnBoardSensor.
 * <p>
 * In particular, this sensor class handles face detected event.
 * <p>
 * 
 * @author Shinda
 * @version 1.0 05/10/2015
 */
public final class Camera extends OnBoardSensor implements ICameraCtxUpdated {

	/* The name of the sensor thread */
	final private String SENSOR_THREAD_NAME = "Cam";

	/* The path where the sensor module executable is stored */
	final private String SENSOR_MODULE_PATH = "sensorbin/cam";

	/*
	 * The interface to which this sensor module passes events for actual
	 * handling
	 */
	private ICameraCtxUpdated ctxInterface = null;

	/**
	 * This is the constructor.
	 * 
	 * @param ICameraCtxUpdated
	 *            The ICameraCtxUpdated interface that to which this sensor
	 *            module passes events for actual handling.
	 */
	public Camera(ICameraCtxUpdated _ctxInterface) {
		setCtxInterface(_ctxInterface);
	}

	/******************************************************************
	 * Implementation of abstract methods
	 ******************************************************************/

	/**
	 * Override abstract function from parent class to update context.
	 * <p>
	 * Shake : the shaken event
	 * 
	 * @param msg
	 *            The sensor message read from stdout.
	 */
	@Override
	protected void handleSensorMsg(String msg) {
		if (msg.compareToIgnoreCase("face") == 0) {
			this.OnBoardCameraFaceDetected();
		}
	}

	/**
	 * Override abstract function from parent class to configure the path of the
	 * sensor module executable.
	 * 
	 * @return The path where camera module executable is stored.
	 */
	@Override
	final protected String getSensorModulePath() {
		return this.SENSOR_MODULE_PATH;
	}

	/**
	 * Override abstract function from parent class to configure the name of the
	 * camera sensor thread.
	 * 
	 * @return The name of the camera sensor thread.
	 */
	@Override
	final protected String getSensorThreadName() {
		return this.SENSOR_THREAD_NAME;
	}

	/******************************************************************
	 * ICameraCtxUpdated interface
	 ******************************************************************/

	/**
	 * This function implements the faceDetected function in the
	 * ICameraCtxUpdated interface by passing the event to the context engine.
	 */
	@Override
	public void OnBoardCameraFaceDetected() {
		ctxInterface.OnBoardCameraFaceDetected();
	}

	/**
	 * This is the local setter of the ICameraCtxUpdated.
	 * 
	 * @param _ctxInterface
	 *            The ICameraCtxUpdated that actually does the event handling.
	 */
	public void setCtxInterface(ICameraCtxUpdated _ctxInterface) {
		this.ctxInterface = _ctxInterface;
	}

}
