package no.sandramoen.libgdx33.actors;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Align;
import no.sandramoen.libgdx33.utils.BaseActor;
import no.sandramoen.libgdx33.utils.BaseGame;


public class Player extends BaseActor {

    private float movementSpeed = 10f;
    private float movementAcceleration = movementSpeed * 10f;


    public Player(float x, float y, Stage s) {
        super(x, y, s);
        loadImage("blue_circle");

        // body
        setSize(1, 1);
        centerAtPosition(x, y);
        setOrigin(Align.center);

        setWorldBounds(BaseGame.WORLD_WIDTH + 0.5f, BaseGame.WORLD_HEIGHT);

        // movement
        setAcceleration(movementAcceleration);
        setMaxSpeed(movementSpeed);
        setDeceleration(movementAcceleration);
    }


    @Override
    public void act(float delta) {
        super.act(delta);

        // poll keyboard
        if (Gdx.input.isKeyPressed(Keys.W) || Gdx.input.isKeyPressed(Keys.UP))
            accelerateAtAngle(90f);
        if (Gdx.input.isKeyPressed(Keys.A) || Gdx.input.isKeyPressed(Keys.LEFT))
            accelerateAtAngle(180f);
        if (Gdx.input.isKeyPressed(Keys.S) || Gdx.input.isKeyPressed(Keys.DOWN))
            accelerateAtAngle(270f);
        if (Gdx.input.isKeyPressed(Keys.D) || Gdx.input.isKeyPressed(Keys.RIGHT))
            accelerateAtAngle(0f);

        applyPhysics(delta);
        boundToWorld();
    }


    public boolean isMoving() {
        return getSpeed() > 0.1f; // small threshold to avoid tiny jitter counts as moving
    }

}
