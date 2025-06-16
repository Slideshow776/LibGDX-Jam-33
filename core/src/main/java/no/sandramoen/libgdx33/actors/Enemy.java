package no.sandramoen.libgdx33.actors;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Align;

import no.sandramoen.libgdx33.utils.BaseActor;


public class Enemy extends BaseActor {


    public Enemy(float x, float y, Stage s) {
        super(x, y, s);
        loadImage("enemy/enemy");

        setSize(1, 2);
        centerAtPosition(x, y);
        setOrigin(Align.center);
        //setDebug(true);
    }


    @Override
    public void act(float delta) {
        super.act(delta);
    }

}
