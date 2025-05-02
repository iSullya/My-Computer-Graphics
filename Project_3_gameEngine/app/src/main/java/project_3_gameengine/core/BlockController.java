package project_3_gameengine.core;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector3i;
import org.joml.Vector4f;
import org.joml.Matrix4f;
import org.joml.Vector2d;

import project_3_gameengine.core.Camera;
import project_3_gameengine.core.WindowManager;
import project_3_gameengine.core.MouseInput;
import project_3_gameengine.core.ShaderManager;
import project_3_gameengine.core.entity.Entity;
import project_3_gameengine.core.entity.SceneManager;
import project_3_gameengine.core.entity.terrain.Terrain;
import project_3_gameengine.core.entity.Model;
import project_3_gameengine.core.utils.Transformation;

/**
 * block placement and removal logic, including ray creation.
 */
public class BlockController {
    private final Camera camera;
    private final WindowManager window;
    private final BlockWorld world;

    public BlockController(Camera camera,
                           WindowManager window,
                           BlockWorld world) {
        this.camera = camera;
        this.window = window;
        this.world  = world;
    }

    /**
     * Call each frame to process left-click (remove) and right-click (place).
     */
    public void handleInput(MouseInput mouse) {
        mouse.input();
        Ray ray = buildPickRay(mouse);

        // Remove block
        if (mouse.isLeftButtonPress()) {
            Vector3i hit = world.raycastBlock(ray);
            if (hit != null) {
                world.removeBlock(hit);
            }
        }
        // Place block
        if (mouse.isRightButtonPress()) {
            Vector3i hit = world.raycastBlock(ray);
            if (hit != null) {
                // stack directly on top
                Vector3i place = new Vector3i(hit.x, hit.y + 1, hit.z);
                world.placeBlock(place);
            } else {
                Vector3i onGround = world.raycastTerrain(ray);
                if (onGround != null) {
                    world.placeBlock(onGround);
                }
            }
        }
    }

    private Ray buildPickRay(MouseInput mouse) {
        Vector2d mp = mouse.getCurrentPos();
        Vector2f m = new Vector2f((float) mp.x, (float) mp.y);
        float ndcX = (2f * m.x) / window.getWidth() - 1f;
        float ndcY = 1f - (2f * m.y) / window.getHeight();
        Vector4f clip = new Vector4f(ndcX, ndcY, -1f, 1f);
        Matrix4f projInv = window.getProjectionMatrix().invert(new Matrix4f());
        Vector4f eye = projInv.transform(clip);
        eye.z = -1f; eye.w = 0f;
        Matrix4f viewInv = Transformation.getViewMatrix(camera).invert(new Matrix4f());
        Vector4f w4 = viewInv.transform(eye);
        Vector3f dir = new Vector3f(w4.x, w4.y, w4.z).normalize();
        return new Ray(camera.getPosition(), dir);
    }

    /**
     * BlockWorld: maintains a 3D occupancy grid and supports raycasts.
     */
    public static class BlockWorld {
        private final SceneManager scene;
        private final Model blockModel;
        private final Terrain terrain;
        private final boolean[][][] occupied;
        private final Map<Vector3i, Entity> blocks = new HashMap<>();

        public BlockWorld(SceneManager scene, Model blockModel, Terrain terrain) {
            this.scene      = scene;
            this.blockModel = blockModel;
            this.terrain    = terrain;
            int sizeX = (int) Terrain.SIZE;
            int sizeY = 128; // max world height
            int sizeZ = sizeX;
            occupied = new boolean[sizeX][sizeY][sizeZ];
        }

        /** Ray-voxel DDA to hit existing blocks */
        public Vector3i raycastBlock(Ray ray) {
            Vector3f origin = ray.getOrigin();
            Vector3f dir = ray.getDirection();
            int x = (int) Math.floor(origin.x);
            int y = (int) Math.floor(origin.y);
            int z = (int) Math.floor(origin.z);
            int stepX = dir.x > 0 ? 1 : -1;
            int stepY = dir.y > 0 ? 1 : -1;
            int stepZ = dir.z > 0 ? 1 : -1;
            float tMaxX = deltaT(origin.x, dir.x, x, stepX);
            float tMaxY = deltaT(origin.y, dir.y, y, stepY);
            float tMaxZ = deltaT(origin.z, dir.z, z, stepZ);
            float tDeltaX = Math.abs(1f / dir.x);
            float tDeltaY = Math.abs(1f / dir.y);
            float tDeltaZ = Math.abs(1f / dir.z);
            float t = 0f;
            float maxT = 100f;
            while (t <= maxT) {
                if (inBounds(x, y, z) && occupied[x][y][z]) {
                    return new Vector3i(x, y, z);
                }
                if (tMaxX < tMaxY) {
                    if (tMaxX < tMaxZ) {
                        x += stepX;
                        t = tMaxX;
                        tMaxX += tDeltaX;
                    } else {
                        z += stepZ;
                        t = tMaxZ;
                        tMaxZ += tDeltaZ;
                    }
                } else {
                    if (tMaxY < tMaxZ) {
                        y += stepY;
                        t = tMaxY;
                        tMaxY += tDeltaY;
                    } else {
                        z += stepZ;
                        t = tMaxZ;
                        tMaxZ += tDeltaZ;
                    }
                }
            }
            return null;
        }

        /** Stepped raycast to find terrain intersection */
        public Vector3i raycastTerrain(Ray ray) {
            Vector3f p = new Vector3f();
            float maxDistance = 100f;
            float stepSize = 0.2f;
            for (float t = 0; t < maxDistance; t += stepSize) {
                ray.getOrigin().fma(t, ray.getDirection(), p);
                int gx = (int) Math.floor(p.x);
                int gz = (int) Math.floor(p.z);
                if (!inBounds(gx, 0, gz)) continue;
                float h = terrain.getHeightAt(gx, gz);
                if (p.y <= h + 1 && p.y >= h) {
                    return new Vector3i(gx, (int) Math.floor(h) + 1, gz);
                }
            }
            return null;
        }

        public void placeBlock(Vector3i pos) {
            if (inBounds(pos.x, pos.y, pos.z) && !occupied[pos.x][pos.y][pos.z]) {
                occupied[pos.x][pos.y][pos.z] = true;
                Entity e = new Entity(blockModel,
                                      new Vector3f(pos.x, pos.y, pos.z),
                                      new Vector3f(0), 1f);
                scene.addEntity(e);
                blocks.put(new Vector3i(pos), e);
            }
        }

        public void removeBlock(Vector3i pos) {
            if (inBounds(pos.x, pos.y, pos.z) && occupied[pos.x][pos.y][pos.z]) {
                Entity e = blocks.remove(pos);
                if (e != null) {
                    scene.getEntities().remove(e);
                }
                occupied[pos.x][pos.y][pos.z] = false;
            }
        }

        private boolean inBounds(int x, int y, int z) {
            return x >= 0 && z >= 0 && y >= 0
                && x < occupied.length && z < occupied[0][0].length
                && y < occupied[0].length;
        }

        private float deltaT(float o, float d, int coord, int step) {
            float next = coord + (step > 0 ? 1 : 0);
            return (next - o) / d;
        }
    }

    private static class Ray {
        private final Vector3f origin, direction;
        public Ray(Vector3f o, Vector3f d) { origin = new Vector3f(o); direction = new Vector3f(d).normalize(); }
        public Vector3f getOrigin()    { return origin; }
        public Vector3f getDirection() { return direction; }
    }
}
