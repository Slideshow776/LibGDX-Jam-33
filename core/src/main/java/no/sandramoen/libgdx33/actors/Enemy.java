package no.sandramoen.libgdx33.actors;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.utils.Align;

import no.sandramoen.libgdx33.actors.particles.EffectEnemyMovement;
import no.sandramoen.libgdx33.utils.BaseActor;
import no.sandramoen.libgdx33.utils.BaseGame;


public class Enemy extends BaseActor {

    private static final float MIN_MOVEMENT_SPEED = 2f;
    private static final float MAX_MOVEMENT_SPEED = 8f;

    private float movementSpeed = 10f;
    private float movementAcceleration = movementSpeed * 10f;
    private float angle = MathUtils.random(0f, 360f);


    public Enemy(Stage s) {
        super(0f, 0f, s);

        loadImage("cat_normal");
        //setColor(Color.FIREBRICK);
        //setDebug(true);

        // body
        float width = MathUtils.random(0.75f, 1.25f);
        float height = 2 * width;
        setSize(width, height);
        //setDebug(true);
        setOrigin(Align.center);
        setBoundaryPolygon(8, 0.5f);

        // spawn
        reset();

        float duration = 2.5f * 1 / movementSpeed;
        //System.out.println("movement speed: " + movementSpeed + ", duration: " + duration);
        addAction(Actions.forever(Actions.sequence(
            Actions.delay(duration),
            Actions.run(this::flip),
            Actions.delay(duration),
            Actions.run(this::flip)
        )));

        EffectEnemyMovement effect = new EffectEnemyMovement();
        effect.setScale(0.005f);
        effect.setPosition((getWidth() / 2) - 0.2f, getHeight() * 0.5f);
        effect.start();
        addActor(effect);
    }


    @Override
    public void act(float delta) {
        if (pause) return;
        super.act(delta);

        accelerateAtAngle(angle);
        applyPhysics(delta);

        setRotation(getMotionAngle() - 90f);

        if (
            Math.abs(getX()) > BaseGame.WORLD_WIDTH * 1.2f ||
            Math.abs(getY()) > BaseGame.WORLD_HEIGHT * 1.2f
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
