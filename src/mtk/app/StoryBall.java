package mtk.app;

public class StoryBall extends MemoryApp {

	private final String HOST_NAME = "localhost";
	private final String METHOD_FILE = "methods.json";

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
		return HOST_NAME;
	}

	@Override
	protected String getMethodFile() {
		return METHOD_FILE;
	}

	@Override
	public void OnBoardIMUShakeDetected() {
		System.out.println("Shaked");
		// To-do
		/* Play some audio clips */
	}

}
