package core;

import BlockData.Cube;
import core.Entity.Entity;
import core.Entity.RayEntity;
import core.Utils.OctreeNode;
import core.Utils.RayCasting;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL46.*;
import static org.lwjgl.glfw.GLFW.*;

public class TestGame implements ILogic {

    private static final float CAMERA_MOVE_SPEED = 0.05f;

    private final RenderManager renderer;
    private final WindowManager window;
    private static ObjectLoader loader;
    private static final List<Entity> entities = new ArrayList<>();
    private static List<RayEntity> rayEntities = new ArrayList<>();
    private static Camera camera;
    private static OctreeNode root;
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

    static Vector3f minCorner = new Vector3f(-100, -100, -100); // Adjust these values as needed
    static  Vector3f maxCorner = new Vector3f(100, 100, 100); // Adjust these values as needed

    public static void mouse_button_callback(long window, int button, int action, int mods) {
        if ((button == GLFW_MOUSE_BUTTON_1 || button == GLFW_MOUSE_BUTTON_2) && action == GLFW_PRESS) {
            // I need to figure out how to get the block that the mouse is pointing at
            int maxRange = 5;
            RayCasting rayCasting = new RayCasting(maxRange);
            Vector3f ray = rayCasting.calculateRay(Main.getWindow(), camera);
            RayEntity rayEntity = new RayEntity(loader, new Vector3f(0, 0, 0), new Vector3f(0, 0, 0), maxRange);
            rayEntity.setOrigin(rayCasting.getRayOrigin());
            rayEntity.setDirection(rayCasting.getRayDirection());
            rayEntities.clear();
            rayEntities.add(rayEntity);
            System.out.println(rayCasting);

            Entity closestEntity = root.intersectRay(rayCasting.getRayOrigin(), rayCasting.getRayDirection());
            if (closestEntity != null) {
                Vector3f entityPosition = closestEntity.getPos();
                System.out.println(entityPosition);
                try {
                    Cube c = new Cube(loader, new Vector3f(entityPosition), new Vector3f(0, 0, 0), 1);
                    entities.remove(c.generateEntity());

                    // Rebuild the Octree
                    root = new OctreeNode(minCorner, maxCorner, new ArrayList<>());
                    for (Entity entity : entities) {
                        root.addEntity(entity);
                    }
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }

    }

    @Override
    public void init() throws Exception {
        renderer.init();
        root = new OctreeNode(minCorner, maxCorner, new ArrayList<>());

        // Populate the Octree with entities
        for (int i = 0; i < 31; i++) {
            for (int j = 0; j < 31;  j++) {
                for (int k = 0; k < 31; k++) {
                    Cube c = new Cube(loader, new Vector3f(i, k, -j), new Vector3f(0, 0, 0), 1);
                    Entity entity = c.generateEntity();
                    entities.add(entity);
                    root.addEntity(entity);
                }
            }
        }

        // Subdivide the Octree
        root.subdivide();
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
        entities.forEach(e -> e.incRotation(0.0f, 0.0f, 0.0f));
    }

    @Override
    public void render() {
        if (window.isResize()) {
            glViewport(0, 0, window.getWidth(), window.getHeight());
            window.setResize(true);
        }

        window.setClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        renderer.clear();
        renderer.renderCubes(entities, camera);
        renderer.renderRays(rayEntities, camera);
    }

    @Override
    public void cleanUp() {
        renderer.cleanUp();
        loader.cleanUp();
    }
}
