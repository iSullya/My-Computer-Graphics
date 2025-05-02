package project_3_gameengine.core.entity.terrain;

import org.joml.Vector3f;

import project_3_gameengine.core.ObjectLoader;
import project_3_gameengine.core.entity.Material;
import project_3_gameengine.core.entity.Model;
import project_3_gameengine.core.entity.Texture;

public class Terrain {
    
    public static final float SIZE = 500;
    private static final int VERTEX_COUNT = 128;

    private Vector3f position;
    private Model model;
    private TerrainTexture blendMap;
    private BlendMapTerrrain blendMapTerrrain;
    private float[][] heightMap = new float[VERTEX_COUNT][VERTEX_COUNT];

    
    public Terrain(Vector3f position, ObjectLoader loader, Material material, BlendMapTerrrain blendMapTerrrain, TerrainTexture blendMap){
        this.position = position;
        this.model = generateTerrain(loader);
        this.model.setMaterial(material);
        this.blendMapTerrrain = blendMapTerrrain;
        this.blendMap = blendMap;
    }

    private Model generateTerrain(ObjectLoader loader){
        int count = VERTEX_COUNT * VERTEX_COUNT;
        float[] vertices = new float[count * 3];
        float[] normals = new float[count * 3];
        float[] textureCoords = new float[count * 2];
        int[] indices = new int[6 * (VERTEX_COUNT - 1) * (VERTEX_COUNT - 1)];
        int vertexPointer = 0;
        
        for (int i = 0; i < VERTEX_COUNT; i++) {
            for (int j = 0; j < VERTEX_COUNT; j++) {
                vertices[vertexPointer * 3] = j / (VERTEX_COUNT - 1.0f) * SIZE;
                vertices[vertexPointer * 3 + 1] = 0;
                heightMap[i][j] = vertices[vertexPointer * 3 + 1];
                vertices[vertexPointer * 3 + 2] = i / (VERTEX_COUNT - 1.0f) * SIZE;
                normals[vertexPointer * 3] = 0;
                normals[vertexPointer * 3 + 1] = 1;
                normals[vertexPointer * 3 + 2] = 0;
                textureCoords[vertexPointer * 2] = j / (VERTEX_COUNT - 1.0f);
                textureCoords[vertexPointer * 2 + 1] = i / (VERTEX_COUNT - 1.0f);
                vertexPointer++;
            }
        }
        int pointer = 0;
        for (int z = 0; z < VERTEX_COUNT - 1.0f; z++) {
            for (int x = 0; x < VERTEX_COUNT - 1.0f; x++){
                int topLeft = (z * VERTEX_COUNT) + x;
                int topRight = topLeft + 1;
                int bottomLeft = ((z + 1) * VERTEX_COUNT) + x;
                int bottomRight = bottomLeft + 1;
                indices[pointer++] = topLeft;
                indices[pointer++] = bottomLeft;
                indices[pointer++] = topRight;
                indices[pointer++] = topRight;
                indices[pointer++] = bottomLeft;
                indices[pointer++] = bottomRight;
            }
        }
        return loader.loadModel(vertices, textureCoords, normals, indices);
    }

    public Vector3f getPosition() {
        return position;
    }

    public Model getModel() {
        return model;
    }

    public Material getMaterial(){
        return model.getMaterial();
    }

    public Texture getTexture(){
        return model.getTexture();
    }

    public TerrainTexture getBlendMap() {
        return blendMap;
    }

    public BlendMapTerrrain getBlendMapTerrrain() {
        return blendMapTerrrain;
    }

    public float getHeightAt(int worldX, int worldZ) {
        float gridSize = SIZE / (VERTEX_COUNT - 1);
        int xi = Math.floorDiv(worldX, (int)gridSize);
        int zi = Math.floorDiv(worldZ, (int)gridSize);
        xi = Math.max(0, Math.min(xi, VERTEX_COUNT - 1));
        zi = Math.max(0, Math.min(zi, VERTEX_COUNT - 1));
        return heightMap[zi][xi];
    }
}
