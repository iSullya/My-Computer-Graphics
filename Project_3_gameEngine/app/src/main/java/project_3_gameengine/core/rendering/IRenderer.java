package project_3_gameengine.core.rendering;

import project_3_gameengine.core.Camera;
import project_3_gameengine.core.entity.Model;
import project_3_gameengine.core.lighting.DirectionalLight;
import project_3_gameengine.core.lighting.PointLight;
import project_3_gameengine.core.lighting.SpotLight;

public interface IRenderer<T> {
    
    public void init() throws Exception;

    public void render(Camera camera, PointLight[] pointLights, SpotLight[] spotLights, 
                        DirectionalLight directionalLight);
    
    abstract void bind(Model model);

    public void unbind();

    public void prepare(T t, Camera camera);

    public void cleanup();
}
