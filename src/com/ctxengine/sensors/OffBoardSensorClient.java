/**
 * 
 */
package com.ctxengine.sensors;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.JedisPubSub;

/**
 * This class is an abstract class of all off-board sensor service wrappers that
 * run on board.
 * <p>
 * Basically, what this class does is to communicate with corresponding
 * off-broad sensor services through prescribed channels on Redis.
 * 
 * @author Shinda
 * @version 1.0 05/12/2015
 */

public abstract class OffBoardSensorClient {

	/*
	 * The private Redis thread instance that communicates with off-board
	 * sensors through Redis server
	 */
	private RedisThread redisThread;

	/* The Jedis pool instance */
	private static JedisPool pool;

	/* The Jedis instance */
	private Jedis jedis;

	/******************************************************************
	 * Abstract methods
	 ******************************************************************/

	/**
	 * This abstract function defines the name of the sensor thread.
	 * 
	 * @return The name of the sensor thread.
	 */
	protected abstract String getSensorThreadName();

	/**
	 * This abstract function defines the channel that this sensor subscribes
	 * to, e.g., ACTIVITY_CH.
	 * 
	 * @return the channel that this sensor subscribes.
	 */
	protected abstract String getSensorChannelFromRedis();

	/**
	 * This abstract function defines host name of the Redis server.
	 * 
	 * @return the host name of the Redis server
	 */
	protected abstract String getHostName();

	/**
	 * Override this function to handle off-board sensor messages.
	 * 
	 * @param msg
	 *            host name of the Redis server
	 */
	protected abstract void msgReceivedFromRedis(String msg);

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
			pool = new JedisPool(new JedisPoolConfig(), this.getHostName());
		}

		// Create a Jedis pub/sub instance
		jedis = pool.getResource();

		// Run the subscription as a thread
		redisThread = new RedisThread(this.getSensorThreadName());
		redisThread.start();
	}

	/**
	 * This function stops the sensor module service.
	 */
	public void stopSensor() {
		this.redisThread.stop();
		jedis.disconnect();
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

		/**
		 * The constructor
		 * 
		 * @param _threadName
		 *            The thread name
		 */
		RedisThread(String _threadName) {
			this.threadName = _threadName;
		}

		/**
		 * The thread function. This function simply subscribes to corresponding
		 * channel.
		 */
		public void run() {
			jedis.subscribe(this, getSensorChannelFromRedis());
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
			if (channel.compareToIgnoreCase(getSensorChannelFromRedis()) == 0) {
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
