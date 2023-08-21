package core;

import core.Entitiy.Model;
import core.Entitiy.Texture;
import core.Utils.Consts;
import org.lwjgl.glfw.GLFW;

import static org.lwjgl.opengl.GL46.*;

public class TestGame implements ILogic {
    private int direction = 0;
    private float color = 0;

    private final RenderManager renderer;
    private final WindowManager window;
    private final ObjectLoader loader;
    private Model model;

    public TestGame() {
        renderer = new RenderManager();
        window = Main.getWindow();
        loader = new ObjectLoader();
    }

    @Override
    public void init() throws Exception {
        renderer.init();
        float[] vertices = {
                -0.5f / Consts.ASPECT_RATIO, 0.5f, 0f,
                -0.5f / Consts.ASPECT_RATIO, -0.5f, 0f,
                0.5f / Consts.ASPECT_RATIO, -0.5f, 0f,
                0.5f / Consts.ASPECT_RATIO, 0.5f, 0f,
        };

        int[] indices = {
                0, 1, 3,
                3, 1, 2
        };

        float[] textureCoords = {
                0, 0,
                0, 1,
                1, 1,
                1, 0,
        };

        model = loader.loadModel(vertices, textureCoords, indices);
        model.setTexture(new Texture(loader.loadTexture("resources/textures/dirt.png")));
    }

    @Override
    public void input() {
        if (window.isKeyPressed(GLFW.GLFW_KEY_UP) || window.isKeyPressed(GLFW.GLFW_KEY_W)) {
            direction = 1;
        } else if (window.isKeyPressed(GLFW.GLFW_KEY_DOWN) || window.isKeyPressed(GLFW.GLFW_KEY_S)) {
            direction = -1;
        } else {
            direction = 0;
        }
    }

    @Override
    public void update() {
        color += direction * 0.01f;
        if (color > 1) {
            color = 1;
        } else if (color < 0) {
            color = 0;
        }
    }

    @Override
    public void render() {
        if (window.isResize()) {
            glViewport(0, 0, window.getWidth(), window.getHeight());
            window.setResize(true);
        }

        window.setClearColor(color, color, color, 0.0f);
        renderer.clear();
        renderer.render(model);
    }

    @Override
    public void cleanUp() {
        renderer.cleanUp();
        loader.cleanUp();
    }
}
