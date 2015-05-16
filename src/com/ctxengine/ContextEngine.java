package com.ctxengine;

import java.io.IOException;
import java.util.LinkedList;

import org.json.JSONException;

import com.ctxengine.sensors.OffBoardSensorClient;
import com.ctxengine.sensors.interfaces.ICtxUpdated;
import com.ctxengine.sensors.onboard.Camera;
import com.ctxengine.sensors.onboard.IMU;

/**
 * @author shinda
 * 
 */
public class ContextEngine implements ICtxUpdated {

	/* The host where Redis server is running */
	String hostName = "localhost";

	/* The path where methods.json file locates */
	String methodFile = "methods.json";

	/******************************************************************
	 * On-board sensor
	 ******************************************************************/
	private static IMU imuSensor;
	private static Camera camSensor;

	/******************************************************************
	 * Off-board sensor
	 ******************************************************************/
	private static LinkedList<OffBoardSensorClient> sensorClients = new LinkedList<OffBoardSensorClient>();

	/**
	 * Naive constructor
	 */
	public ContextEngine() {
		// Set static variables
		OffBoardSensorClient.setHostName(hostName);
		OffBoardSensorClient.setMethodFile(methodFile);
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
	 * Off-board sensor control
	 ******************************************************************/

	/**
	 * This function instantiates an off-board sensor client giving the sensor
	 * name.
	 * <p>
	 * The method firstly query the sensor list to see if the particular sensor
	 * has been started. If not, the method instantiates a new
	 * OffBoardSensorClient and starts the service.
	 * 
	 */
	public void startOffBoardSensor(String _sensorName) {
		// First check if the sensor has been started
		if (this.getOffBoardSensor(_sensorName) != null) {
			return;
		}

		// Instantiate a new OffBoardSensorClient
		OffBoardSensorClient sensor;
		try {
			sensor = new OffBoardSensorClient(_sensorName, this);
			sensor.startSensor();
			sensorClients.addLast(sensor);
		}
		// Wrong JSON format
		catch (JSONException e) {
			e.printStackTrace();
		}
		// methods.json file not found, or cannot be read
		catch (IOException e) {
			e.printStackTrace();
		}

	}

	/**
	 * 
	 */
	public void stopOffBoardSensor(String _sensorName) {
		OffBoardSensorClient sensor = this.getOffBoardSensor(_sensorName);

		// First check if the sensor exists
		if (sensor == null) {
			return;
		}

		// Stop the sensor
		sensor.stopSensor();
	}

	/**
	 * This function queries the sensor client list and finds the sensor client
	 * giving the sensor name.
	 * 
	 * @return The off-board sensor client regarding the given sensor name, if
	 *         any.
	 */
	public OffBoardSensorClient getOffBoardSensor(String _sensorName) {
		for (int i = 0; i < sensorClients.size(); ++i) {
			OffBoardSensorClient temp = sensorClients.get(i);
			if (temp.getSensorName() == _sensorName) {
				return temp;
			}
		}
		return null;
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
