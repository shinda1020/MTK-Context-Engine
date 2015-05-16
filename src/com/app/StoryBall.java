package com.app;

public class StoryBall extends MemoryApp {

	private String hostName = "localhost";
	private String methodFilePath = "methods.json";

	public StoryBall() {
		super();
	}

	@Override
	public void start() {
		// Very important, must start with super.start()!
		super.start();
//		ctxEngine.startOffBoardSensor("Speech");
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
	}

	@Override
	public void OffBoardSpeechNoneDetected() {
		System.out.println("none");
	}

	@Override
	public void OffBoardSpeechLowDetected() {
		System.out.println("low");
	}

	@Override
	public void OffBoardSpeechHighDetected() {
		System.out.println("high");
	}
}
