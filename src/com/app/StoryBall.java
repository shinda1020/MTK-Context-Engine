package com.app;

public class StoryBall extends MemoryApp {

	public StoryBall() {
		super();
		ctxEngine.startOffBoardSensor("Speech");
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
