package BlockData;

import core.Entity.Entity;
import core.Entity.Model;
import core.Entity.Texture;
import core.ObjectLoader;
import org.joml.Vector3f;

import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Cube {

    private final ObjectLoader loader;
    private final Vector3f pos;
    private final Vector3f rotation;
    private final float scale;
    private BlockType blocktype;

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

    int[] indices = new int[]{
            4, 7, 5, 7, 6, 5, // back face
            8, 9, 11, 11, 9, 10, // right face
            12, 15, 13, 15, 14, 13, // left face
            16, 19, 17, 19, 18, 17, // top face
            20, 21, 23, 23, 21, 22, // bottom face
            0, 1, 3, 3, 1, 2, // front face
    };

//    float[] vertices = new float[]{
//            -0.5f, -0.5f, -0.5f, // 0: back-bottom-left     ---
//            0.5f, -0.5f, -0.5f, // 1: back-bottom-right     +--
//            -0.5f, 0.5f, -0.5f, // 2: back-top-left         -+-
//            0.5f, 0.5f, -0.5f, // 3: back-top-right         ++-
//            -0.5f, -0.5f, 0.5f, // 4: front-bottom-left     --+
//            0.5f, -0.5f, 0.5f, // 5: front-bottom-right     +-+
//            -0.5f, 0.5f, 0.5f, // 6: front-top-left         -++
//            0.5f, 0.5f, 0.5f, // 7: front-top-right         +++
//    };
//
//    int[] indices = new int[]{
//            2, 3, 0, 3, 1, 0, // back face
//            7, 5, 3, 3, 5, 1, // right face
//            6, 2, 4, 2, 0, 4, // left face
//            6, 7, 2, 7, 3, 2, // top face
//            4, 0, 2, 2, 0, 1, // bottom face
//            6, 4, 7, 7, 4, 5, // front face
//    };

    float[] textCoords = new float[]{
            //format
            //x, y

            //back face
            1.0f, 0.0f, 1.0f, 1.0f, 0.0f, 1.0f, 0.0f, 0.0f,

            //right face
            0.0f, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f, 1.0f, 0.0f,

            //left face
            1.0f, 0.0f, 1.0f, 1.0f, 0.0f, 1.0f, 0.0f, 0.0f,

            //top face
            0.0f, 1.0f, 0.0f, 0.0f, 1.0f, 0.0f, 1.0f, 1.0f,

            //bottom face
            1.0f, 1.0f, 1.0f, 0.0f, 0.0f, 0.0f, 0.0f, 1.0f,

            //front face
            0.0f, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f, 1.0f, 0.0f,

    };

    public Cube(ObjectLoader loader, Vector3f pos, Vector3f rotation, float scale, BlockType blocktype) throws Exception {
        this.loader = loader;
        this.pos = pos;
        this.rotation = rotation;
        this.scale = scale;
        this.blocktype = blocktype;
    }

    public void setPos(float x, float y, float z) {
        this.pos.x = x;
        this.pos.y = y;
        this.pos.z = z;
    }

    public Entity generateEntity() throws Exception {
        Model model = loader.loadModel(vertices, textCoords, indices);
        model.setTexture(new Texture(loader.loadTexture(blocktype.getTexturePath())));
        return new Entity(model, pos, rotation, scale, blocktype);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Cube cube = (Cube) o;
        return Objects.equals(pos, cube.pos);
    }

    @Override
    public int hashCode() {
        return Objects.hash(pos);
    }
}
