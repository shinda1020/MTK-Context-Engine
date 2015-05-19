package mtk.app;

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
		ctxEngine.startIMU();
	}

	@Override
	protected String getHostName() {
		return hostName;
	}

	@Override
	protected String getMethodFile() {
		return methodFilePath;
	}

	@Override
	public void OnBoardIMUShakeDetected() {
		System.out.println("Shaked");
		// To-do
		/* Play some audio clips */
	}

}
