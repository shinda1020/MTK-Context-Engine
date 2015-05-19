package mtk.app;

import mtk.ctxengine.ContextEngine;
import mtk.ctxengine.sensors.ICtxUpdated;

/**
 * This class is the application prototype.
 * <p>
 * To build applications on top of this class, firstly create a class that
 * extends the MemoryApp class. Then, for the interested context types, override
 * the event methods defined in ICtxUpdated interface.
 * 
 * @author Shinda
 * @version 1.0 05/15/2015
 */

public abstract class MemoryApp implements ICtxUpdated {

	/* The context engine for this app */
	protected ContextEngine ctxEngine;

	/**
	 * Constructor
	 * 
	 */
	public MemoryApp() {

	}

	/**
	 * The start method for applications. Must be override! And the first line
	 * of the overridden method must start with the line "super.start()".
	 * <p>
	 * The super methods simply instantiates its own context engine.
	 */
	public void start() {
		ctxEngine = new ContextEngine(getHostName(), getMethodFile(), this);
	}

	/******************************************************************
	 * Abstract methods
	 ******************************************************************/

	/**
	 * This abstract defines the host name of the Redis server.
	 */
	protected abstract String getHostName();

	/**
	 * This abstract defines the path of the methods.json file.
	 */
	protected abstract String getMethodFile();

	/******************************************************************
	 * ICtxUpdated implementation
	 ******************************************************************/

	/**
	 * Below are all the methods declared in the ICtxUpdated interface. Right
	 * now this is the naive implementation of these methods, which does
	 * nothing.
	 * <p>
	 * To build an application, create a new class that extends to this class,
	 * i.e., MemoryApp. Then simply override the context events that are of
	 * interest, such as IMU events for StoryBall.
	 */

	@Override
	public void OnBoardIMUShakeDetected() {

	}

	@Override
	public void OnBoardCameraFaceDetected() {

	}

	@Override
	public void OffBoardActivityNoneDetected() {

	}

	@Override
	public void OffBoardActivityLowDetected() {

	}

	@Override
	public void OffBoardActivityHighDetected() {

	}

	@Override
	public void OffBoardSpeechNoneDetected() {

	}

	@Override
	public void OffBoardSpeechLowDetected() {

	}

	@Override
	public void OffBoardSpeechHighDetected() {

	}

}
