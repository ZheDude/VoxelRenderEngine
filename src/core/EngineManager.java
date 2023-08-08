package core;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;

import static core.Utils.Consts.*;

public class EngineManager {


    private static int fps;
    private static float FrameTime = 1.0f / Framerate;

    private boolean running;

    private WindowManager window;
    private GLFWErrorCallback errorCallback;
    private ILogic gameLogic;

    private void init() throws Exception {
        GLFW.glfwSetErrorCallback(errorCallback = GLFWErrorCallback.createPrint(System.err));
        window = Main.getWindow();
        gameLogic = Main.getGame();
        window.init();
        gameLogic.init();
    }

    public void start() throws Exception {
        init();
        if (running) {
            return;
        }

        run();
    }

    private void run() {
        this.running = true;
        int frames = 0;
        long frameCounter = 0;
        long lastTime = System.nanoTime();
        double unprocessedTime = 0;

        while (running) {
            boolean render = false;
            long startTime = System.nanoTime();
            long passedTime = startTime - lastTime;
            lastTime = startTime;

            unprocessedTime += passedTime / (double) NANOS_PER_SECOND;
            frameCounter += passedTime;

            input();

            while (unprocessedTime > FrameTime) {
                render = true;
                unprocessedTime -= FrameTime;

                if (window.windowShouldClose()) {
                    stop();
                }

                update();

                if (frameCounter >= NANOS_PER_SECOND) {
                    setFps(frames);
                    window.setTitle(TITLE + " FPS: " + getFps());
                    frames = 0;
                    frameCounter = 0;
                }
            }

            if (render) {
                update();
                render();
                frames++;
            } else {
                try {
                    Thread.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        cleanUp();
    }

    private void stop() {
        if (!running) {
            return;
        }

        running = false;
    }

    private void input() {
        gameLogic.input();
    }

    private void render() {
        gameLogic.render();
        window.update();
    }

    private void update() {
        gameLogic.update();
    }

    private void cleanUp() {
        gameLogic.cleanUp();
        window.cleanUp();
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
