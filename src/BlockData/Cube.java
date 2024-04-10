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
//            -0.5f, -0.5f, -0.5f, // 0: bottom-left-back
//            0.5f, -0.5f, -0.5f, // 1: bottom-right-back
//            -0.5f, 0.5f, -0.5f, // 2: top-left-back
//            0.5f, 0.5f, -0.5f, // 3: top-right-back
//            -0.5f, -0.5f, 0.5f, // 4: bottom-left-front
//            0.5f, -0.5f, 0.5f, // 5: bottom-right-front
//            -0.5f, 0.5f, 0.5f, // 6: top-left-front
//            0.5f, 0.5f, 0.5f, // 7: top-right-front
//    };
//
//    int[] indices = new int[]{
//            0, 2, 1, 1, 2, 3, // back face
//            4, 5, 6, 5, 7, 6, // front face
//            0, 4, 2, 2, 4, 6, // left face
//            1, 3, 5, 3, 7, 5, // right face
//            2, 6, 3, 3, 6, 7, // top face
//            0, 1, 4, 1, 5, 4  // bottom face
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

    public int getNeighbour(List<Cube> allCubes) {
        System.out.println(this.pos);
        int neighbourCount = 0;

        //generate the coordinates of the 6 neighbors of each face
        Vector3f[] neighbours = new Vector3f[]{
                new Vector3f(this.pos.x, this.pos.y, this.pos.z + 1), //front face
                new Vector3f(this.pos.x, this.pos.y, this.pos.z - 1), //back face
                new Vector3f(this.pos.x + 1, this.pos.y, this.pos.z), //right face
                new Vector3f(this.pos.x - 1, this.pos.y, this.pos.z), //left face
                new Vector3f(this.pos.x, this.pos.y + 1, this.pos.z), //top face
                new Vector3f(this.pos.x, this.pos.y - 1, this.pos.z), //bottom face
        };

        //check if the coordinates are in the list of all cubes
        for (Vector3f neighbour : neighbours) {
            for (Cube cube : allCubes) {
                if (cube.pos.equals(neighbour)) {
                    neighbourCount++;
                }
            }
        }
        return neighbourCount;
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
