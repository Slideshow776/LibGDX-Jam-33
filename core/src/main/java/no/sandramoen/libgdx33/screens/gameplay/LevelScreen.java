package no.sandramoen.libgdx33.screens.gameplay;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.utils.Array;

import no.sandramoen.libgdx33.actors.Background;
import no.sandramoen.libgdx33.actors.Enemy;
import no.sandramoen.libgdx33.actors.Player;
import no.sandramoen.libgdx33.utils.BaseGame;
import no.sandramoen.libgdx33.utils.BaseScreen;

public class LevelScreen extends BaseScreen {

    private Player player;
    private Array<Enemy> enemies;


    @Override
    public void initialize() {
        // background
        new Background(0f, 0f, mainStage);

        // characters
        player = new Player(BaseGame.WORLD_WIDTH / 2, BaseGame.WORLD_HEIGHT / 2, mainStage);

        enemies = new Array<Enemy>();
        for(int i = 0; i < 10; i++)
            enemies.add(new Enemy(BaseGame.WORLD_WIDTH / 2, BaseGame.WORLD_HEIGHT / 2, mainStage));
    }


    @Override
    public void update(float delta) {}


    @Override
    public boolean keyDown(int keycode) {
        if (keycode == Keys.ESCAPE || keycode == Keys.Q)
            Gdx.app.exit();
        else if (keycode == Keys.R)
            BaseGame.setActiveScreen(new LevelScreen());
        return super.keyDown(keycode);
    }


    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) { // 0 for left, 1 for right
        System.out.println("touch down");
        return super.touchDown(screenX, screenY, pointer, button);
    }
}
