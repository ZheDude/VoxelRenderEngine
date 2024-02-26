package core.Utils;

import core.Camera;
import core.Entity.Entity;
import core.WindowManager;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.joml.Vector3fc;
import org.joml.Vector4f;

public class RayCasting {

    private Matrix4f inverseProjectionMatrix;
    private Matrix4f inverseViewMatrix;
    private Vector3f rayOrigin;
    private Vector3f rayDirection;

    float maxRange;

    public RayCasting(float maxRange) {
        inverseProjectionMatrix = new Matrix4f();
        inverseViewMatrix = new Matrix4f();
        rayOrigin = new Vector3f();
        rayDirection = new Vector3f();
        this.maxRange = maxRange;
    }

    public Vector3f calculateRay(WindowManager window, Camera camera) {
        float mouseX = (float) window.getWidth() / 2.0f;
        float mouseY = (float) window.getHeight() / 2.0f;


        Vector4f clipCoords = new Vector4f((2.0f * mouseX) / window.getWidth() - 1.0f, 1.0f - (2.0f * mouseY) / window.getHeight(), -1.0f, 1.0f);

        Vector4f eyeCoords = new Vector4f();
        inverseProjectionMatrix.transform(clipCoords, eyeCoords);

        Vector4f temp = new Vector4f();
        inverseViewMatrix.transform(eyeCoords, temp);
        Vector3f worldCoords = new Vector3f(temp.x, temp.y, temp.z);

        rayOrigin.set(camera.getPosition());
        rayDirection.set(worldCoords.x, worldCoords.y, worldCoords.z);

        // Rotate the ray direction by the camera's rotation
        rayDirection.rotateX((float) Math.toRadians(camera.getRotation().x));
        rayDirection.rotateY((float) Math.toRadians(camera.getRotation().y));
        rayDirection.rotateZ((float) Math.toRadians(camera.getRotation().z));
        rayDirection.normalize();
        rayDirection.x = -rayDirection.x;
        rayDirection.y = -rayDirection.y;
        return rayDirection;
    }

    public float isIntersecting(Vector3f ray, Entity entity) {
        Vector3f entityMinCorner = entity.getMinCorner();
        Vector3f entityMaxCorner = entity.getMaxCorner();

        Vector3f[] vertices = {
                new Vector3f(entityMinCorner.x, entityMinCorner.y, entityMinCorner.z),
                new Vector3f(entityMaxCorner.x, entityMinCorner.y, entityMinCorner.z),
                new Vector3f(entityMaxCorner.x, entityMaxCorner.y, entityMinCorner.z),
                new Vector3f(entityMinCorner.x, entityMaxCorner.y, entityMinCorner.z),
                new Vector3f(entityMinCorner.x, entityMinCorner.y, entityMaxCorner.z),
                new Vector3f(entityMaxCorner.x, entityMinCorner.y, entityMaxCorner.z),
                new Vector3f(entityMaxCorner.x, entityMaxCorner.y, entityMaxCorner.z),
                new Vector3f(entityMinCorner.x, entityMaxCorner.y, entityMaxCorner.z)
        };

        int[][] triangles = {
                {0, 1, 2}, {2, 3, 0}, // front face
                {4, 5, 6}, {6, 7, 4}, // back face
                {0, 1, 5}, {5, 4, 0}, // bottom face
                {2, 3, 7}, {7, 6, 2}, // top face
                {0, 3, 7}, {7, 4, 0}, // left face
                {1, 2, 6}, {6, 5, 1}  // right face
        };

        float closestT = Float.MAX_VALUE;

        for (int[] triangle : triangles) {
            Vector3f v0 = vertices[triangle[0]];
            Vector3f v1 = vertices[triangle[1]];
            Vector3f v2 = vertices[triangle[2]];

            Vector3f edge1 = new Vector3f();
            Vector3f edge2 = new Vector3f();
            Vector3f h = new Vector3f();
            Vector3f s = new Vector3f();
            Vector3f q = new Vector3f();

            v1.sub(v0, edge1);
            v2.sub(v0, edge2);

            ray.cross(edge2, h);
            float a = edge1.dot(h);

            if (a > -0.00001 && a < 0.00001) {
                continue; // This ray is parallel to this triangle
            }

            float f = 1.0f / a;
            rayOrigin.sub(v0, s);
            float u = f * s.dot(h);

            if (u < 0.0 || u > 1.0) {
                continue;
            }

            s.cross(edge1, q);
            float v = f * ray.dot(q);

            if (v < 0.0 || u + v > 1.0) {
                continue;
            }

            // At this stage we can compute t to find out where the intersection point is on the line
            float t = f * edge2.dot(q);

            if (t > 0.00001 && t < closestT) { // ray intersection
                closestT = t;
            }
        }

        return closestT == Float.MAX_VALUE ? -1 : closestT;
    }

    public Vector3f getRayOrigin() {
        return rayOrigin;
    }

    public Vector3f getRayDirection() {
        return rayDirection;
    }

    @Override
    public String toString() {
        return "RayCasting{" +
                "rayOrigin=" + rayOrigin +
                ", rayDirection=" + rayDirection +
                '}';
    }
}