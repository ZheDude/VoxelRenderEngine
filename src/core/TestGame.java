package core;

import BlockData.BlockType;
import BlockData.Cube;
import core.Entity.Entity;
import core.Utils.GreedyMeshing;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.lwjgl.opengl.GL46.*;
import static org.lwjgl.glfw.GLFW.*;

public class TestGame implements ILogic {

    private static final float CAMERA_MOVE_SPEED = 0.05f;

    private final RenderManager renderer;
    private final WindowManager window;
    private static ObjectLoader loader;

    public static Map<Vector3f, Entity> world = new HashMap<>();
    public static Map<Vector3f, Entity> renderWorld = new HashMap<>();
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
        camera.setRotation((float) pitch, (float) yaw, 0.0f);
    }

    public static void mouse_button_callback(long window, int button, int action, int mods) {

        if ((button == GLFW_MOUSE_BUTTON_1) && action == GLFW_PRESS) {
            //TODO:
        } else if (button == GLFW_MOUSE_BUTTON_2 && action == GLFW_PRESS) {
            //TODO:
        }

    }

    @Override
    public void init() throws Exception {
        renderer.init();
        if (window.isResize()) {
            glViewport(0, 0, window.getWidth(), window.getHeight());
            window.setResize(true);
        }
        window.setClearColor(0.0f, 0.0f, 0.0f, 0.0f);

        Vector3f defaultRotation = new Vector3f(0, 0, 0); // Reuse this for all cubes

        Cube c = new Cube(loader, new Vector3f(0, 0, -0), defaultRotation, 1, BlockType.DIRT);
        Entity entity = c.generateEntity();

        for (int i = 0; i < 7; i++) {
            for (int j = 0; j < 7; j++) {
                for (int k = 0; k < 7; k++) {
                    entity.setPos(i, k, -j);
                    Entity a = entity.clone();
                    world.put(a.getPos(), a);
                }
            }
        }
        entity.setPos(7, 6, -6);
        world.put(entity.getPos(), entity.clone());
        entity.setPos(7, 6, -5);
        world.put(entity.getPos(), entity.clone());

        try {
            List<Cube> optimizedMesh = GreedyMeshing.generateMesh(world, loader);
            for (Cube cube : optimizedMesh) {
                Entity e = cube.generateEntity();
                renderWorld.put(e.getPos(), e);
            }
            System.out.println(renderWorld);
        } catch (Exception e){
            e.printStackTrace();
            // Handle exceptions appropriately
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
        if (window.isKeyPressed(GLFW_KEY_B)) {
            glPolygonMode(GL_FRONT_AND_BACK, GL_LINE);
        }
        if (window.isKeyPressed(GLFW_KEY_N)) {
            glPolygonMode(GL_FRONT_AND_BACK, GL_FILL);
        }
    }


    @Override
    public void update() {
        float frameTime = EngineManager.getFrameTime();
        camera.movePosition(cameraInc.x * CAMERA_MOVE_SPEED * frameTime,
                cameraInc.y * CAMERA_MOVE_SPEED * frameTime,
                cameraInc.z * CAMERA_MOVE_SPEED * frameTime);
        //world.values().forEach(e -> e.incRotation(0.0f, 0.0f, 0.0f));
    }

    @Override
    public void render() {
        renderer.clear();

        // Draw opaque entities first
        for (Entity entity : renderWorld.values()) {
            if (!entity.isTransparent()) {
                renderer.renderCubes(entity, camera);
            }
        }

        // Draw transparent entities last
        for (Entity entity : renderWorld.values()) {
            if (entity.isTransparent()) {
                renderer.renderCubes(entity, camera);
            }
        }

//        for (Entity entity : world.values()) {
//            if (!entity.isTransparent()) {
//                renderer.renderCubes(entity, camera);
//            }
//        }
//
//        for (Entity entity : world.values()) {
//            if (entity.isTransparent()) {
//                renderer.renderCubes(entity, camera);
//            }
//        }
    }

    @Override
    public void cleanUp() {
        renderer.cleanUp();
        loader.cleanUp();
    }
}
