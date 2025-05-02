package project_3_gameengine.test;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL11;

import project_3_gameengine.Launcher;
import project_3_gameengine.core.BlockController;
import project_3_gameengine.core.BlockController.BlockWorld;

import project_3_gameengine.core.Camera;
import project_3_gameengine.core.ILogic;
import project_3_gameengine.core.MouseInput;
import project_3_gameengine.core.ObjectLoader;
import project_3_gameengine.core.WindowManager;
import project_3_gameengine.core.entity.Entity;
import project_3_gameengine.core.entity.Material;
import project_3_gameengine.core.entity.Model;
import project_3_gameengine.core.entity.SceneManager;
import project_3_gameengine.core.entity.Texture;
import project_3_gameengine.core.entity.terrain.BlendMapTerrrain;
import project_3_gameengine.core.entity.terrain.Terrain;
import project_3_gameengine.core.entity.terrain.TerrainTexture;
import project_3_gameengine.core.lighting.DirectionalLight;
import project_3_gameengine.core.lighting.PointLight;
import project_3_gameengine.core.lighting.SpotLight;
import project_3_gameengine.core.rendering.RenderManager;
import project_3_gameengine.core.utils.Constants;

public class TestGame implements ILogic {



    private final RenderManager renderer;
    private final WindowManager window;
    private final ObjectLoader loader;
    private SceneManager sceneManager;
    private Camera camera;
    Vector3f cameraInc;
    private BlockController blockController;
    private BlockController.BlockWorld blockWorld;

    public TestGame(){
        renderer = new RenderManager();
        window = Launcher.getWindow();
        loader = new ObjectLoader();
        camera = new Camera(new Vector3f(234, 132,  390), new Vector3f(27,  0,   0));
        cameraInc = new Vector3f(0, 0, 0);
        sceneManager = new SceneManager(-90);
    }

    @Override
    public void init() throws Exception {
       renderer.init();

       Model model = loader.loadOBJModel("/models/cube3.obj");
        model.setTexture(new Texture(loader.loadTexture("textures/dirt.png")), 1f);
        
        

        TerrainTexture backgroundTexture = new TerrainTexture(loader.loadTexture("textures/terrain.png"));
        TerrainTexture redTexture = new TerrainTexture(loader.loadTexture("textures/flowers.png"));
        TerrainTexture greenTexture = new TerrainTexture(loader.loadTexture("textures/stone.png"));
        TerrainTexture blueTexture = new TerrainTexture(loader.loadTexture("textures/dirt.png"));
        TerrainTexture blendMap = new TerrainTexture(loader.loadTexture("textures/blendMap2.png"));

        BlendMapTerrrain blendMapTerrrain = new BlendMapTerrrain(backgroundTexture, redTexture, greenTexture, blueTexture);

       
       Terrain terrain = new Terrain(new Vector3f(0, 1, -200), loader, new Material(new Vector4f(0.0f, 0.0f, 0.0f, 0.0f), 0.1f), blendMapTerrrain, blendMap);
       //Terrain terrain2 = new Terrain(new Vector3f(-800, 1, -800), loader, new Material(new Vector4f(0.0f, 0.0f, 0.0f, 0.0f), 0.1f), blendMapTerrrain, blendMap);
       sceneManager.addTerrain(terrain); 
       //sceneManager.addTerrain(terrain2);
       blockWorld = new BlockController.BlockWorld(sceneManager, model, terrain);
       blockController = new BlockController(camera, window, blockWorld);
       Random rnd = new Random();

       /*for (int i = 0; i < 2000; i++) {
            float x = rnd.nextFloat() * 800;
            float z = rnd.nextFloat() * -800;
            sceneManager.addEntity(new Entity(model, new Vector3f(x,2,z),
                    new Vector3f(0, 0, 0), 1));
        }

        sceneManager.addEntity(new Entity(model, new Vector3f(0, 2, -5), new Vector3f(0, 0, 0), 1));*/

        
        float lightIntensity = 1.0f;
        //Point Light
        Vector3f lightPosition = new Vector3f(-0.5f, -0.5f, -3.2f);
        Vector3f lightColour = new Vector3f(1, 1, 1);
        PointLight pointLight = new PointLight(lightColour, lightPosition, lightIntensity, 0, 0, 1);
        
        //Spot light
        

        // Directional light
        lightIntensity = 1f;
        lightPosition = new Vector3f(-1, 0, 0);
        lightColour = new Vector3f(1, 1, 1);
        Vector3f lightDir = new Vector3f(-0.5f, -1.0f, -0.2f).normalize(); 
        sceneManager.setDirectionalLight(new DirectionalLight(lightColour, lightDir, lightIntensity));
        

        sceneManager.setPointLights(new PointLight[]{pointLight});
    }

    @Override
    public void input() {
        cameraInc.set(0, 0, 0);
        if (window.isKeyPressed(GLFW.GLFW_KEY_W)) {
            cameraInc.z = -1;
        }
        if (window.isKeyPressed(GLFW.GLFW_KEY_S)) {
            cameraInc.z = 1;
        }
        if (window.isKeyPressed(GLFW.GLFW_KEY_A)) {
            cameraInc.x = -1;
        }
        if (window.isKeyPressed(GLFW.GLFW_KEY_D)) {
            cameraInc.x = 1;
        }
        if (window.isKeyPressed(GLFW.GLFW_KEY_Z)) {
            cameraInc.y = -1;
        }
        if (window.isKeyPressed(GLFW.GLFW_KEY_X)) {
            cameraInc.y = 1;
        }
        if (window.isKeyPressed(GLFW.GLFW_KEY_Y)) {
            System.out.println(camera.getPosition() + "         "+ camera.getRotation());
        }

        // if (window.isKeyPressed(GLFW.GLFW_KEY_O)) {
        //     pointLight.getPosition().x += 0.1f;
        // }
        // if (window.isKeyPressed(GLFW.GLFW_KEY_P)) {
        //     pointLight.getPosition().x -= 0.1f;
        // }

    }

    @Override
    public void update(float interval, MouseInput mouseInput) {
        
        camera.movePosition(cameraInc.x * Constants.CAMERA_MOVE_SPEED, cameraInc.y * Constants.CAMERA_MOVE_SPEED, cameraInc.z * Constants.CAMERA_MOVE_SPEED);
        // camera.movePosition(cameraInc.x * Constants.CAMERA_STEP, cameraInc.y * Constants.CAMERA_STEP, cameraInc.z * Constants.CAMERA_STEP);

        
        Vector2f rotVec = mouseInput.getDisplVec();
        camera.moveRotation(rotVec.x * Constants.MOUSE_SENSITIVITY, rotVec.y * Constants.MOUSE_SENSITIVITY, 0);

        mouseInput.input();
        blockController.handleInput(mouseInput);

        

        for (Entity entity : sceneManager.getEntities()) {
            renderer.processEntity(entity);
        }
        for (Terrain terrain : sceneManager.getTerrains()) {
            renderer.processTerrain(terrain);
        }
    }

    @Override
    public void render() {


        window.setClearColour(0.6f, 0.8f, 1.0f, 0.0f);

        renderer.render(camera, sceneManager);
    }

    @Override
    public void cleanup() {
        renderer.cleanup();
        loader.cleanup();
    }
    
}
