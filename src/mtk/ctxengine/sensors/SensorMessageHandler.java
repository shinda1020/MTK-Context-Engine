package mtk.ctxengine.sensors;

/**
 * Memory Toolkit Sensor Message Handler
 * <p>
 * This interface serves as the delegate for handlers of all sensor messages,
 * for both local and remote sensors.
 * </p>
 * 
 * @author Xinda Zeng <xinda@umich.edu>
 * @version 1.1 10/20/2015
 */

public interface SensorMessageHandler {

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
	 */
	void onSensorMessageReceived(Sensor sensor, String message);
}
