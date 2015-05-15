/**
 * 
 */
package com.ctxengine.sensors.offboard;

import com.ctxengine.sensors.IOffBoardCtxUpdated;
import com.ctxengine.sensors.OffBoardSensorClient_Backup;

/**
 * @author shinda
 * 
 */
public class ActivityClient extends OffBoardSensorClient_Backup implements
		IActivityCtxUpdated {

	public ActivityClient(String _hostName, IOffBoardCtxUpdated _ctxInterface) {
		super(_hostName, _ctxInterface);
		this.hostName = _hostName;
		// TODO Auto-generated constructor stub
	}

	/* The name of the sensor thread */
	final private String SENSOR_THREAD_NAME = "Activity";

	/* The name of the sensor channel on Redis */
	final private String REDIS_SENSOR_CH = "ACTIVITY_CH";

	/* The host name of Redis server */
	private String hostName;

	/*
	 * The interface to which this sensor module passes events for actual
	 * handling
	 */
	private IActivityCtxUpdated ctxInterface = null;

	/**
	 * 
	 */
//	public ActivityClient(String _hostName, IActivityCtxUpdated _ctxInterface) {
//		this.hostName = _hostName;
//		this.ctxInterface = _ctxInterface;
//	}

	/******************************************************************
	 * Implementation of abstract methods
	 ******************************************************************/

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
	 * This function defines the channel that this sensor subscribes to, in this
	 * case, ACTIVITY_CH.
	 * 
	 * @return the channel that this sensor subscribes.
	 */
	@Override
	protected String getSensorChannelFromRedis() {
		return this.REDIS_SENSOR_CH;
	}

	/**
	 * This function defines the host name of the Redis server.
	 * 
	 * @return the host name of the Redis server.
	 */
	@Override
	protected String getHostName() {
		return this.hostName;
	}

	/**
	 * Override abstract function from parent class to update context.
	 * <p>
	 * None : no activity
	 * <p>
	 * Low : low activity
	 * <p>
	 * High : high activity
	 * 
	 * @param msg
	 *            The sensor message read from stdout.
	 */
	@Override
	protected void msgReceivedFromRedis(String msg) {
		// No activity detected
		if (msg.compareToIgnoreCase("none") == 0) {
			this.OffBoardActivityNoneDetected();
		}
		// Low activity detected
		else if (msg.compareToIgnoreCase("low") == 0) {
			this.OffBoardActivityLowDetected();
		}
		// High activity detected
		else if (msg.compareToIgnoreCase("high") == 0) {
			this.OffBoardActivityHighDetected();
		}

	}

	/******************************************************************
	 * IIMUCtxUpdated interface
	 ******************************************************************/

	/**
	 * The following methods simply pass the event to the context engine for
	 * actual handling.
	 */

	@Override
	public void OffBoardActivityNoneDetected() {
		ctxInterface.OffBoardActivityNoneDetected();
	}

	@Override
	public void OffBoardActivityLowDetected() {
		ctxInterface.OffBoardActivityLowDetected();
	}

	@Override
	public void OffBoardActivityHighDetected() {
		ctxInterface.OffBoardActivityHighDetected();
	}

}
