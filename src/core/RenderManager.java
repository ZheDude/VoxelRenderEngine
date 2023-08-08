package core;

import core.Entitiy.Model;
import core.Utils.Utils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;

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
    }

    public void render(Model model) {
        clear();
        shader.bind();
        GL30.glBindVertexArray(model.getVaoID());
        GL30.glEnableVertexAttribArray(0);
        GL30.glDrawArrays(GL30.GL_TRIANGLES, 0, model.getVertexCount());
        GL30.glDisableVertexAttribArray(0);
        GL30.glBindVertexArray(0);
        shader.unbind();
    }
    public void clear(){
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
    }

    public void cleanUp() {
        shader.cleanup();
    }
}
