package core.Utils;

import BlockData.BlockType;
import BlockData.Cube;
import core.Entity.Entity;
import core.ObjectLoader;
import org.joml.Vector3f;

import java.util.*;

public class GreedyMeshing {


    public static List<Cube> generateMesh(Map<Vector3f, Entity> world, ObjectLoader loader) throws Exception {
        List<Cube> mesh = new ArrayList<>();
        Set<Vector3f> processed = new HashSet<>();
        List<Map.Entry<Vector3f, Entity>> entries = getEntries(world);

        for (Map.Entry<Vector3f, Entity> entry : entries) {
            Vector3f pos = entry.getKey();
            Entity entity = entry.getValue();

            if (processed.contains(pos)) continue;

            int width = expandInDirection(world, processed, pos, new Vector3f(1, 0, 0), entity.blockType);

            int height = expandInDirection(world, processed, pos, new Vector3f(0, 1, 0), entity.blockType);

            int depth = expandInDirection(world, processed, pos, new Vector3f(0, 0, 1), entity.blockType);

            Vector3f adjustedPos = new Vector3f(Math.floorDiv(width,2),Math.floorDiv(height,2),-Math.floorDiv(depth,2));

            Cube mergedCube = new Cube(loader, pos, new Vector3f(0, 0, 0), width, height, depth, entity.blockType);
            mesh.add(mergedCube);

            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    for (int z = 0; z < depth; z++) {
                        processed.add(new Vector3f(pos.x + x, pos.y + y, pos.z + z));
                    }
                }
            }
        }

        return mesh;
    }

    private static List<Map.Entry<Vector3f, Entity>> getEntries(Map<Vector3f, Entity> world) {
        List<Map.Entry<Vector3f, Entity>> entries = new ArrayList<>(world.entrySet());
        entries.sort(new Comparator<Map.Entry<Vector3f, Entity>>() {
            @Override
            public int compare(Map.Entry<Vector3f, Entity> o1, Map.Entry<Vector3f, Entity> o2) {
                Vector3f v1 = o1.getKey();
                Vector3f v2 = o2.getKey();
                if (v1.x != v2.x) return Float.compare(v1.x, v2.x);
                if (v1.y != v2.y) return Float.compare(v1.y, v2.y);
                return Float.compare(v1.z, v2.z);
            }
        });
        return entries;
    }

    private static int expandInDirection(Map<Vector3f, Entity> world, Set<Vector3f> processed, Vector3f startPos, Vector3f direction, BlockType blockType) {
        int length = 1;
        Vector3f checkPos = new Vector3f(startPos).add(direction);
        while (world.containsKey(checkPos) && !processed.contains(checkPos) && world.get(checkPos).blockType.equals(blockType)) {
            processed.add(new Vector3f(checkPos));
            length++;
            checkPos.add(direction);
        }
        return length;
    }
}
