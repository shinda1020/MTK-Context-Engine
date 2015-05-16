package com.app;

public class StoryBall extends MemoryApp {

	private String hostName = "192.168.2.1";
	private String methodFilePath = "methods.json";

	public StoryBall() {
		super();
	}

	@Override
	public void start() {
		// Very important, must start with super.start()!
		super.start();
		ctxEngine.startIMU();
	}

	@Override
	protected String getHostName() {
		return hostName;
	}

	@Override
	protected String getMethodFilePath() {
		return methodFilePath;
	}

	@Override
	public void OnBoardIMUShakeDetected() {
		System.out.println("Shaked");
		// To-do
		/* Play some audio clips */
	}

}
