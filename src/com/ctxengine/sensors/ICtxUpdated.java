package com.ctxengine.sensors;

import com.ctxengine.sensors.IOnBoardCtxUpdated;
import com.ctxengine.sensors.IOffBoardCtxUpdated;

/**
 * This interface serves as the delegate for all contexts, from both on and
 * off-board sensors.
 * 
 * @author Shinda
 * @version 1.0 05/10/2015
 */
public interface ICtxUpdated extends IOnBoardCtxUpdated, IOffBoardCtxUpdated {

}
