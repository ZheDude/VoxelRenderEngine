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

    public RayCasting() {
        inverseProjectionMatrix = new Matrix4f();
        inverseViewMatrix = new Matrix4f();
        rayOrigin = new Vector3f();
        rayDirection = new Vector3f();
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
//        rayDirection.normalize();
        rayDirection.x = -rayDirection.x;
        rayDirection.y = -rayDirection.y;
        return rayDirection;
    }

    public boolean isIntersecting(Vector3f ray, Entity entity) {
        Vector3f entityPosition = entity.getPos();
        float entityRadius = entity.getScale() / 2.0f ;

        Vector3f distance = new Vector3f();
        rayOrigin.sub(entityPosition, distance);

        float b = ray.dot(distance);
        float c = distance.lengthSquared() - entityRadius * entityRadius;
        float discriminant = b * b - c;

        return discriminant >= 0;
    }

    public String getRayOrigin() {
        return rayOrigin.x + " " + rayOrigin.y + " " + rayOrigin.z;
    }

    public String getRayDirection() {
        return rayDirection.x + " " + rayDirection.y + " " + rayDirection.z;
    }
}