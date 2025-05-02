package project_3_gameengine;

import org.lwjgl.Version;

import project_3_gameengine.core.EngineManager;
import project_3_gameengine.core.WindowManager;
import project_3_gameengine.core.utils.Constants;
import project_3_gameengine.test.TestGame;

public class Launcher {

    private static WindowManager window;
    private static TestGame game;

    public static void main(String[] args) {
        //System.out.println(Version.getVersion());
        window = new WindowManager(Constants.TITLE, 1600, 900, false);
        game = new TestGame();
        EngineManager engine = new EngineManager();
        try {
            engine.start();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static WindowManager getWindow() {
        return window;
    }

    public static TestGame getGame() {
        return game;
    }

    
}
