package core;

import BlockData.Cube;
import core.Entity.Entity;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL46.*;

public class TestGame implements ILogic {

    private static final float CAMERA_MOVE_SPEED = 0.05f;

    private final RenderManager renderer;
    private final WindowManager window;
    private final ObjectLoader loader;
    private final List<Entity> entity = new ArrayList<>();
    private static Camera camera;

    private static float sensitivity = 0.1f;
    Vector3f cameraInc;

    static double lastX, lastY, yaw, pitch;
    static boolean firstMouse = true;

    public TestGame() {
        renderer = new RenderManager();
        window = Main.getWindow();
        loader = new ObjectLoader();
        camera = new Camera();
        camera.setPosition(-5, 3, 10);
        cameraInc = new Vector3f(0, 0, 0);
        lastX = window.getWidth() / 2.0f;
        lastY = window.getHeight() / 2.0f;
    }


    public static void mouse_callback(long window, double xpos, double ypos) {
        if (firstMouse) {
            lastX = xpos;
            lastY = ypos;
            firstMouse = false;
        }
        double xoffset = xpos - lastX;
        double yoffset = lastY - ypos;
        lastX = xpos;
        lastY = ypos;

        xoffset *= sensitivity;
        yoffset *= sensitivity;

        yaw += xoffset;
        pitch -= yoffset;

        if (pitch > 89.0f)
            pitch = 89.0f;
        if (pitch < -89.0f)
            pitch = -89.0f;

        System.out.println("xpos = " + xpos);
        System.out.println("ypos = " + ypos);
        System.out.println("xoffset = " + xoffset);
        System.out.println("yoffset = " + yoffset);
        System.out.println("yaw = " + yaw);
        System.out.println("pitch = " + pitch);
        System.out.println("lastX = " + lastX);
        System.out.println("lastY = " + lastY);
        camera.setRotation((float) pitch, (float) yaw, 0.0f);
    }

    @Override
    public void init() throws Exception {
        renderer.init();


        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                for (int k = -1; k < 64; k++) {
                    Cube c = new Cube(loader, new Vector3f(i, k, -j), new Vector3f(0, 0, 0), 1);
                    entity.add(c.generateEntity());
                }
            }
        }

    }

    @Override
    public void input() {
        cameraInc.set(0, 0, 0);
        if (window.isKeyPressed(GLFW.GLFW_KEY_W)) {
            cameraInc.z = -100;
        }
        if (window.isKeyPressed(GLFW.GLFW_KEY_S)) {
            cameraInc.z = 100;
        }
        if (window.isKeyPressed(GLFW.GLFW_KEY_A)) {
            cameraInc.x = -100;
        }
        if (window.isKeyPressed(GLFW.GLFW_KEY_D)) {
            cameraInc.x = 100;
        }
        if (window.isKeyPressed(GLFW.GLFW_KEY_LEFT_CONTROL)) {
            cameraInc.y = -100;
        }
        if (window.isKeyPressed(GLFW.GLFW_KEY_SPACE)) {
            cameraInc.y = 100;
        }
    }

    @Override
    public void update() {
        float frameTime = EngineManager.getFrameTime();
        camera.movePosition(cameraInc.x * CAMERA_MOVE_SPEED * frameTime,
                cameraInc.y * CAMERA_MOVE_SPEED * frameTime,
                cameraInc.z * CAMERA_MOVE_SPEED * frameTime);
        entity.forEach(e -> e.incRotation(0.0f, 0.0f, 0.0f));
    }

    @Override
    public void render() {
        if (window.isResize()) {
            glViewport(0, 0, window.getWidth(), window.getHeight());
            window.setResize(true);
        }

        window.setClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        renderer.clear();
        renderer.render(entity, camera);
    }

    @Override
    public void cleanUp() {
        renderer.cleanUp();
        loader.cleanUp();
    }
}
