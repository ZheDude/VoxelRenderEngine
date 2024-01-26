package core;

import core.Entitiy.Model;
import core.Utils.Utils;
import org.lwjgl.opengl.GL30;
import org.lwjgl.stb.STBImage;
import org.lwjgl.system.MemoryStack;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

public class ObjectLoader {
    private List<Integer> VAOs = new ArrayList<>();
    private List<Integer> VBOs = new ArrayList<>();
    private List<Integer> textures = new ArrayList<>();

    public Model loadModel(float[] vertices, float[] texturePos, int[] indices) {
        int vaoID = createVAO();
        bindIndicesBuffer(indices);
        storeIndicesBuffer(indices);
        storeDataInAttributeList(0, 3, vertices);
        storeDataInAttributeList(1, 2, texturePos);
        unbind();
        return new Model(vaoID, vertices.length);
    }

    public int loadTexture(String fileName) throws Exception {
        int width, height;
        ByteBuffer buffer;
        try(MemoryStack stack = MemoryStack.stackPush()){
            IntBuffer w = stack.mallocInt(1);
            IntBuffer h = stack.mallocInt(1);
            IntBuffer comp = stack.mallocInt(1);
            buffer = STBImage.stbi_load(fileName, w, h, comp, 4);

            if (buffer == null){
                throw new Exception("Image file [" + fileName + "] not loaded: " + STBImage.stbi_failure_reason());
            }

            width = w.get();
            height = h.get();
        }
        int id = GL30.glGenTextures();
        textures.add(id);
        GL30.glBindTexture(GL30.GL_TEXTURE_2D, id);
        GL30.glPixelStorei(GL30.GL_UNPACK_ALIGNMENT, 1);
//        GL30.glTexParameteri(GL30.GL_TEXTURE_2D, GL30.GL_TEXTURE_WRAP_S, GL30.GL_REPEAT);
//        GL30.glTexParameteri(GL30.GL_TEXTURE_2D, GL30.GL_TEXTURE_WRAP_S, GL30.GL_REPEAT);
        GL30.glTexParameteri(GL30.GL_TEXTURE_2D, GL30.GL_TEXTURE_MIN_FILTER, GL30.GL_NEAREST);
        GL30.glTexParameteri(GL30.GL_TEXTURE_2D, GL30.GL_TEXTURE_MAG_FILTER, GL30.GL_NEAREST);
        GL30.glTexImage2D(GL30.GL_TEXTURE_2D, 0, GL30.GL_RGBA, width, height, 0, GL30.GL_RGBA, GL30.GL_UNSIGNED_BYTE, buffer);
        GL30.glBindTexture(GL30.GL_TEXTURE_2D, 0);
        GL30.glGenerateMipmap(GL30.GL_TEXTURE_2D);
        STBImage.stbi_image_free(buffer);
        return id;
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
        //TODO some stuff (really informative comment)
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
        for (int texture : textures) {
            GL30.glDeleteTextures(texture);
        }
    }

    private int createVAO() {
        int vaoID = GL30.glGenVertexArrays();
        VAOs.add(vaoID);
        GL30.glBindVertexArray(vaoID);
        return vaoID;
    }
}
