package mtk.ctxengine.sensors;

/**
 * Memory Toolkit Local/On-board Sensor
 * <p>
 * This class is an abstract class of all local/on-board sensor services.
 * </p>
 * <p>
 * Unlike remote/off-board sensor services, the local/on-board ones are started
 * via running executable sensor modules, whereas the remote/off-board ones are
 * virtual wrappers of Redis threads that read sensor events and messages coming
 * from sensors in the wild.
 * </p>
 * 
 * @author Xinda Zeng <xinda@umich.edu>
 * @version 1.1 10/20/2015
 */

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class LocalSensor extends Sensor {

	/* The name of sensor service */
	private String sensorName;

	/* The messages/sensor events of the particular sensor */
	private String[] messages;

	/* The path where the sensor module executable is stored */
	private String sensorModulePath;

	/* The flag that specifies the running state of the sensor module */
	private boolean isRunning = false;

	/* The private sensor thread instance */
	private SensorThread sensorThread;

	/*
	 * The interface to which the local sensor client passes events for actual
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
	 * @param sensorModulePath
	 *            the path where the sensor module executable is stored.
	 * @param messages
	 *            the messages/sensor events of the particular sensor.
	 */
	public LocalSensor(String sensorName, String sensorModulePath,
			String[] messages, SensorMessageHandler sensorMessageHandler) {
		this.sensorName = sensorName;
		this.sensorModulePath = sensorModulePath;
		this.messages = messages;
		this.sensorMessageHandler = sensorMessageHandler;
	}

	@Override
	public String getSensorName() {
		return sensorName;
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

	/**
	 * Message Received From Standard Output
	 * <p>
	 * This method handles the output in the standard output.
	 * </p>
	 * 
	 * @param message
	 *            the message received from standard output.
	 */
	protected void msgReceivedFromStdOutput(String message) {

		for (int i = 0; i < messages.length; ++i) {
			if (message.compareToIgnoreCase(messages[i]) == 0) {
				sensorMessageHandler.onSensorMessageReceived(this, message);
				return;
			}
		}
	}

	/******************************************************************
	 * Global implementation of sensor services (No need to change for
	 * individual sensors)
	 ******************************************************************/

	/**
	 * This function starts the sensor module from predefined module path.
	 */
	@Override
	public void start() {
		if (!isRunning) {
			sensorThread = new SensorThread(sensorName, sensorModulePath);
			sensorThread.start();
			isRunning = true;
		}
	}

	/**
	 * This function stops the sensor module service.
	 */
	@Override
	public void stop() {
		this.sensorThread.stop();
		isRunning = false;
	}

	/******************************************************************
	 * Inner class
	 ******************************************************************/

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
					msgReceivedFromStdOutput(line);
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

} /* OnBoardSensor */
