package com.ctxengine.sensors;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;

import com.ctxengine.ContextEngine;
import com.ctxengine.sensors.offboard.IActivityCtxUpdated;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.JedisPubSub;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

;
/**
 * 
 * 
 * @author Shinda
 * @version 1.0 05/12/2015
 */

public class OffBoardSensorClient {

	// The host where Redis server is running
	private static String hostName;

	/*
	 * The private Redis thread instance that communicates with off-board
	 * sensors through Redis server
	 */
	private RedisThread redisThread;

	/* The Jedis pool instance */
	private static JedisPool pool;

	/* The Jedis instance */
	private Jedis jedis;

	/* The name of sensor service */
	private String sensorName;

	/* The channel of sensor service */
	private String sensorChannel;

	/* The method dictionary mapping to sensor messages */
	HashMap<String, String> methodDict = new HashMap<String, String>();

	/*
	 * The interface to which the off-board sensor client passes events for
	 * actual handling
	 */
	private IOffBoardCtxUpdated ctxInterface = null;

	/******************************************************************
	 * Constructor and Setters & Getters
	 ******************************************************************/

	public OffBoardSensorClient(String _sensorName,
			IOffBoardCtxUpdated _ctxInterface) {
		this.sensorName = _sensorName;
		this.ctxInterface = _ctxInterface;

		// Initialize this sensor client based on the sensor name given.
		try {
			this.initialize();
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	public String getSensorName() {
		return sensorName;
	}

	public void setSensorName(String sensorName) {
		this.sensorName = sensorName;
	}

	public HashMap<String, String> getMethodDict() {
		return methodDict;
	}

	public void setMethodDict(HashMap<String, String> methodDict) {
		this.methodDict = methodDict;
	}

	public IOffBoardCtxUpdated getCtxInterface() {
		return ctxInterface;
	}

	public void setCtxInterface(IOffBoardCtxUpdated ctxInterface) {
		this.ctxInterface = ctxInterface;
	}

	public String getSensorChannel() {
		return sensorChannel;
	}

	public void setSensorChannel(String sensorChannel) {
		this.sensorChannel = sensorChannel;
	}

	public static String getHostName() {
		return hostName;
	}

	public static void setHostName(String hostName) {
		OffBoardSensorClient.hostName = hostName;
	}

	/******************************************************************
	 * Global implementation of sensor services (No need to change for
	 * individual sensors)
	 ******************************************************************/

	/**
	 * This function starts the sensor module from predefined module path.
	 */
	public void startSensor() {

		// Create a Jedis pool if not exist
		if (pool == null) {
			pool = new JedisPool(new JedisPoolConfig(), this.hostName);
		}

		// Create a Jedis pub/sub instance
		jedis = pool.getResource();

		// Run the subscription as a thread
		redisThread = new RedisThread(this.getSensorName(),
				this.getSensorChannel());
		redisThread.start();
	}

	/**
	 * This function stops the sensor module service.
	 */
	public void stopSensor() {
		this.redisThread.stop();
		jedis.disconnect();
	}

	private void initialize() throws JSONException {

		String jsonString = "{\"sensorName\":\"Activity\", \"sensorCh\":\"ACTIVITY_CH\", "
				+ "\"methods\":["
				+ "{\"message\":\"ActivityLow\", \"method\":\"OffBoardActivityLowDetected\"}, "
				+ "{\"message\":\"ActivityHigh\", \"method\":\"OffBoardActivityHighDetected\"}"
				+ "]}";
		System.out.println(jsonString);
		JSONObject jsonObject = new JSONObject(jsonString);

		String _sensorName = jsonObject.getString("sensorName");

		if (_sensorName.compareToIgnoreCase(this.sensorName) == 0) {
			// Initialize sensor channel
			this.sensorChannel = jsonObject.getString("sensorCh");

			// Initialize sensor method dictionary
			JSONArray methodArr = jsonObject.getJSONArray("methods");

			for (int i = 0; i < methodArr.length(); ++i) {
				JSONObject tempObj = methodArr.getJSONObject(i);
				String message = tempObj.getString("message");
				String methodName = tempObj.getString("method");

				this.methodDict.put(message, methodName);
			}
		}

		System.out.println(this.methodDict);

	}

	/**
	 * Override this function to handle off-board sensor messages.
	 * 
	 * @param msg
	 *            host name of the Redis server
	 */
	protected void msgReceivedFromRedis(String msg) {

		String methodName = methodDict.get(msg);

		if (methodName == null) {
			return;
		}

		try {
			Method method = ctxInterface.getClass().getMethod(methodName,
					new Class[] {});

			if (method != null) {
				method.invoke(ctxInterface, new Object[] {});
			}

		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Subscription on Redis blocks the main thread. Hence, this inner class is
	 * designed to start subscription as threads to stop blocking any processing
	 * on context engine.
	 * 
	 * @author shinda
	 * @version 1.0 05/10/2015
	 */
	class RedisThread extends JedisPubSub implements Runnable {

		/* The thread instance */
		private Thread t;

		/* The thread name */
		private String threadName;

		/* The redis channel */
		private String redisChannel;

		/**
		 * The constructor
		 * 
		 * @param _threadName
		 *            The thread name
		 */
		RedisThread(String _threadName, String _redisChannel) {
			this.threadName = _threadName;
			this.redisChannel = _redisChannel;
		}

		/**
		 * The thread function. This function simply subscribes to corresponding
		 * channel.
		 */
		public void run() {
			jedis.subscribe(this, redisChannel);
		}

		/**
		 * This function starts the sensor thread.
		 */
		public void start() {
			if (t == null) {
				t = new Thread(this, threadName);
				t.setDaemon(false);
				t.start();
			}
		}

		/**
		 * This function stops the sensor thread and kills the sensor process.
		 */
		public void stop() {
			if (t != null) {
				this.unsubscribe();
				t.interrupt();
				t = null;
			}
		}

		/******************************************************************
		 * Implementation of abstract methods
		 ******************************************************************/

		@Override
		public void onMessage(String channel, String msg) {
			// Make sure the command comes from the channel that this sensor
			// subscribes to.
			if (channel.compareToIgnoreCase(this.redisChannel) == 0) {
				msgReceivedFromRedis(msg);
			}
		}

		@Override
		public void onPMessage(String arg0, String arg1, String arg2) {

		}

		@Override
		public void onPSubscribe(String arg0, int arg1) {

		}

		@Override
		public void onPUnsubscribe(String arg0, int arg1) {

		}

		@Override
		public void onSubscribe(String arg0, int arg1) {

		}

		@Override
		public void onUnsubscribe(String arg0, int arg1) {

		}
	} /* RedisThread */

}
