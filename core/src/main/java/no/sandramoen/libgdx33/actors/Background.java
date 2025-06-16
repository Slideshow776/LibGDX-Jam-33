package no.sandramoen.libgdx33.actors;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Stage;

import no.sandramoen.libgdx33.utils.BaseActor;
import no.sandramoen.libgdx33.utils.BaseGame;

public class Background extends BaseActor {

    public Background(float x, float y, Stage stage) {
        super(x, y, stage);

        loadImage("whitePixel");
        setSize(BaseGame.WORLD_WIDTH + 2, BaseGame.WORLD_HEIGHT + 2);
        setPosition(-1, -1);
        setColor(Color.DARK_GRAY);
    }
}
