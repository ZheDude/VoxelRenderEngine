package core.Entity;

import org.joml.Vector3f;

import java.util.List;
import java.util.Objects;

public class Entity {

    private Model model;
    private Vector3f pos, rotation;
    private float scale;

    public Entity(Model model, Vector3f pos, Vector3f rotation, float scale) {
        this.model = model;
        this.pos = pos;
        this.rotation = rotation;
        this.scale = scale;
    }


    public void incPos(float x, float y, float z) {
        this.pos.y += y;
        this.pos.x += x;
        this.pos.z += z;
    }

    public void setPos(float x, float y, float z) {
        this.pos.y = y;
        this.pos.x = x;
        this.pos.z = z;
    }


    public void incRotation(float x, float y, float z) {
        this.rotation.y += y;
        this.rotation.x += x;
        this.rotation.z += z;
    }

    public void setRotation(float x, float y, float z) {
        this.rotation.y = y;
        this.rotation.x = x;
        this.rotation.z = z;
    }

    public Model getModel() {
        return model;
    }

    public Vector3f getPos() {
        return pos;
    }

    public Vector3f getRotation() {
        return rotation;
    }

    public float getScale() {
        return scale;
    }

    public int getNeighbour(List<Entity> allCubes) {
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
            for (Entity cube : allCubes) {
                if (cube.getPos().equals(neighbour)) {
                    neighbourCount++;
                }
            }
        }
//        System.out.println(neighbourCount);
        return neighbourCount;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Entity entity = (Entity) o;
        return Objects.equals(pos, entity.pos);
    }

    @Override
    public int hashCode() {
        return Objects.hash(pos);
    }
}
