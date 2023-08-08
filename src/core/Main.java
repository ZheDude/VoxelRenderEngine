package core;

import static core.Utils.Consts.*;

public class Main {

    private static WindowManager window;
    private static TestGame game;

    public static void main(String[] args) {
        System.out.println("Hello world!");
        window = new WindowManager(TITLE, 1280, 720, true);
        game = new TestGame();
        EngineManager engine = new EngineManager();

        try{
            engine.start();
        } catch (Exception e){
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