package mtk.ctxengine.sensors;

import mtk.ctxengine.sensors.offboard.IOffBoardCtxUpdated;
import mtk.ctxengine.sensors.onboard.IOnBoardCtxUpdated;

/**
 * This interface serves as the delegate for all contexts, from both on and
 * off-board sensors.
 * 
 * @author Shinda
 * @version 1.0 05/10/2015
 */
public interface ICtxUpdated extends IOnBoardCtxUpdated, IOffBoardCtxUpdated {

}
