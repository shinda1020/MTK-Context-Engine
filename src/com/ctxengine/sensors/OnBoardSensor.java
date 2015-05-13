package com.ctxengine.sensors;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * This class is an abstract class of all on-board sensor services.
 * <p>
 * Unlike off-board sensor services, the on-board ones are called and started
 * from the context engine directly, whereas the off-board ones are activated
 * through network.
 * <p>
 * 
 * @author Shinda
 * @version 1.0 05/10/2015
 */

public abstract class OnBoardSensor {

	/* The private sensor thread instance */
	private SensorThread sensorThread;

	/* The flag that specifies the running state of the sensor module */
	private boolean isRunning = false;

	/******************************************************************
	 * Abstract methods
	 ******************************************************************/

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
	 * This abstract function defines the name of the sensor thread.
	 * 
	 * @return The name of the sensor thread.
	 */
	protected abstract String getSensorThreadName();

	/******************************************************************
	 * Global implementation of sensor services (No need to change for
	 * individual sensors)
	 ******************************************************************/

	/**
	 * This function starts the sensor module from predefined module path.
	 */
	public void startSensor() {
		if (!isRunning) {
			sensorThread = new SensorThread(this.getSensorThreadName(),
					this.getSensorModulePath());
			sensorThread.start();
			isRunning = true;
		}
	}

	/**
	 * This function stops the sensor module service.
	 */
	public void stopSensor() {
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

} /* OnBoardSensor */
