package core;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.opengl.GL30;
import org.lwjgl.system.MemoryStack;

import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

public class ShaderManager {
    private final int programID;
    private final Map<String, Integer> uniforms;

    private int vertexShaderID, fragmentShaderID;

    public ShaderManager() throws Exception {
        this.programID = GL30.glCreateProgram();
        if (programID == 0) {
            throw new Exception("Could not create Shader");
        }
        uniforms = new HashMap<>();
    }

    public void createVertexShader(String shaderCode) throws Exception {
        vertexShaderID = createShader(shaderCode, GL30.GL_VERTEX_SHADER);
    }

    public void createFragmentShader(String shaderCode) throws Exception {
        fragmentShaderID = createShader(shaderCode, GL30.GL_FRAGMENT_SHADER);
    }

    private int createShader(String shaderCode, int shaderType) throws Exception {
        int shaderID = GL30.glCreateShader(shaderType);
        if (shaderID == 0) {
            throw new Exception("Error creating shader. Type: " + shaderType);
        }
        GL30.glShaderSource(shaderID, shaderCode);
        GL30.glCompileShader(shaderID);
        if (GL30.glGetShaderi(shaderID, GL30.GL_COMPILE_STATUS) == 0) {
            throw new Exception("Error compiling " + shaderType + " Shader code: " + GL30.glGetShaderInfoLog(shaderID, 1024));
        }
        GL30.glAttachShader(programID, shaderID);
        return shaderID;
    }


    public void link() throws Exception {
        GL30.glLinkProgram(programID);
        if (GL30.glGetProgrami(programID, GL30.GL_LINK_STATUS) == 0) {
            throw new Exception("Error linking Shader code: " + GL30.glGetProgramInfoLog(programID, 1024));
        }
        if (vertexShaderID != 0) {
            GL30.glDetachShader(programID, vertexShaderID);
        }
        if (fragmentShaderID != 0) {
            GL30.glDetachShader(programID, fragmentShaderID);
        }
        GL30.glValidateProgram(programID);
        if (GL30.glGetProgrami(programID, GL30.GL_VALIDATE_STATUS) == 0) {
            System.err.println("Warning validating Shader code: " + GL30.glGetProgramInfoLog(programID, 1024));
        }
    }

    public void bind() {
        GL30.glUseProgram(programID);
    }

    public void unbind() {
        GL30.glUseProgram(0);
    }

    public void cleanup() {
        unbind();
        if (programID != 0) {
            GL30.glDeleteProgram(programID);
        }
    }

    public void createUniform(String uniformName) throws Exception {
        int uniformLocation = GL30.glGetUniformLocation(programID, uniformName);
        if (uniformLocation < 0) {
            throw new Exception("Could not find uniform:" + uniformName);
        }
        uniforms.put(uniformName, uniformLocation);
    }

    public void setUniform(String uniformName, Matrix4f value) {
        // Dump the matrix into a float buffer
        try (MemoryStack stack = MemoryStack.stackPush()) {
            GL30.glUniformMatrix4fv(uniforms.get(uniformName), false, value.get(stack.mallocFloat(16)));
        }
    }

    public void setUniform(String uniformName, boolean value) {
        float res = value ? 1 : 0;
        GL30.glUniform1f(uniforms.get(uniformName), res);
    }

    public void setUniform(String uniformName, Vector4f value) {
        GL30.glUniform4f(uniforms.get(uniformName), value.x, value.y, value.z, value.w);
    }


    public void setUniform(String uniformName, Vector3f value) {
        GL30.glUniform3f(uniforms.get(uniformName), value.x, value.y, value.z);
    }

    public void setUniform(String uniformName, int value) {
        GL30.glUniform1i(uniforms.get(uniformName), value);
    }

    public void setUniform(String uniformName, float value) {
        GL30.glUniform1f(uniforms.get(uniformName), value);
    }
}
