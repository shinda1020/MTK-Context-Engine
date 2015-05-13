package com.ctxengine.sensors;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.JedisPubSub;

/**
 * This class is an abstract class of all off-board sensor services.
 * <p>
 * Off-board sensor services are activated through network, precisely Redis in
 * this case. Unlike on-board sensors, each sensor module can be started
 * seperately to run sensor service, pub/sub to application modules, etc.
 * <p>
 * 
 * @author Shinda
 * @version 1.0 05/12/2015
 */

public abstract class OffBoardSensor extends JedisPubSub {

	/* The private sensor thread instance */
	private SensorThread sensorThread;

	/*
	 * The number of applications that subscribes to this sensor module. Unlike
	 * on-board sensors, which can only be started by one application at a time,
	 * the off-board sensors may serve multiple apps at the same time. Hence,
	 * once an app is trying to terminate the sensor service, it should count
	 * the current total number of apps that subscribe to itself. If curAppNum
	 * equals 0, then stop the sensor. Otherwise leave it alone.
	 */
	private int curAppNum = 0;

	/* The Jedis pool instance */
	private static JedisPool pool;

	/* The Jedis instance */
	private Jedis jedis;

	/**
	 * This constructor initiates the connection from the sensor module to the
	 * Redis server.
	 * 
	 * @param hostName
	 *            The host name of the Redis server that this sensor module
	 *            connects to.
	 */
	public OffBoardSensor(String hostName) {
		// Initiate the connection.
		pool = new JedisPool(new JedisPoolConfig(), hostName);
		Jedis jedis = pool.getResource();

		// Subscribe to the corresponding channel.
		jedis.subscribe(this, this.getSensorChannelFromRedis());

	}

	/**
	 * This abstract function defines the behaviors after certain sensor
	 * messages are received.
	 * 
	 * @param msg
	 *            The sensor message read from stdout.
	 */
	protected abstract void handleSensorMsg(String msg);

	/**
	 * This abstract function defines where to run the sensor module.
	 * 
	 * @return The path where the sensor executable is stored.
	 */
	protected abstract String getSensorModulePath();

	/**
	 * This abstract function defines the channel that this sensor subscribes
	 * to, e.g., ACTIVITY_CH.
	 * 
	 * @return the channel that this sensor subscribes.
	 */
	protected abstract String getSensorChannelFromRedis();

	/**
	 * This abstract function defines the name of the sensor thread.
	 * 
	 * @return The name of the sensor thread.
	 */
	protected abstract String getSensorThreadName();

	/**
	 * This function starts the sensor module from predefined module path.
	 */
	public void startSensor() {
		sensorThread = new SensorThread(this.getSensorThreadName(),
				this.getSensorModulePath());
		sensorThread.start();
	}

	/**
	 * This function stops the sensor module service.
	 */
	public void stopSensor() {
		this.sensorThread.stop();
	}

	/**
	 * This function overrides the original abstract function from JedisPubSub
	 * class.
	 * <p>
	 * Basically, what this function does is to handle all commands from the
	 * channel to which this sensor service subscribes.
	 * <p>
	 * The two possible commands are:
	 * <p>
	 * start : to start the sensor module.
	 * <p>
	 * stop : to stop the sensor module.
	 * 
	 * @param channel
	 *            The Redis channel from which the command comes.
	 * @param command
	 *            The command from the aforementioned Redis channel.
	 */
	@Override
	public void onMessage(String channel, String command) {
		// Make sure the command comes from the channel that this sensor
		// subscribes to.
		if (channel.compareToIgnoreCase(this.getSensorChannelFromRedis()) == 0) {
			// The command to start the sensor module
			if (command.compareToIgnoreCase("start") == 0) {
				// Check if the sensor has already been started by counting the
				// number of apps that subscribe to this sensor service.
				if (curAppNum == 0) {
					this.startSensor();
				}
				// Don't forget to increase the curAppNum since a new app
				// subscribes to this sensor service.
				curAppNum++;
			}
			// The command to stop the sensor module
			else if (command.compareToIgnoreCase("stop") == 0) {
				// Don't forget to decrease the curAppNum since an app
				// unsubscribes from this sensor service.
				curAppNum = Math.max(0, curAppNum - 1); // Make sure this number
														// is not negative.

				// Check if there are still apps subscribing to this sensor
				// service. If none, terminate the service.
				if (curAppNum == 0) {
					this.stopSensor();
				}
			}
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

	/**
	 * This inner class is designed to start sensor services as threads to stop
	 * blocking any processing on context engine.
	 * 
	 * @author shinda
	 * @version 1.0 05/10/2015
	 */
	class SensorThread implements Runnable {

		/* The thread instance */
		private Thread t;

		/* The thread name */
		private String threadName;

		/* The path of sensor module executable */
		private String runnablePath;

		/* The process instance that runs the sensor module executable */
		private Process pr;

		/* The boolean sign to control the process */
		private boolean isInterrupted = false;

		/**
		 * The constructor
		 * 
		 * @param name
		 *            The thread name
		 * @param path
		 *            The path of sensor module executable
		 */
		SensorThread(String name, String path) {
			threadName = name;
			runnablePath = path;
		}

		/**
		 * The thread function. To start the sensor thread, this function
		 * execute the sensor module executable from command line. If failed to
		 * execute the binary, this module terminates the program.
		 * <p>
		 * Then the thread reads the output from stdout (Hence, when
		 * implementing the sensor modules, use fflush(stdout) to avoid using
		 * buffer), and then pass to the OnBoardSensor class to handle the
		 * output. This is the first version of the sensor services. In the
		 * future, we aim to integrate sensor services written in other
		 * languages with Java via JNI, such as C/C++.
		 */
		public void run() {

			/*
			 * Start executing sensor binary from command line. Terminate the
			 * program when failed.
			 */
			try {
				pr = Runtime.getRuntime().exec(runnablePath);
			} catch (IOException e) {
				e.printStackTrace();
				System.exit(1);
			}

			/*
			 * Set up the stdout reader
			 */
			InputStream stdin = pr.getInputStream();
			InputStreamReader isr = new InputStreamReader(stdin);
			BufferedReader br = new BufferedReader(isr);

			/*
			 * Read the output from the stdout. When a line is captured, pass to
			 * the handleSensorOutput(String) function in the OnBoardSensor
			 * class to handle.
			 */
			String line;
			try {
				while ((line = br.readLine()) != null && !isInterrupted) {
					handleSensorMsg(line);
				}
			} catch (IOException e) {
				e.printStackTrace();
				System.exit(1);
			}
		}

		/**
		 * This function starts the sensor thread.
		 */
		public void start() {
			isInterrupted = false;
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
			isInterrupted = true;
			if (t != null) {
				if (pr != null) {
					pr.destroy();
				}
				t = null;
			}
		}

	} /* SensorThread */

	// /**
	// * @param args
	// */
	// public static void main(String[] args) {
	// System.out.println("Running");
	// JedisPool pool = new JedisPool(new JedisPoolConfig(), "localhost");
	//
	// OffBoardSensor sensor = new OffBoardSensor();
	//
	// try {
	// Jedis jedis = pool.getResource();
	//
	// // / ... do stuff here ... for example
	// // jedis.set("xinda", "zeng");
	// // jedis.publish("IMU", "shake");
	// jedis.subscribe(sensor, "IMU");
	//
	// } finally {
	// // / ... when closing your application:
	// pool.destroy();
	// }
	// }
}
