package project_3_gameengine.core;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;

import project_3_gameengine.Launcher;
import project_3_gameengine.core.utils.Constants;

public class EngineManager {
    
    public static final long NANOSECOND = 1000000000L;
    public static final float FRAMERATE = 1000;
    public static int fps;
    private static float frametime = 1.0f/FRAMERATE;

    private boolean isRunning; // is the engine running?

    private WindowManager window;
    private MouseInput mouseInput;
    private GLFWErrorCallback errorCallback;
    private ILogic gameLogic;

    private void init() throws Exception{
        GLFW.glfwSetErrorCallback(errorCallback = GLFWErrorCallback.createPrint(System.err));
        window = Launcher.getWindow();
        gameLogic = Launcher.getGame();
        mouseInput = new MouseInput();
        window.init();
        gameLogic.init();
        mouseInput.init();
    }

    public void start() throws Exception{
        init();
        if (isRunning) {
            return;
        }
        run();
    }

    public void run() {
        this.isRunning = true;
        int frames = 0;
        long frameCounter = 0;
        long lastTime = System.nanoTime();
        double unprocessedTime = 0;

        while (isRunning) {
            boolean render = false;
            long startTime = System.nanoTime();
            long passedTime = startTime - lastTime;
            lastTime = startTime;

            unprocessedTime += passedTime / (double) NANOSECOND;
            frameCounter += passedTime;

            input();

            while (unprocessedTime > frametime) {
                render = true;
                unprocessedTime -= frametime;

                if (window.windowShouldClose()) {
                    stop();
                }

                if (frameCounter >= NANOSECOND) {
                    setFps(frames);
                    window.setTitle(Constants.TITLE + getFps());
                    frames = 0;
                    frameCounter = 0;
                }
            }

            if (render) {
                update(frametime);
                render();
                frames++;
            }
        }
        cleanup();
    }

    private void stop(){
        if (!isRunning) {
            return;
        }
        isRunning = false;
    }

    private void input(){
        mouseInput.input();
        gameLogic.input();

    }

    private void render(){
        gameLogic.render();
        window.update();
    }

    private void update(float interval){
        gameLogic.update(interval, mouseInput);
    }

    private void cleanup(){
        window.cleanup();
        gameLogic.cleanup();
        errorCallback.free();
        GLFW.glfwTerminate();
    }

    public static int getFps() {
        return fps;
    }

    public static void setFps(int fps) {
        EngineManager.fps = fps;
    }

    


}
