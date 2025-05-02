package project_3_gameengine.core;

public interface ILogic {
    
    void init() throws Exception;
    
    void input();

    void update(float interval, MouseInput mouseInput);

    void render();
    
    void cleanup();
}
