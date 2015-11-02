package mtk.ctxengine;

import java.io.IOException;
import java.util.ArrayList;

import mtk.ctxengine.sensors.SensorMessageHandler;
import mtk.ctxengine.sensors.LocalSensor;
import mtk.ctxengine.sensors.RemoteSensor;
import mtk.ctxengine.sensors.Sensor;
import mtk.ctxengine.sensors.SensorFactory;
import mtk.ctxengine.sensors.SensorFactory.SensorFactoryException;

import org.json.JSONException;

/**
 * Memory Toolkit Context Engine
 * <p>
 * This context engine starts/stops both the local and remote sensor modules, as
 * well as handles their corresponding sensor events/messages. For local
 * sensors, the context engine instantiates corresponding sensor wrappers and
 * monitors their states. For each remote sensor, the context engine creates a
 * Redis client thread and receives events from Redis server via Pub/Sub.
 * <p>
 * 
 * @author Xinda Zeng <xinda@umich.edu>
 * @version 1.1 10/20/2015
 */

public abstract class ContextEngine {

	/* The host where Redis server is running */
	protected String hostName;

	/* The path where the sensor info file locates */
	protected String sensorInfoPath;

	/* The sensor factory that is used for creating new sensor instances */
	protected SensorFactory sensorFactory;

	/* The sensor list that stores all the active sensors */
	protected ArrayList<Sensor> activeSensorList;

	/******************************************************************
	 * Constructor, Setters & Getters
	 ******************************************************************/

	public ContextEngine(String hostName, String sensorInfoPath) {
		this.hostName = hostName;
		this.sensorInfoPath = sensorInfoPath;

		activeSensorList = new ArrayList<Sensor>();

		try {
			sensorFactory = new SensorFactory(hostName, sensorInfoPath);
		} catch (SensorFactoryException e) {
			e.printStackTrace();
		}
	}

	public String getHostName() {
		return hostName;
	}

	public void setHostName(String hostName) {
		this.hostName = hostName;
	}

	public String getSensorInfoPath() {
		return sensorInfoPath;
	}

	public void setSensorInfoPath(String sensorInfoPath) {
		this.sensorInfoPath = sensorInfoPath;
	}

	/******************************************************************
	 * Sensor control
	 ******************************************************************/

	/**
	 * Start Sensor
	 * <p>
	 * This method starts the sensor service with the given name via first
	 * creating a sensor instance from the sensor factory, then starting the
	 * sensor and adding it to the active sensor list.
	 * </p>
	 * 
	 * @param sensorName
	 *            the name of the sensor module that is to be started. This name
	 *            shall be defined in the sensor info file.
	 * @param sensorMessageHandler
	 *            the sensor message handler.
	 * @throws SensorFactoryException
	 * @throws IOException
	 * @throws JSONException
	 * @throws ContextEngineException
	 */
	public void startSensor(String sensorName,
			SensorMessageHandler sensorMessageHandler) throws JSONException,
			IOException, SensorFactoryException, ContextEngineException {
		if (getSensorWithName(sensorName) != null) {
			throw new ContextEngineException(
					ContextEngineException.EXCEPTION_CONTEXT_ENGINE_SENSOR_ALREADY_STARTED);
		}

		Sensor sensor = null;

		sensor = sensorFactory.createSensor(sensorName, sensorMessageHandler);

		if (sensor != null) {
			sensor.start();
			activeSensorList.add(sensor);
		}
	}

	/**
	 * Stop Sensor
	 * <p>
	 * This method firstly retrieves the sensor with the given name. Then it
	 * stops the sensor and removes it from the active sensor list.
	 * </p>
	 * 
	 * @param sensorName
	 * @throws ContextEngineException
	 */
	public void stopSensor(String sensorName) throws ContextEngineException {
		Sensor sensor = getSensorWithName(sensorName);
		if (sensor == null) {
			throw new ContextEngineException(
					ContextEngineException.EXCEPTION_CONTEXT_ENGINE_SENSOR_HAS_NOT_BEEN_STARTED);
		}

		sensor.stop();
	}

	/**
	 * Get Sensor With Name
	 * <p>
	 * This method attempts to get the sensor with the given name from the
	 * sensor list. If no sensor with the given name is found in the list,
	 * return a null object
	 * </p>
	 * 
	 * @param sensorName
	 * @return the sensor instance if found with the same name, or null if not
	 *         found.
	 */
	public Sensor getSensorWithName(String sensorName) {
		for (int i = 0; i < activeSensorList.size(); ++i) {
			Sensor temp = activeSensorList.get(i);
			if (temp.getSensorName() == sensorName) {
				return temp;
			}
		}
		return null;
	}

	/**
	 * Memory Toolkit Context Engine Exception
	 * <p>
	 * This class represents a recoverable error within the work flow of the
	 * {@link Sensor}
	 * </p>
	 * 
	 * @author Xinda Zeng <xinda@umich.edu>
	 * @version 1.1 10/20/2015
	 */
	public class ContextEngineException extends Exception {

		private static final long serialVersionUID = 9096173435450040774L;

		/**
		 * @see {@link ContextEngine#startSensor(String)}
		 */
		public static final String EXCEPTION_CONTEXT_ENGINE_SENSOR_ALREADY_STARTED = "This sensor has already been started.";

		/**
		 * @see {@link ContextEngine#stopSensor(String)}
		 */
		public static final String EXCEPTION_CONTEXT_ENGINE_SENSOR_HAS_NOT_BEEN_STARTED = "This sensor has not been started yet and hence could not be stopped.";

		/**
		 * Default Constructor
		 * 
		 * @param message
		 *            object of type {@link String} that contains the error
		 *            details
		 */
		public ContextEngineException(String message) {
			super(message);
		}
	}
}
