package mtk.test;

import java.io.IOException;

import org.json.JSONException;

import mtk.app.MemoryApp;
import mtk.ctxengine.ContextEngine.ContextEngineException;
import mtk.ctxengine.sensors.SensorFactory.SensorFactoryException;
import mtk.ctxengine.sensors.Sensor;
import mtk.ctxengine.sensors.SensorMessageHandler;

/**
 * Story Ball
 * <p>
 * This is a prototype application designed to demonstrate how to build memory
 * applications with the memory toolkit.
 * </p>
 * 
 * @author Xinda Zeng <xinda@umich.edu>
 * @version 1.1 10/20/2015
 */

public class StoryBall extends MemoryApp implements SensorMessageHandler {

	private static StoryBallContextEngine contextEngine;

	@Override
	protected void onCreate() {
		// Initialize local variables, i.e., contextEngine in this case.
		contextEngine = new StoryBallContextEngine("localhost",
				"sensor_info.json");
	}

	@Override
	protected void onStart() {
		// Start the IMU sensor defined in sensor_info.json
		try {
			contextEngine.startSensor("IMU", this);
		} catch (JSONException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (SensorFactoryException e) {
			e.printStackTrace();
		} catch (ContextEngineException e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void onStop() {
		// Terminate all sub-services
		try {
			contextEngine.stopSensor("IMU");
		} catch (ContextEngineException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		StoryBall sb = new StoryBall();
	}

	/**
	 * On Sensor Message Received
	 * <p>
	 * This method is a callback for the upcoming sensor message. Generally,
	 * this handler should be implemented within the context engine to process
	 * all sensor messages.
	 * </p>
	 * 
	 * @param sensor
	 *            the type of {@link Sensor} which received this message.
	 * @param message
	 *            primitive type of string that specifies the message content.
	 * 
	 * @see {@link SensorMessageHandler#onSensorMessageReceived(Sensor, String)}
	 */
	@Override
	public void onSensorMessageReceived(Sensor sensor, String message) {
		// If the message comes from IMU
		if (sensor.getSensorName().compareToIgnoreCase("IMU") == 0) {
			// If it is a shaken event
			if (message.compareToIgnoreCase("shake") == 0) {
				// Play some audio clips
				System.out.println("Play audio clips");
			}
		}

		// If multiple sensors are activated in this application, add more event
		// handlers here.
	}
}
