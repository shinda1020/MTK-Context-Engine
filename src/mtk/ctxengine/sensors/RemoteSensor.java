package mtk.ctxengine.sensors;

/**
 * Memory Toolkit Remote/Off-board Sensor
 * <p/>
 * <p>This class is an off-board sensor service wrappers that run on board.</p>
 * <p/>
 * <p>Basically, what this class does is to communicate with corresponding
 * off-broad sensor services through prescribed channels on Redis.</p>
 * <p/>
 * <p>For each off-board sensor service, the app developer needs to create a new
 * instance via giving the name of the sensor module. There is no need to write
 * individual wrappers for every sensor, since the callback mechanism is
 * implemented with Java Reflection.</p>
 * 
 * @author Xinda Zeng <xinda@umich.edu>
 * @version 1.1 10/20/2015
 */

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.JedisPubSub;

public class RemoteSensor extends Sensor {

	/* The name of sensor service */
	private String sensorName;

	/* The host where Redis server is running */
	private String hostName;

	/*
	 * The private Redis thread instance that communicates with remote/off-board
	 * sensors through Redis server
	 */
	private RedisThread redisThread;

	/* The Jedis pool instance */
	private static JedisPool pool;

	/* The Jedis instance */
	private Jedis jedis;

	/* The channel of sensor service */
	private String sensorChannel;

	/* The messages/sensor events of the particular sensor */
	private String[] messages;

	/*
	 * The initialization state of the client module. If not successful, a
	 * possible explanation is that no such sensor module is defined in the
	 * methods.json file.
	 */
	private boolean initialized = false;

	/*
	 * The interface to which the remote sensor client passes events for actual
	 * handling
	 */
	private SensorMessageHandler sensorMessageHandler = null;

	/******************************************************************
	 * Constructor, Setters & Getters
	 ******************************************************************/

	/**
	 * The constructor.
	 * 
	 * @param sensorName
	 *            the name of the sensor service.
	 * @param sensorChannel
	 *            the Redis channel that this sensor listens to.
	 * @param messages
	 *            the messages/sensor events of the particular sensor.
	 * @param hostName
	 *            the host where Redis server is running
	 */
	public RemoteSensor(String sensorName, String sensorChannel,
			String[] messages, String hostName,
			SensorMessageHandler sensorMessageHandler) {
		this.sensorName = sensorName;
		this.sensorChannel = sensorChannel;
		this.messages = messages;
		this.hostName = hostName;
		this.sensorMessageHandler = sensorMessageHandler;
	}

	@Override
	public String getSensorName() {
		return sensorName;
	}

	public void setSensorName(String sensorName) {
		this.sensorName = sensorName;
	}

	public String[] getMessages() {
		return messages;
	}

	public void setMessages(String[] messages) {
		this.messages = messages;
	}

	public SensorMessageHandler getSensorMessageHandler() {
		return sensorMessageHandler;
	}

	public void setSensorMessageHandler(
			SensorMessageHandler sensorMessageHandler) {
		this.sensorMessageHandler = sensorMessageHandler;
	}

	public String getSensorChannel() {
		return sensorChannel;
	}

	public void setSensorChannel(String sensorChannel) {
		this.sensorChannel = sensorChannel;
	}

	public String getHostName() {
		return hostName;
	}

	public void setHostName(String _hostName) {
		this.hostName = _hostName;
	}

	public boolean isInitialized() {
		return initialized;
	}

	/******************************************************************
	 * Global implementation of sensor services (No need to change for
	 * individual sensors)
	 ******************************************************************/

	/**
	 * This function starts the sensor module by listening to specified channel
	 * on Redis.
	 * 
	 * @throws SensorException
	 */
	@Override
	public void start() {

		// Create a Jedis pool if not exist
		if (pool == null) {
			pool = new JedisPool(new JedisPoolConfig(), hostName);
		}

		// Create a Jedis pub/sub instance
		jedis = pool.getResource();

		// Run the subscription as a thread
		if (redisThread == null) {
			redisThread = new RedisThread(this.sensorName, this.sensorChannel);
			redisThread.start();
		}
	}

	/**
	 * This function stops the sensor module service.
	 * 
	 * @throws SensorException
	 */
	@Override
	public void stop() {
		redisThread.stop();
		jedis.disconnect();
		redisThread = null;
	}

	/**
	 * Message Received From Redis
	 * <p>
	 * This method handles the messages received from subscribed Redis channel.
	 * </p>
	 * 
	 * @param message
	 *            the message received from corresponding Redis channel.
	 */
	protected void msgReceivedFromRedis(String message) {

		for (int i = 0; i < messages.length; ++i) {
			if (message.compareToIgnoreCase(messages[i]) == 0) {
				sensorMessageHandler.onSensorMessageReceived(this, message);
				return;
			}
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
