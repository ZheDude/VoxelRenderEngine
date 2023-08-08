package core;

import org.lwjgl.opengl.GL30;

public class ShaderManager {
    private final int programID;
    private int vertexShaderID, fragmentShaderID;

    public ShaderManager() throws Exception {
        this.programID = GL30.glCreateProgram();
        if (programID == 0) {
            throw new Exception("Could not create Shader");
        }
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
    }
}
