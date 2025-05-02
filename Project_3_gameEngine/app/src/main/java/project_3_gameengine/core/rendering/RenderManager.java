package project_3_gameengine.core.rendering;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL13;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;

import project_3_gameengine.Launcher;
import project_3_gameengine.core.Camera;
import project_3_gameengine.core.ShaderManager;
import project_3_gameengine.core.WindowManager;
import project_3_gameengine.core.entity.Entity;
import project_3_gameengine.core.entity.Model;
import project_3_gameengine.core.entity.SceneManager;
import project_3_gameengine.core.entity.terrain.Terrain;
import project_3_gameengine.core.lighting.DirectionalLight;
import project_3_gameengine.core.lighting.PointLight;
import project_3_gameengine.core.lighting.SpotLight;
import project_3_gameengine.core.utils.Constants;
import project_3_gameengine.core.utils.Transformation;
import project_3_gameengine.core.utils.Utils;

public class RenderManager {
    
    private final WindowManager window;
    private EntityRender entityRenderer;
    private TerrainRenderer terrainRenderer;

    public RenderManager() {
        window = Launcher.getWindow();
    }

    public void init() throws Exception{
        entityRenderer = new EntityRender();
        terrainRenderer = new TerrainRenderer();
        entityRenderer.init();
        terrainRenderer.init();
    }


    public static void renderLights(PointLight[] pointLights, SpotLight[] spotLights,
                                DirectionalLight directionalLight, ShaderManager shader){
        shader.setUniform("ambientLight", Constants.AMBIENT_LIGHT);
        shader.setUniform("specularPower", Constants.SPECULAR_POWER);

        int numLights = spotLights != null ? spotLights.length : 0;
        for (int i = 0; i < numLights; i++) {
            shader.setUniform("spotLights", spotLights[i], i);
        }
        numLights = pointLights != null ? pointLights.length : 0;
        for (int i = 0; i < numLights; i++) {
            shader.setUniform("pointLights", pointLights[i], i);
        }
        shader.setUniform("directionalLight", directionalLight);
    }

    public void render(Camera camera, SceneManager scene){
        clear();

        if (window.isResize()) {
            GL11.glViewport(0, 0, window.getWidth(), window.getHeight());
            window.setResize(false);
        }
        entityRenderer.render(camera, scene.getPointLights(), scene.getSpotLights(), scene.getDirectionalLight());
        terrainRenderer.render(camera, scene.getPointLights(), scene.getSpotLights(), scene.getDirectionalLight());
    }

    public void processEntity(Entity entity){
        List<Entity> entityList = entityRenderer.getEntities().get(entity.getModel());
        if (entityList != null) {
            entityList.add(entity);
        } else {
            List<Entity> newEntityList = new ArrayList<>();
            newEntityList.add(entity);
            entityRenderer.getEntities().put(entity.getModel(), newEntityList);
        }
    }

    public void processTerrain(Terrain terrain){
        terrainRenderer.getTerrain().add(terrain);
    }

    public void clear(){
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
    }

    public void cleanup(){
        entityRenderer.cleanup();
        terrainRenderer.cleanup();
    }

    
}
