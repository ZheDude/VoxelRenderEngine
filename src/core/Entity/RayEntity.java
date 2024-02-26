package core.Entity;

import core.ObjectLoader;
import org.joml.Vector3f;

public class RayEntity {

    private Vector3f origin;
    private Vector3f direction;

    public float maxRange;

    public RayEntity(ObjectLoader loader, Vector3f pos, Vector3f rotation, float maxRange) {
        this.origin = new Vector3f(pos);
        this.direction = rotation;
        this.maxRange = maxRange;
    }

    public Vector3f getOrigin() {
        return origin;
    }

    public void setOrigin(Vector3f origin) {
        this.origin = origin;
    }

    public Vector3f getDirection() {
        return direction;
    }

    public void setDirection(Vector3f direction) {
        this.direction = direction;
    }
}