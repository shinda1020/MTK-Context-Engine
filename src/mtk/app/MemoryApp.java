package mtk.app;

import mtk.ctxengine.ContextEngine;
import mtk.ctxengine.sensors.SensorMessageHandler;

/**
 * Memory Toolkit Memory Application
 * <p>
 * To build applications on top of this class, firstly create a class that
 * extends the MemoryApp class. Then, for the interested context types, override
 * the event methods defined in ICtxUpdated interface.
 * 
 * @author Xinda Zeng <xinda@umich.edu>
 * @version 1.1 10/20/2015
 */

public abstract class MemoryApp {

	/**
	 * Constructor
	 * 
	 */
	public MemoryApp() {
		onCreate();
		onStart();
	}

	/**
	 * Stop Application
	 * <p>
	 * This method terminates this application. Also, this method is declared as
	 * final such that all termination handling should be placed in
	 * {@link MemoryApp#onStop()}.
	 */
	public final void stopApplication() {
		onStop();
	}

	/******************************************************************
	 * Application life-cycle abstract methods
	 ******************************************************************/

	/**
	 * On Create
	 * <p>
	 * The abstract method that defines the behavior of application being
	 * created. Initialization of application variables should be placed here.
	 * </p>
	 */
	protected abstract void onCreate();

	/**
	 * On Start
	 * <p>
	 * The abstract method that defines the behavior of application being
	 * started. Main logic of the application should be placed here.
	 * </p>
	 */
	protected abstract void onStart();

	/**
	 * On Stop
	 * <p>
	 * The abstract method that defines the {@link MemoryApp#stopApplication()}
	 * being called. All termination of sub-services, such as network, sensor,
	 * etc., should be placed here.
	 * </p>
	 */
	protected abstract void onStop();

}
