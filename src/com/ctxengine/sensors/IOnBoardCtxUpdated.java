package com.ctxengine.sensors;

import com.ctxengine.sensors.onboard.ICameraCtxUpdated;
import com.ctxengine.sensors.onboard.IIMUCtxUpdated;

/**
 * This interface serves as the delegate for all on-board sensor contexts.
 * 
 * @author Shinda
 * @version 1.0 05/10/2015
 */
public interface IOnBoardCtxUpdated extends IIMUCtxUpdated, ICameraCtxUpdated {

}
