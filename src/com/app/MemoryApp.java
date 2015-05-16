package com.app;

import com.ctxengine.ContextEngine;
import com.ctxengine.sensors.ICtxUpdated;

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
	 * Constructor. This constructor simply instantiates its own context engine.
	 * 
	 */
	public MemoryApp() {
		ctxEngine = new ContextEngine(this);
	}

	/******************************************************************
	 * ICtxUpdated implementation
	 ******************************************************************/

	/**
	 * Below are all the methods declared in the ICtxUpdated interface. Right
	 * now this naive of these methods, which does nothing.
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
