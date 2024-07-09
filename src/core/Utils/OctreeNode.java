package core.Utils;

import core.Entity.Entity;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OctreeNode {
    private final Vector3f minCorner;
    private final Vector3f maxCorner;
    private Map<Vector3f, Entity> entityMap = new HashMap<>();
    private final OctreeNode[] children;

    private final int  maxRange = 5; // Adjust this value as needed

    private static final int MAX_ENTITIES_PER_NODE = 10; // Adjust this value as needed

    public OctreeNode(Vector3f minCorner, Vector3f maxCorner, Map<Vector3f, Entity> entityMap) {
        this.minCorner = minCorner;
        this.maxCorner = maxCorner;
        this.entityMap = entityMap;
        this.children = new OctreeNode[8];
    }

    public void subdivide() {
        // Compute the midpoint of the cube
        Vector3f midpoint = new Vector3f(
                (minCorner.x + maxCorner.x) / 2,
                (minCorner.y + maxCorner.y) / 2,
                (minCorner.z + maxCorner.z) / 2
        );

        // Create the eight children
        for (int i = 0; i < 8; i++) {
            Vector3f childMinCorner = new Vector3f(
                    (i & 1) == 0 ? minCorner.x : midpoint.x,
                    (i & 2) == 0 ? minCorner.y : midpoint.y,
                    (i & 4) == 0 ? minCorner.z : midpoint.z
            );
            Vector3f childMaxCorner = new Vector3f(
                    (i & 1) == 0 ? midpoint.x : maxCorner.x,
                    (i & 2) == 0 ? midpoint.y : maxCorner.y,
                    (i & 4) == 0 ? midpoint.z : maxCorner.z
            );

            // Filter the entities that intersect the child's cube
            Map<Vector3f, Entity> childEntities = new HashMap<>();
            for (Entity entity : entityMap.values()) {
                if (entity.intersectsCube(childMinCorner, childMaxCorner)) {
                    childEntities.put(entity.getPos(), entity);
                }
            }

            children[i] = new OctreeNode(childMinCorner, childMaxCorner, childEntities);
            if (childEntities.size() > MAX_ENTITIES_PER_NODE) {
                children[i].subdivide();
            }
        }

        // Clear the entities from this node
        entityMap.clear();
    }

    public Entity intersectRay(Vector3f rayOrigin, Vector3f rayDirection) {
        if (!intersectsRay(rayOrigin, rayDirection)) {
            return null;
        }

        Entity closestEntity = null;
        float closestDistance = Float.MAX_VALUE;

        for (Entity entity : entityMap.values()) {
            float distance = entity.intersectRay(rayOrigin, rayDirection);
            if (distance >= 0 && distance < closestDistance && distance < maxRange) {
                closestEntity = entity;
                closestDistance = distance;
            }
        }

        for (OctreeNode child : children) {
            if (child != null) {
                Entity childEntity = child.intersectRay(rayOrigin, rayDirection);
                if (childEntity != null) {
                    float childDistance = childEntity.getDistanceTo(rayOrigin);
                    if (childDistance < closestDistance) {
                        closestEntity = childEntity;
                        closestDistance = childDistance;
                    }
                }
            }
        }

        return closestEntity;
    }

    // In OctreeNode.java
    public boolean intersectsRay(Vector3f rayOrigin, Vector3f rayDirection) {
        float tmin = (minCorner.x - rayOrigin.x) / rayDirection.x;
        float tmax = (maxCorner.x - rayOrigin.x) / rayDirection.x;

        if (tmin > tmax) {
            float temp = tmin;
            tmin = tmax;
            tmax = temp;
        }

        float tymin = (minCorner.y - rayOrigin.y) / rayDirection.y;
        float tymax = (maxCorner.y - rayOrigin.y) / rayDirection.y;

        if (tymin > tymax) {
            float temp = tymin;
            tymin = tymax;
            tymax = temp;
        }

        if ((tmin > tymax) || (tymin > tmax)) {
            return false;
        }

        if (tymin > tmin) {
            tmin = tymin;
        }

        if (tymax < tmax) {
            tmax = tymax;
        }

        float tzmin = (minCorner.z - rayOrigin.z) / rayDirection.z;
        float tzmax = (maxCorner.z - rayOrigin.z) / rayDirection.z;

        if (tzmin > tzmax) {
            float temp = tzmin;
            tzmin = tzmax;
            tzmax = temp;
        }

        if ((tmin > tzmax) || (tzmin > tmax)) {
            return false;
        }

        if (tzmin > tmin) {
            tmin = tzmin;
        }

        if (tzmax < tmax) {
            tmax = tzmax;
        }

        return true;
    }

    public void addEntity(Entity entity) {
        entityMap.put(entity.getPos(), entity);
    }

    public Entity getEntity(Vector3f pos) {
        return entityMap.get(pos);
    }
}