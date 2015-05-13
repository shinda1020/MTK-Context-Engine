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
public class Activity extends OffBoardSensor {

	/* The name of the sensor thread */
	final private String SENSOR_THREAD_NAME = "Activity";

	/* The path where the sensor module executable is stored */
	final private String SENSOR_MODULE_PATH = "sensorbin/cam";

	/* The name of the sensor channel on Redis */
	final private String REDIS_SENSOR_CH = "ACTIVITY_CH";

	public Activity(String hostName) {
		super(hostName);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.ctxengine.sensors.OffBoardSensor#handleSensorMsg(java.lang.String)
	 */
	@Override
	protected void handleSensorMsg(String msg) {
		// TODO Auto-generated method stub
		System.out.println(msg);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ctxengine.sensors.OffBoardSensor#getSensorThreadName()
	 */
	@Override
	protected String getSensorThreadName() {
		return this.SENSOR_THREAD_NAME;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ctxengine.sensors.OffBoardSensor#getSensorModulePath()
	 */
	@Override
	protected String getSensorModulePath() {
		return this.SENSOR_MODULE_PATH;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ctxengine.sensors.OffBoardSensor#getSensorChannelFromRedis()
	 */
	@Override
	protected String getSensorChannelFromRedis() {
		return this.REDIS_SENSOR_CH;
	}

}
