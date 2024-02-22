package core;

import core.Entity.Entity;
import core.Utils.Transformation;
import core.Utils.Utils;

import java.util.List;

import static org.lwjgl.opengl.GL46.*;

public class RenderManager {
    private final WindowManager window;
    private ShaderManager shader;

    public RenderManager() {
        this.window = Main.getWindow();
    }

    public void init() throws Exception {
        shader = new ShaderManager();
        shader.createVertexShader(Utils.loadResource("resources/shaders/vertex.vs"));
        shader.createFragmentShader(Utils.loadResource("resources/shaders/fragment.fs"));
        shader.link();
        shader.createUniform("textureSampler");
        shader.createUniform("transformationMatrix");
        shader.createUniform("projectionMatrix");
        shader.createUniform("viewMatrix");
    }

    public void renderCubes(List<Entity> entity, Camera camera) {
        clear();
        for (Entity e : entity) {
            if (e.getNeighbour(entity) == 6) {
                continue;
            }
            shader.bind();
            shader.setUniform("textureSampler", 0);
            shader.setUniform("transformationMatrix", Transformation.createTransformationMatrix(e));
            shader.setUniform("projectionMatrix", window.updateProjectionMatrix());
            shader.setUniform("viewMatrix", Transformation.getViewMatrix(camera));
            glBindVertexArray(e.getModel().getVaoID());
            glEnableVertexAttribArray(0);
            glEnableVertexAttribArray(1);
            glActiveTexture(GL_TEXTURE0);
            glBindTexture(GL_TEXTURE_2D, e.getModel().getTexture().getID());
            glDrawElements(GL_TRIANGLES, e.getModel().getVertexCount(), GL_UNSIGNED_INT, 0);
            glDisableVertexAttribArray(0);
            glDisableVertexAttribArray(1);
            glBindVertexArray(0);
            shader.unbind();
        }
    }

    public void clear() {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT | GL_STENCIL_BUFFER_BIT);
    }

    public void cleanUp() {
        shader.cleanup();
    }
}
