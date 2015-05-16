package com.app;

public class StoryBall extends MemoryApp {

	public StoryBall() {
		super();
		ctxEngine.startIMU();
	}

	@Override
	public void OnBoardIMUShakeDetected() {
		System.out.println("Shaked");
	}

}
