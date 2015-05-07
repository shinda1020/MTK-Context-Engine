/**
 * 
 */
package com.contexts;

/**
 * @author shinda
 *
 */
public class IMU {

	/**
	 * 
	 */
	public IMU() {
		// TODO Auto-generated constructor stub
	}
	
	private native void updateContext();
	
    static {
        System.loadLibrary("IMU");
    }

}

// From src folder, run javah -jni com.contexts.IMU to generate com_contexts_IMU.h file
// Then write the source file and use the following command to compile
// g++ "-I/System/Library/Frameworks/JavaVM.framework/Versions/Current/Headers" 
// -c com_contexts_IMU.cpp
