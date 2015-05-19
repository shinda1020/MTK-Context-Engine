package mtk.ctxengine;

import java.io.IOException;
import java.util.LinkedList;

import mtk.ctxengine.sensors.ICtxUpdated;
import mtk.ctxengine.sensors.OffBoardSensorClient;
import mtk.ctxengine.sensors.onboard.Camera;
import mtk.ctxengine.sensors.onboard.IMU;

import org.json.JSONException;

/**
 * This class is the context engine in an application.
 * <p>
 * The context engine is responsible for the following things:
 * <p>
 * Sensor monitor: This context engine starts/stops and monitors both the on and
 * off-board sensor modules. For on-board sensors, the context engine
 * instantiates corresponding sensor wrappers and monitors their states. For
 * each off-board sensor, the context engine creates a Redis client thread and
 * receives events from Redis server via Pub/Sub.
 * <p>
 * Event passing: The context engine passes the sensor events to a particular
 * app via Java interface. The concrete event-handling is implemented in
 * specific apps.
 * 
 * @author Shinda
 * @version 1.0 05/15/2015
 */

public class ContextEngine implements ICtxUpdated {

	/* The host where Redis server is running */
	private String hostName;

	/* The path where methods.json file locates */
	private String methodFile;

	/******************************************************************
	 * On-board sensors
	 ******************************************************************/
	private static IMU imuSensor;
	private static Camera camSensor;

	/******************************************************************
	 * Off-board sensors
	 ******************************************************************/
	private static LinkedList<OffBoardSensorClient> sensorClients = new LinkedList<OffBoardSensorClient>();

	/******************************************************************
	 * Interface that handles sensor events
	 ******************************************************************/
	private ICtxUpdated ctxInterface = null;

	/******************************************************************
	 * Constructor, Setters & Getters
	 ******************************************************************/

	/**
	 * Naive constructor
	 */
	public ContextEngine(String _hostName, String _methodFile,
			ICtxUpdated _ctxInterface) {
		this.hostName = _hostName;
		this.methodFile = _methodFile;
		this.ctxInterface = _ctxInterface;

		// Set static variables
		OffBoardSensorClient.setHostName(hostName);
		OffBoardSensorClient.setMethodFile(methodFile);
	}

	public String getHostName() {
		return hostName;
	}

	public void setHostName(String hostName) {
		this.hostName = hostName;
	}

	public String getMethodFilePath() {
		return methodFile;
	}

	public void setMethodFilePath(String methodFilePath) {
		this.methodFile = methodFilePath;
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
	 * This function stops receiving messages from off-board sensor giving the
	 * sensor name.
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

		// Remove the sensor from the list
		sensorClients.remove(sensor);
	}

	/**
	 * This function queries the sensor client list and finds the sensor client
	 * giving the sensor name.
	 * 
	 * @param _sensorName
	 *            the name of the sensor
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
	 * This function passes the shaken event when a shake is detected from IMU
	 * module.
	 */
	@Override
	public void OnBoardIMUShakeDetected() {
		ctxInterface.OnBoardIMUShakeDetected();
	}

	/******************************************************************
	 * ICameraCtxUpdated interface handling
	 ******************************************************************/

	/**
	 * This function passes the face detected event when a face is detected from
	 * camera module.
	 */
	@Override
	public void OnBoardCameraFaceDetected() {
		ctxInterface.OnBoardCameraFaceDetected();
	}

	/******************************************************************
	 * IActivityCtxUpdated interface handling
	 ******************************************************************/

	/**
	 * This function passes the none activity event when it is detected from
	 * depth sensor.
	 */
	@Override
	public void OffBoardActivityNoneDetected() {
		ctxInterface.OffBoardActivityNoneDetected();
	}

	/**
	 * This function passes the low activity event when it is detected from
	 * depth sensor.
	 */
	@Override
	public void OffBoardActivityLowDetected() {
		ctxInterface.OffBoardActivityLowDetected();
	}

	/**
	 * This function passes the high speech activity event when it is detected
	 * from depth sensor.
	 */
	@Override
	public void OffBoardActivityHighDetected() {
		ctxInterface.OffBoardActivityHighDetected();
	}

	/******************************************************************
	 * ISpeechCtxUpdated interface handling
	 ******************************************************************/

	/**
	 * This function passes the none speech activity event when it is detected
	 * from microphone.
	 */
	@Override
	public void OffBoardSpeechNoneDetected() {
		ctxInterface.OffBoardSpeechNoneDetected();
	}

	/**
	 * This function passes the low speech activity event when it is detected
	 * from microphone.
	 */
	@Override
	public void OffBoardSpeechLowDetected() {
		ctxInterface.OffBoardSpeechLowDetected();
	}

	/**
	 * This function passes the high speech activity event when it is detected
	 * from microphone.
	 */
	@Override
	public void OffBoardSpeechHighDetected() {
		ctxInterface.OffBoardSpeechHighDetected();
	}

}
