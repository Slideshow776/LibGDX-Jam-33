package no.sandramoen.libgdx33.actors;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Align;

import no.sandramoen.libgdx33.utils.BaseActor;
import no.sandramoen.libgdx33.utils.BaseGame;


public class Enemy extends BaseActor {

    private static final float MIN_MOVEMENT_SPEED = 2.5f;
    private static final float MAX_MOVEMENT_SPEED = 10f;

    private float movementSpeed = 10f;
    private float movementAcceleration = movementSpeed * 10f;
    private float angle = MathUtils.random(0f, 360f);


    public Enemy(float x, float y, Stage s) {
        super(x, y, s);
        loadImage("yellow_triangle");

        // body
        setSize(1, 1);
        centerAtPosition(x, y);
        setOrigin(Align.center);

        // spawn
        reset();
    }


    @Override
    public void act(float delta) {
        super.act(delta);
        accelerateAtAngle(angle);
        applyPhysics(delta);

        setRotation(getMotionAngle() - 90f);

        if (
            Math.abs(getX()) > BaseGame.WORLD_WIDTH * 2 ||
            Math.abs(getY()) > BaseGame.WORLD_HEIGHT * 2
        ) {
            reset();
        }
    }


    private void reset() {
        setPositionAtEdge();

        // Randomize movement speed
        movementSpeed = MathUtils.random(MIN_MOVEMENT_SPEED, MAX_MOVEMENT_SPEED);
        movementAcceleration = movementSpeed * 10f;

        setMaxSpeed(movementSpeed);
        setAcceleration(movementAcceleration);
        setDeceleration(movementAcceleration);
    }


    private void setPositionAtEdge() {
        int side = MathUtils.random(0, 3);
        float x;
        float y;
        float start_offset = 2;

        if (side == 0) { // left
            x = -start_offset;
            y = MathUtils.random(-start_offset, BaseGame.WORLD_HEIGHT + start_offset);
            angle = MathUtils.random(-45f, 45f);
        } else if (side == 1) { // right
            x = BaseGame.WORLD_WIDTH + start_offset;
            y = MathUtils.random(-start_offset, BaseGame.WORLD_HEIGHT + start_offset);
            angle = MathUtils.random(135f, 225f);
        } else if (side == 2) { // bottom
            x = MathUtils.random(-start_offset, BaseGame.WORLD_WIDTH + start_offset);
            y = -start_offset;
            angle = MathUtils.random(45f, 135f);
        } else { // top
            x = MathUtils.random(-start_offset, BaseGame.WORLD_WIDTH + start_offset);
            y = BaseGame.WORLD_HEIGHT + start_offset;
            angle = MathUtils.random(225f, 315f);
        }
        setPosition(x, y);
    }


}
