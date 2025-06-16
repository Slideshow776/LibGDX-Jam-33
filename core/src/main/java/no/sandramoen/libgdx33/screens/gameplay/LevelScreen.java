package no.sandramoen.libgdx33.screens.gameplay;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import no.sandramoen.libgdx33.actors.player.Player;
import no.sandramoen.libgdx33.utils.BaseActor;
import no.sandramoen.libgdx33.utils.BaseGame;
import no.sandramoen.libgdx33.utils.BaseScreen;

public class LevelScreen extends BaseScreen {

    private Player player;


    @Override
    public void initialize() {}


    @Override
    public void update(float delta) {
        // background
        BaseActor sky_background = new BaseActor(0f, 0f, mainStage);
        sky_background.loadImage("parallax_backgrounds/-1");
        sky_background.setSize(BaseGame.WORLD_WIDTH + 2, BaseGame.WORLD_HEIGHT + 2);
        sky_background.setPosition(sky_background.getX() - 1, sky_background.getY() - 1);

        // characters
        player = new Player(BaseGame.WORLD_WIDTH / 2, 1, mainStage);
    }


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
