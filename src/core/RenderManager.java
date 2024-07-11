package core;

import core.Entity.Entity;
import core.Utils.Transformation;
import core.Utils.Utils;
import org.joml.Vector3f;

import java.util.List;
import java.util.Set;

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

    public void renderCubes(Entity e, Camera camera) {
        shader.bind();
        shader.setUniform("textureSampler", 0);
        shader.setUniform("projectionMatrix", window.updateProjectionMatrix());
        shader.setUniform("viewMatrix", Transformation.getViewMatrix(camera));
        shader.setUniform("transformationMatrix", Transformation.createTransformationMatrix(e));
        glBindVertexArray(e.getModel().getVaoID());
        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);
        glActiveTexture(GL_TEXTURE0);
        glBindTexture(GL_TEXTURE_2D, e.getModel().getTexture().getID());

//        Set<Integer> sameTypeFaces = e.getFacesWithSameTypeNeighbors();
//        if (!sameTypeFaces.contains(12)) {
//            glDrawElements(GL_TRIANGLES, 6, GL_UNSIGNED_INT, 12 * Integer.BYTES);
//        }
//        if (!sameTypeFaces.contains(18)) {
//            glDrawElements(GL_TRIANGLES, 6, GL_UNSIGNED_INT, 18 * Integer.BYTES); // top face
//        }
//        if (!sameTypeFaces.contains(30)) {
//            glDrawElements(GL_TRIANGLES, 6, GL_UNSIGNED_INT, 30 * Integer.BYTES); // top face
//        }
//        if (!sameTypeFaces.contains(24)) {
//            glDrawElements(GL_TRIANGLES, 6, GL_UNSIGNED_INT, 24 * Integer.BYTES); // top face
//        }
//        if (!sameTypeFaces.contains(6)) {
//            glDrawElements(GL_TRIANGLES, 6, GL_UNSIGNED_INT, 6 * Integer.BYTES); // top face
//        }
//        if (!sameTypeFaces.contains(0)) {
//            glDrawElements(GL_TRIANGLES, 6, GL_UNSIGNED_INT, 0 * Integer.BYTES); // top face
//        }

        if (!e.isNeighborSameType(new Vector3f(-1, 0, 0))) {
            glDrawElements(GL_TRIANGLES, 6, GL_UNSIGNED_INT, 12 * Integer.BYTES);
        }
        if (!e.isNeighborSameType(new Vector3f(0, 1, 0))) {
            glDrawElements(GL_TRIANGLES, 6, GL_UNSIGNED_INT, 18 * Integer.BYTES); // top face
        }
        if (!e.isNeighborSameType(new Vector3f(0, 0, 1))) {
            glDrawElements(GL_TRIANGLES, 6, GL_UNSIGNED_INT, 30 * Integer.BYTES); // front face
        }
        if (!e.isNeighborSameType(new Vector3f(0, -1, 0))) {
            glDrawElements(GL_TRIANGLES, 6, GL_UNSIGNED_INT, 24 * Integer.BYTES); // bottom face
        }
        if (!e.isNeighborSameType(new Vector3f(1, 0, 0))) {
            glDrawElements(GL_TRIANGLES, 6, GL_UNSIGNED_INT, 6 * Integer.BYTES); // right face
        }
        if (!e.isNeighborSameType(new Vector3f(0, 0, -1))) {
            glDrawElements(GL_TRIANGLES, 6, GL_UNSIGNED_INT, 0); // back face
        }
        glDisableVertexAttribArray(0);
        glDisableVertexAttribArray(1);
        glBindVertexArray(0);
        shader.unbind();
    }

    public void renderCrosshair(Camera camera) {
        shader.bind();
        shader.unbind();
    }

    public void clear() {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT | GL_STENCIL_BUFFER_BIT);
    }

    public void cleanUp() {
        shader.cleanup();
    }
}
