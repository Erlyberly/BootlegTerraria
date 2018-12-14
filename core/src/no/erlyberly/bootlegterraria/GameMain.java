package no.erlyberly.bootlegterraria;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import no.erlyberly.bootlegterraria.console.ConsoleHandler;
import no.erlyberly.bootlegterraria.input.InputHandler;
import no.erlyberly.bootlegterraria.input.InputSetting;
import no.erlyberly.bootlegterraria.input.MouseInput;
import no.erlyberly.bootlegterraria.render.ui.UIController;
import no.erlyberly.bootlegterraria.util.CancellableThreadScheduler;
import no.erlyberly.bootlegterraria.util.Util;
import no.erlyberly.bootlegterraria.world.TiledGameMap;

import java.util.HashMap;
import java.util.Map;


public class GameMain extends Game {

    public static final CancellableThreadScheduler SECONDARY_THREAD = new CancellableThreadScheduler();
    private final Map<String, String> args;

    private static SpriteBatch batch;

    public static OrthographicCamera camera;
    public static OrthographicCamera hudCamera;
    public static TiledGameMap map;
    public static InputMultiplexer inputMultiplexer;
    public static InputHandler input;
    public static UIController ui;
    public static ConsoleHandler console;

    private static boolean headless;

    private static final String DEFAULT_MAP = "map.tmx";
    public static final String VERSION_FILE = "version";

    //For the interpreter to see it as a flag a dash(-) must be at the start of the argument
    //A start (*) means that a value is required, it is given as: -key=value

    //To specify that this instance is headless NOTE: currently broken, do not use
    private static final String HEADLESS_FLAG = "headless";
    //Execute the given file*
    private static final String AUTO_EXEC_FLAG = "exec";
    //Specify what map to load on startup, if none DEFAULT_MAP is loaded*
    private static final String MAP_FLAG = "map";
    //Make it easier to play on a laptop
    private static final String MOUSEPAD_FLAG = "laptop";
    //save current commit to 'version' file in assets
    private static final String SAVE_COMMIT_ID_FLAG = "saveID";
    //exit after create()
    private static final String EXIT_FLAG = "exit";

    public GameMain() {
        args = new HashMap<>();
    }

    public GameMain(final String[] args) {
        this.args = Util.interpreterArgs(args);
    }

    @Override
    public void create() {
        batch = new SpriteBatch();

        inputMultiplexer = new InputMultiplexer();

        camera = new OrthographicCamera();
        camera.setToOrtho(false, Gdx.graphics.getWidth() / 2f, Gdx.graphics.getHeight() / 2f);
        hudCamera = new OrthographicCamera();
        hudCamera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

//        camera.position.put(800f, 500f, 0); //Unlocked cam for debugging
        camera.update();

        console = new ConsoleHandler();
        console.setAlpha(0.75f);
        ui = new UIController();
        input = new InputHandler();


        Gdx.input.setInputProcessor(inputMultiplexer);

        //load map specified or default
        String map = args.get(MAP_FLAG);
        if (map == null) {
            console.log("No environment map specified, loading default map (" + DEFAULT_MAP + ")");
            map = DEFAULT_MAP;
        }
        loadMap(map);

        //check if this instance is headless
        headless = args.containsKey(HEADLESS_FLAG);


        // auto execute commands if specified
        final String fileName = args.get(AUTO_EXEC_FLAG);
        if (fileName != null) {
            console.log("Auto executing " + fileName);
            console.execCommand("exec " + fileName);
        }
        else {
            console.log("No auto executable file specified");
        }

        if (args.containsKey(MOUSEPAD_FLAG)) {
            //bind place_block LMB l-shift
            console.execCommand(
                "bind " + InputSetting.PLACE_BLOCK.name() + " " + MouseInput.toString(MouseInput.LEFT_MB) + " " +
                Input.Keys.toString(Input.Keys.SHIFT_LEFT));
        }

        if (args.containsKey(SAVE_COMMIT_ID_FLAG)) {
            final String commit = Util.getLastGitCommitID(true);
            Gdx.files.absolute(VERSION_FILE).writeString(commit, false);
        }

        if (args.containsKey(EXIT_FLAG)) {
            //force a clean exit
            System.exit(0);
        }
    }

    /**
     * Load an internal .tmx map
     *
     * @param map
     *     The map to load, with file ending
     */
    public static void loadMap(final String map) {
        SECONDARY_THREAD.cancelTasks();
        console.log("Loading map '" + map + '\'');
        if (GameMain.map != null) { GameMain.map.dispose(); }
        GameMain.map = new TiledGameMap(map, headless);
        GameMain.map.spawnPlayer();
        camera.position.set(GameMain.map.getPlayer().getPos(), 0);
    }

    @Override
    public void dispose() {
        batch.dispose();
        map.dispose();
        SECONDARY_THREAD.shutdown();
        ui.dispose();
    }

    @Override
    public void render() {
        batch.setProjectionMatrix(camera.combined);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        input.update();
        camera.update();
        map.update();
        map.render(camera, hudCamera, batch);

        ui.render();
        console.draw();
    }

    @Override
    public void resize(final int width, final int height) {
        super.resize(width, height);
        ui.resize(width, height);
    }

    public static boolean isHeadless() {
        return headless;
    }
}
