package core;

import BlockData.Cube;
import core.Entity.Entity;
import core.Entity.RayEntity;
import core.Utils.Transformation;
import core.Utils.Utils;
import org.joml.Vector3f;

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

    public void renderCubes(Entity e, Camera camera) {
//        clear();
        shader.bind();
        shader.setUniform("textureSampler", 0);
        shader.setUniform("projectionMatrix", window.updateProjectionMatrix());
        shader.setUniform("viewMatrix", Transformation.getViewMatrix(camera));
//        for (Entity e : entity) {
        shader.setUniform("transformationMatrix", Transformation.createTransformationMatrix(e));
        glBindVertexArray(e.getModel().getVaoID());
        glEnableVertexAttribArray(0);
        glEnableVertexAttribArray(1);
        glActiveTexture(GL_TEXTURE0);
        glBindTexture(GL_TEXTURE_2D, e.getModel().getTexture().getID());
//        glDrawElements(GL_TRIANGLES, e.getModel().getVertexCount(), GL_UNSIGNED_INT, 0);

//        System.out.printf("new Cube: current Cube Coords: %s \n------", e.getPos());
        if (!e.isNeighborSameType(new Vector3f(-1, 0, 0))) {
//            System.out.println("-x");
            glDrawElements(GL_TRIANGLES, 6, GL_UNSIGNED_INT, 12 * Integer.BYTES); // left face
        }
        if (!e.isNeighborSameType(new Vector3f(0, 1, 0))) {
//            System.out.println("+y");
            glDrawElements(GL_TRIANGLES, 6, GL_UNSIGNED_INT, 18 * Integer.BYTES); // top face
        }
        if (!e.isNeighborSameType(new Vector3f(0, 0, 1))) {
//            System.out.println("-z");
            glDrawElements(GL_TRIANGLES, 6, GL_UNSIGNED_INT, 30 * Integer.BYTES); // front face
        }
        if (!e.isNeighborSameType(new Vector3f(0, -1, 0))) {
//            System.out.println("-y");
            glDrawElements(GL_TRIANGLES, 6, GL_UNSIGNED_INT, 24 * Integer.BYTES); // bottom face
        }
        if (!e.isNeighborSameType(new Vector3f(1, 0, 0))) {
//            System.out.println("+x");
            glDrawElements(GL_TRIANGLES, 6, GL_UNSIGNED_INT, 6 * Integer.BYTES); // right face
        }
        if (!e.isNeighborSameType(new Vector3f(0, 0, -1))) {
//            System.out.println("+z");
            glDrawElements(GL_TRIANGLES, 6, GL_UNSIGNED_INT, 0); // back face
        }
//        System.out.println("------");
        glDisableVertexAttribArray(0);
        glDisableVertexAttribArray(1);
        glBindVertexArray(0);
//        }
        shader.unbind();
    }

    public void renderRays(List<RayEntity> rays, Camera camera) {
        shader.bind();
        shader.setUniform("projectionMatrix", window.updateProjectionMatrix());
        shader.setUniform("viewMatrix", Transformation.getViewMatrix(camera));
        for (RayEntity ray : rays) {
            shader.setUniform("transformationMatrix", Transformation.createTransformationMatrix(ray.getOrigin(), new Vector3f(0, 0, 0), 1));
            Vector3f normalizedDirection = new Vector3f(ray.getDirection()).normalize();
            float[] vertices = new float[]{
                    0f, 0f, 0f,
                    normalizedDirection.x * ray.maxRange, normalizedDirection.y * ray.maxRange, normalizedDirection.z * ray.maxRange
            };
            int vaoID = glGenVertexArrays();
            glBindVertexArray(vaoID);
            int vboID = glGenBuffers();
            glBindBuffer(GL_ARRAY_BUFFER, vboID);
            glBufferData(GL_ARRAY_BUFFER, vertices, GL_STATIC_DRAW);
            glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);
            glEnableVertexAttribArray(0);
            glDrawArrays(GL_LINES, 0, 2);
            glDisableVertexAttribArray(0);
            glBindBuffer(GL_ARRAY_BUFFER, 0);
            glBindVertexArray(0);
        }
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
