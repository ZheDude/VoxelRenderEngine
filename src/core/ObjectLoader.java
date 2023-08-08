package core;

import core.Entitiy.Model;
import core.Utils.Utils;
import org.lwjgl.opengl.GL30;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

public class ObjectLoader {
    private List<Integer> VAOs = new ArrayList<>();
    private List<Integer> VBOs = new ArrayList<>();

    public Model loadModel(float[] vertices, int[] indices) {
        int vaoID = createVAO();
//        bindIndicesBuffer(vertices);
        storeIndicesBuffer(indices);
        storeDataInAttributeList(0, 3, vertices);
        unbind();
        return new Model(vaoID, vertices.length);
    }

    private void unbind() {
        GL30.glBindVertexArray(0);
    }

    private void storeIndicesBuffer(int[] indices) {
        int vboID = GL30.glGenBuffers();
        VBOs.add(vboID);
        GL30.glBindBuffer(GL30.GL_ELEMENT_ARRAY_BUFFER, vboID);
        IntBuffer buffer = Utils.storeDataInIntBuffer(indices);
        GL30.glBufferData(GL30.GL_ELEMENT_ARRAY_BUFFER, buffer, GL30.GL_STATIC_DRAW);
    }

    private void bindIndicesBuffer(int[] indices) {

    }

    private void storeDataInAttributeList(int attributeNu, int vertexCount, float[] data) {
        int vboID = GL30.glGenBuffers();
        VBOs.add(vboID);
        GL30.glBindBuffer(GL30.GL_ARRAY_BUFFER, vboID);
        FloatBuffer buffer = Utils.storeDataInFloatBuffer(data);
        GL30.glBufferData(GL30.GL_ARRAY_BUFFER, buffer, GL30.GL_STATIC_DRAW);
        GL30.glVertexAttribPointer(attributeNu, vertexCount, GL30.GL_FLOAT, false, 0, 0);
        GL30.glBindBuffer(GL30.GL_ARRAY_BUFFER, 0);
    }

    public void cleanUp() {
        for (int vao : VAOs) {
            GL30.glDeleteVertexArrays(vao);
        }
        for (int vbo : VBOs) {
            GL30.glDeleteBuffers(vbo);
        }
    }

    private int createVAO() {
        int vaoID = GL30.glGenVertexArrays();
        VAOs.add(vaoID);
        GL30.glBindVertexArray(vaoID);
        return vaoID;
    }
}
