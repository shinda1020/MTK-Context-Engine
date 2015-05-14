package com.ctxengine.sensors.offboard;

import com.ctxengine.sensors.OffBoardSensor;

/**
 * This class is a camera sensor class inherited from OnBoardSensor.
 * <p>
 * In particular, this sensor class handles face detected event.
 * <p>
 * 
 * @author Shinda
 * @version 1.0 05/10/2015
 */
public final class Activity extends OffBoardSensor {

	/* The name of the sensor thread */
	final private String SENSOR_THREAD_NAME = "Activity";

	/* The path where the sensor module executable is stored */
	final private String SENSOR_MODULE_PATH = "sensorbin/activity";

	/* The name of the sensor channel on Redis */
	final private String REDIS_SENSOR_CH = "ACTIVITY_CH";

	/**
	 * This constructor initiates the connection from the sensor module to the
	 * Redis server.
	 * 
	 * @param hostName
	 *            The host name of the Redis server that this sensor module
	 *            connects to.
	 */
	public Activity(String hostName) {
		super(hostName);
	}

	/**
	 * This function handles the sensor message read from stdout.
	 * 
	 * @param msg
	 *            The sensor message read from stdout.
	 */
	@Override
	protected void handleSensorMsg(String msg) {
		System.out.println(msg);
		// No activity detected
		if (msg.compareToIgnoreCase("none") == 0) {
			this.pubContext("none");
		}
		// Low activity detected
		else if (msg.compareToIgnoreCase("low") == 0) {
			this.pubContext("low");
		}
		// High activity detected
		else if (msg.compareToIgnoreCase("high") == 0) {
			this.pubContext("high");
		}
	}

	/**
	 * This function defines the name of the sensor thread.
	 * 
	 * @return The name of the sensor thread.
	 */
	@Override
	protected String getSensorThreadName() {
		return this.SENSOR_THREAD_NAME;
	}

	/**
	 * This function defines where to run the sensor module.
	 * 
	 * @return The path where the sensor executable is stored.
	 */
	@Override
	protected String getSensorModulePath() {
		return this.SENSOR_MODULE_PATH;
	}

	/**
	 * This function defines the channel that this sensor subscribes
	 * to, in this case, ACTIVITY_CH.
	 * 
	 * @return the channel that this sensor subscribes.
	 */
	@Override
	protected String getSensorChannelFromRedis() {
		return this.REDIS_SENSOR_CH;
	}

}
