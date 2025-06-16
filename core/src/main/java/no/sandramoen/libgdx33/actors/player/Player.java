package no.sandramoen.libgdx33.actors.player;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Align;
import no.sandramoen.libgdx33.utils.BaseActor;


public class Player extends BaseActor {


    public Player(float x, float y, Stage s) {
        super(x, y, s);
        loadImage("player/player");

        // body
        setSize(4, 8);
        centerAtPosition(x, y);
        setOrigin(Align.center);
    }


    @Override
    public void act(float delta) {
        super.act(delta);
    }
}
