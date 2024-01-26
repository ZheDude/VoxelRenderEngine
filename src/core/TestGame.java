package core;

import core.Entitiy.Entity;
import core.Entitiy.Model;
import core.Entitiy.Texture;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;

import static org.lwjgl.opengl.GL46.*;

public class TestGame implements ILogic {

    private static final float CAMERA_MOVE_SPEED = 0.05f;

    private final RenderManager renderer;
    private final WindowManager window;
    private final ObjectLoader loader;
    private Entity entity;
    private Camera camera;
    private float entityZ;

    Vector3f cameraInc;

    public TestGame() {
        renderer = new RenderManager();
        window = Main.getWindow();
        loader = new ObjectLoader();
        camera = new Camera();
        cameraInc = new Vector3f(0, 0, 0);
    }

    @Override
    public void init() throws Exception {
        renderer.init();

        float[] vertices = new float[]{
                // front face
                -0.5f, 0.5f, 0.5f, // 0 top left
                -0.5f, -0.5f, 0.5f, // 1 bottom left
                0.5f, -0.5f, 0.5f, // 2 bottom right
                0.5f, 0.5f, 0.5f, // 3 top right

                // back face
                -0.5f, 0.5f, -0.5f, // 4 top left
                -0.5f, -0.5f, -0.5f, // 5 bottom left
                0.5f, -0.5f, -0.5f, // 6 bottom right
                0.5f, 0.5f, -0.5f, // 7 top right

                //right face
                0.5f, 0.5f, 0.5f, // 8 top left
                0.5f, -0.5f, 0.5f, // 9 bottom left
                0.5f, -0.5f, -0.5f, // 10 bottom right
                0.5f, 0.5f, -0.5f, // 11 top right

                //left face
                -0.5f, 0.5f, 0.5f, // 12 top left
                -0.5f, -0.5f, 0.5f, // 13 bottom left
                -0.5f, -0.5f, -0.5f, // 14 bottom right
                -0.5f, 0.5f, -0.5f, // 15 top right

                //top face
                -0.5f, 0.5f, 0.5f, // 16 top left
                -0.5f, 0.5f, -0.5f, // 17 bottom left
                0.5f, 0.5f, -0.5f, // 18 bottom right
                0.5f, 0.5f, 0.5f, // 19 top right

                //bottom face
                -0.5f, -0.5f, 0.5f, // 20 top left
                -0.5f, -0.5f, -0.5f, // 21 bottom left
                0.5f, -0.5f, -0.5f, // 22 bottom right
                0.5f, -0.5f, 0.5f, // 23 top right
        };

        // look into java OBJ parser
        int[] indices = new int[]{
                0, 1, 3, 3, 1, 2, // front face
                4, 5, 7, 7, 5, 6, // back face
                8, 9, 11, 11, 9, 10, // right face
                12, 13, 15, 15, 13, 14, // left face
                16, 17, 19, 19, 17, 18, // top face
                20, 21, 23, 23, 21, 22, // bottom face
        };

        float[] textCoords = new float[]{
                //format
                //x, y

                //front face
                0.0f, 0.0f,
                0.0f, 1.0f,
                1.0f, 1.0f,
                1.0f, 0.0f,

                //back face
                1.0f, 0.0f,
                1.0f, 1.0f,
                0.0f, 1.0f,
                0.0f, 0.0f,

                //right face
                0.0f, 0.0f,
                0.0f, 1.0f,
                1.0f, 1.0f,
                1.0f, 0.0f,

                //left face
                1.0f, 0.0f,
                1.0f, 1.0f,
                0.0f, 1.0f,
                0.0f, 0.0f,

                //top face
                0.0f, 1.0f,
                0.0f, 0.0f,
                1.0f, 0.0f,
                1.0f, 1.0f,

                //bottom face
                1.0f, 1.0f,
                1.0f, 0.0f,
                0.0f, 0.0f,
                0.0f, 1.0f

        };

        Model model = loader.loadModel(vertices, textCoords, indices);
        model.setTexture(new Texture(loader.loadTexture("resources/textures/dirt.png")));
        entity = new Entity(model, new Vector3f(0, 0,  -5), new Vector3f(0, 0, 0), 1);
    }

    @Override
    public void input() {
        cameraInc.set(0, 0, 0);
        if (window.isKeyPressed(GLFW.GLFW_KEY_W)) {
            cameraInc.z = -1;
        }
        if (window.isKeyPressed(GLFW.GLFW_KEY_S)) {
            cameraInc.z = 1;
        }
        if (window.isKeyPressed(GLFW.GLFW_KEY_A)) {
            cameraInc.x = -1;
        }
        if (window.isKeyPressed(GLFW.GLFW_KEY_D)) {
            cameraInc.x = 1;
        }
        if (window.isKeyPressed(GLFW.GLFW_KEY_LEFT_CONTROL)) {
            cameraInc.y = -1;
        }
        if (window.isKeyPressed(GLFW.GLFW_KEY_SPACE)) {
            cameraInc.y = 1;
        }
        if (window.isKeyPressed(GLFW.GLFW_KEY_LEFT_SHIFT)) {
            entityZ = 0.5f;
        }
        if (window.isKeyPressed(GLFW.GLFW_KEY_LEFT_ALT)) {
            entityZ = 0;
        }
    }

    @Override
    public void update() {
        camera.movePosition(cameraInc.x * CAMERA_MOVE_SPEED,
                cameraInc.y * CAMERA_MOVE_SPEED,
                cameraInc.z * CAMERA_MOVE_SPEED);
        entity.incRotation(0.0f, entityZ, 0.0f);
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
