package core.Entity;

import BlockData.BlockType;
import core.TestGame;
import org.joml.Vector3f;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class Entity implements Cloneable{

    private Model model;
    private Vector3f pos, rotation;
    private float scale;

    private BlockType blockType;
    private Vector3f tempVector = new Vector3f(); // Temporary vector for calculations


    public Entity(Model model, Vector3f pos, Vector3f rotation, float scale, BlockType blockType) {
        this.model = model;
        this.pos = pos;
        this.rotation = rotation;
        this.scale = scale;
        this.blockType = blockType;
    }


    public Vector3f getMinCorner() {
        return new Vector3f(
                this.pos.x - this.scale / 2,
                this.pos.y - this.scale / 2,
                this.pos.z - this.scale / 2
        );
    }
    public Vector3f getMaxCorner() {
        return new Vector3f(
                this.pos.x + this.scale / 2,
                this.pos.y + this.scale / 2,
                this.pos.z + this.scale / 2
        );
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
    public void setPos(Vector3f pos) {
        this.pos.y = pos.y;
        this.pos.x = pos.x;
        this.pos.z = pos.z;
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
    public void setRotation(Vector3f rotation) {
        this.rotation.y = rotation.y;
        this.rotation.x = rotation.x;
        this.rotation.z = rotation.z;
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
    public boolean intersectsCube(Vector3f minCorner, Vector3f maxCorner) {
        Vector3f entityMinCorner = getMinCorner();
        Vector3f entityMaxCorner = getMaxCorner();

        return entityMaxCorner.x >= minCorner.x && entityMinCorner.x <= maxCorner.x &&
                entityMaxCorner.y >= minCorner.y && entityMinCorner.y <= maxCorner.y &&
                entityMaxCorner.z >= minCorner.z && entityMinCorner.z <= maxCorner.z;
    }

    public float intersectRay(Vector3f rayOrigin, Vector3f rayDirection) {
        Vector3f entityMinCorner = getMinCorner();
        Vector3f entityMaxCorner = getMaxCorner();

        float tmin = (entityMinCorner.x - rayOrigin.x) / rayDirection.x;
        float tmax = (entityMaxCorner.x - rayOrigin.x) / rayDirection.x;

        if (tmin > tmax) {
            float temp = tmin;
            tmin = tmax;
            tmax = temp;
        }

        float tymin = (entityMinCorner.y - rayOrigin.y) / rayDirection.y;
        float tymax = (entityMaxCorner.y - rayOrigin.y) / rayDirection.y;

        if (tymin > tymax) {
            float temp = tymin;
            tymin = tymax;
            tymax = temp;
        }

        if ((tmin > tymax) || (tymin > tmax)) {
            return -1;
        }

        if (tymin > tmin) {
            tmin = tymin;
        }

        if (tymax < tmax) {
            tmax = tymax;
        }

        float tzmin = (entityMinCorner.z - rayOrigin.z) / rayDirection.z;
        float tzmax = (entityMaxCorner.z - rayOrigin.z) / rayDirection.z;

        if (tzmin > tzmax) {
            float temp = tzmin;
            tzmin = tzmax;
            tzmax = temp;
        }

        if ((tmin > tzmax) || (tzmin > tmax)) {
            return -1;
        }

        if (tzmin > tmin) {
            tmin = tzmin;
        }

        if (tzmax < tmax) {
            tmax = tzmax;
        }

        return tmin;
    }

    public Vector3f calculateDirection(Vector3f targetPosition) {
        Vector3f direction = new Vector3f(targetPosition);
        direction.sub(this.pos);
        direction.normalize();
        return direction;
    }

    public float getDistanceTo(Vector3f point) {
        return pos.distance(point);
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

    public boolean isTransparent() {
        return blockType.isTransparent();
    }


    public Set<Integer> getFacesWithSameTypeNeighbors() {
        Vector3f[] neighborDirections = new Vector3f[]{
                new Vector3f(-1, 0, 0), // Left face
                new Vector3f(0, 1, 0),  // Top face
                new Vector3f(0, 0, 1),  // Front face
                new Vector3f(0, -1, 0), // Bottom face
                new Vector3f(1, 0, 0),  // Right face
                new Vector3f(0, 0, -1)  // Back face
        };
        Integer[] faceIndices = new Integer[]{12, 18, 30, 24, 6, 0}; // Corresponding indices for each face
        Set<Integer> sameTypeFaces = new HashSet<>();

        for (int i = 0; i < neighborDirections.length; i++) {
            tempVector.set(this.getPos()).add(neighborDirections[i]);
            Entity neighbor = TestGame.world.get(tempVector);
            if (neighbor != null && neighbor.blockType.equals(this.blockType)) {
                sameTypeFaces.add(faceIndices[i]);
            }
        }
        return sameTypeFaces;
    }


    public boolean isNeighborSameType(Vector3f vector3f) {
        tempVector.set(this.getPos()).add(vector3f);
        Entity neighbor = TestGame.world.get(tempVector);
        return neighbor != null && neighbor.blockType.equals(this.blockType);

    }

    @Override
    public Entity clone() {
        try {
            Entity clone = (Entity) super.clone();
            // Ensure deep copy for mutable fields
            clone.pos = new Vector3f(this.pos);
            clone.rotation = new Vector3f(this.rotation);
            // For immutable fields or primitives, a shallow copy is sufficient
            // clone.scale = this.scale;
            // clone.blockType = this.blockType;
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError(); // Can never happen
        }
    }
}
