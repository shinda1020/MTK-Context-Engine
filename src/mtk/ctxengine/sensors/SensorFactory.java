package mtk.ctxengine.sensors;

/**
 * Memory Toolkit Sensor Factory
 * <p>
 * This class is factory class that instantiates both local/on-board and remote/off-board
 * sensors.
 * </p>
 * 
 * @author Xinda Zeng <xinda@umich.edu>
 * @version 1.1 10/20/2015
 */

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class SensorFactory {

/**
	 * @see {@link SensorFactory#createLocalSensor(String)
	 * @see {@link SensorFactory#createRemoteSensor(String)
	 */
	public final static String SENSOR_TYPE_LOCAL = "local";

/**
	 * @see {@link SensorFactory#createLocalSensor(String)
	 * @see {@link SensorFactory#createRemoteSensor(String)
	 */
	public final static String SENSOR_TYPE_REMOTE = "remote";

/**
	 * @see {@link SensorFactory#createLocalSensor(String)
	 * @see {@link SensorFactory#createRemoteSensor(String)
	 */
	public static final String SENSOR_TYPE_TAG = "sensorType";

/**
	 * @see {@link SensorFactory#createLocalSensor(String)
	 * @see {@link SensorFactory#createRemoteSensor(String)
	 */
	public static final String SENSOR_NAME_TAG = "sensorName";

/**
	 * @see {@link SensorFactory#createLocalSensor(String)
	 * @see {@link SensorFactory#createRemoteSensor(String)
	 */
	public static final String SENSOR_MODULE_PATH_TAG = "sensorModulePath";

/**
	 * @see {@link SensorFactory#createLocalSensor(String)
	 * @see {@link SensorFactory#createRemoteSensor(String)
	 */
	public static final String SENSOR_CHANNEL_TAG = "sensorChannel";

/**
	 * @see {@link SensorFactory#createLocalSensor(String)
	 * @see {@link SensorFactory#createRemoteSensor(String)
	 */
	public static final String SENSOR_MESSAGES_TAG = "sensorMessages";

	/* The host where Redis server is running */
	private String hostName;

	/* The path where JSON file locates */
	private String sensorInfoPath;

	/******************************************************************
	 * Constructor, Setters & Getters
	 ******************************************************************/

	public SensorFactory(String hostName, String sensorInfoPath)
			throws SensorFactoryException {
		if (null == hostName) {
			throw new SensorFactoryException(
					SensorFactoryException.EXCEPTION_SENSOR_FACTORY_REDIS_HOST_NAME_NOT_DEFINED);
		}

		if (null == sensorInfoPath) {
			throw new SensorFactoryException(
					SensorFactoryException.EXCEPTION_SENSOR_FACTORY_SENSOR_INFO_FILE_NOT_DEFINED);
		}

		this.hostName = hostName;
		this.sensorInfoPath = sensorInfoPath;
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
	 * Factory methods
	 ******************************************************************/

	/**
	 * Create Sensor Instance
	 * <p>
	 * Create a sensor instance regarding the given sensor name, which is
	 * supposed to be defined in the JSON sensor info file.
	 * </p>
	 * 
	 * @param sensorName
	 *            the sensor name defined in the JSON sensor info file.
	 * @param sensorMessageHandler
	 *            the sensor message handler.
	 * @return the created sensor instance given the sensor name.
	 * @throws JSONException
	 * @throws IOException
	 * @throws SensorFactoryException
	 */
	public Sensor createSensor(String sensorName,
			SensorMessageHandler sensorMessageHandler) throws JSONException,
			IOException, SensorFactoryException {

		BufferedReader br = new BufferedReader(new FileReader(sensorInfoPath));
		boolean sensorFound = false;
		Sensor sensor = null;

		String jsonString = br.readLine();

		while (jsonString != null) {
			JSONObject jsonObject = new JSONObject(jsonString);
			String tempSensorName = jsonObject.getString(SENSOR_NAME_TAG);

			// Sensor found
			if (tempSensorName.compareToIgnoreCase(sensorName) == 0) {
				sensorFound = true;

				String sensorType = jsonObject.getString(SENSOR_TYPE_TAG);
				// Local sensor
				if (sensorType.compareToIgnoreCase(SENSOR_TYPE_LOCAL) == 0) {
					sensor = createLocalSensor(jsonString, sensorMessageHandler);
				}
				// Remote sensor
				else if (sensorType.compareToIgnoreCase(SENSOR_TYPE_REMOTE) == 0) {
					sensor = createRemoteSensor(jsonString,
							sensorMessageHandler);
				}

				// Stop reading file
				break;
			}

			jsonString = br.readLine();
		}

		br.close();

		if (!sensorFound) {
			throw new SensorFactoryException(
					SensorFactoryException.EXCEPTION_SENSOR_FACTORY_SENSOR_NOT_DEFINED_IN_SENSOR_INFO_FILE);
		}

		return sensor;
	}

	/**
	 * Create Remote Sensor Instance
	 * <p>
	 * Create a remote sensor instance regarding the JSON string from the sensor
	 * info file.
	 * </p>
	 * 
	 * @param jsonString
	 *            the JSON string that defines the remote sensor.
	 * @param sensorMessageHandler
	 *            the sensor message handler.
	 * @return the remote sensor created from the JSON string.
	 * @throws JSONException
	 * @throws IOException
	 * @throws SensorFactoryException
	 */
	private RemoteSensor createRemoteSensor(String jsonString,
			SensorMessageHandler sensorMessageHandler) throws JSONException,
			IOException, SensorFactoryException {
		JSONObject jsonObject = new JSONObject(jsonString);

		String sensorName = jsonObject.getString(SENSOR_NAME_TAG);
		String sensorChannel = jsonObject.getString(SENSOR_CHANNEL_TAG);
		ArrayList<String> messageList = new ArrayList<String>();

		// Initialize sensor message array
		JSONArray messageArray = jsonObject.getJSONArray(SENSOR_MESSAGES_TAG);

		for (int i = 0; i < messageArray.length(); ++i) {
			String message = messageArray.getString(i);
			messageList.add(message);
		}

		String[] messages = messageList.toArray(new String[messageList.size()]);
		return new RemoteSensor(sensorName, sensorChannel, messages, hostName,
				sensorMessageHandler);

	}

	/**
	 * Create Local Sensor Instance
	 * <p>
	 * Create a local sensor instance regarding the JSON string from the sensor
	 * info file.
	 * </p>
	 * 
	 * @param jsonString
	 *            the JSON string that defines the local sensor.
	 * @param sensorMessageHandler
	 *            the sensor message handler.
	 * @return the local sensor created from the JSON string.
	 * @throws JSONException
	 * @throws IOException
	 * @throws SensorFactoryException
	 */
	private LocalSensor createLocalSensor(String jsonString,
			SensorMessageHandler sensorMessageHandler) throws JSONException,
			IOException, SensorFactoryException {
		JSONObject jsonObject = new JSONObject(jsonString);

		String sensorName = jsonObject.getString(SENSOR_NAME_TAG);
		String sensorModulePath = jsonObject.getString(SENSOR_MODULE_PATH_TAG);
		ArrayList<String> messageList = new ArrayList<String>();

		File sensorModule = new File(sensorModulePath);

		// Local sensor executable not found
		if (!sensorModule.exists() || sensorModule.isDirectory()) {
			throw new SensorFactoryException(
					SensorFactoryException.EXCEPTION_SENSOR_FACTORY_LOCAL_SENSOR_MODULE_NOT_FOUND);
		}

		// Initialize sensor message array
		JSONArray messageArray = jsonObject.getJSONArray(SENSOR_MESSAGES_TAG);

		for (int i = 0; i < messageArray.length(); ++i) {
			String message = messageArray.getString(i);
			messageList.add(message);
		}

		String[] messages = messageList.toArray(new String[messageList.size()]);
		return new LocalSensor(sensorName, sensorModulePath, messages,
				sensorMessageHandler);
	}

	/**
	 * Memory Toolkit Sensor Factory Exception
	 * <p>
	 * This class represents a recoverable error within the work flow of the
	 * {@link Sensor}
	 * </p>
	 * 
	 * @author Xinda Zeng <xinda@umich.edu>
	 * @version 1.1 10/20/2015
	 */
	public class SensorFactoryException extends Exception {

		private static final long serialVersionUID = 7999822022905706163L;

		/**
		 * @see {@link SensorFactory#SensorFactory(String, String)}
		 */
		public static final String EXCEPTION_SENSOR_FACTORY_REDIS_HOST_NAME_NOT_DEFINED = "The host name must be specified for remote/off-board sensors.";

		/**
		 * @see {@link SensorFactory#SensorFactory(String, String)}
		 */
		public static final String EXCEPTION_SENSOR_FACTORY_SENSOR_INFO_FILE_NOT_DEFINED = "The sensor info file that specifies the sensor events and messages is not defined.";

		/**
		 * @see {@link SensorFactory#createSensor(String)}
		 */
		public static final String EXCEPTION_SENSOR_FACTORY_ERROR_READING_SENSOR_INFO_FILE = "The JSON file that specifies the sensor events and messages is not defined.";

		/**
		 * @see {@link SensorFactory#createSensor(String)}
		 */
		public static final String EXCEPTION_SENSOR_FACTORY_SENSOR_NOT_DEFINED_IN_SENSOR_INFO_FILE = "The sensor is not defined in the JSON file.";

		/**
		 * @see {@link SensorFactory#createLocalSensor(String)}
		 */
		public static final String EXCEPTION_SENSOR_FACTORY_LOCAL_SENSOR_MODULE_NOT_FOUND = "The executable of the local sensor is not found.";

		/**
		 * Default Constructor
		 * 
		 * @param message
		 *            object of type {@link String} that contains the error
		 *            details
		 */
		public SensorFactoryException(String message) {
			super(message);
		}
	}

}
