package no.sandramoen.libgdx33.utils;

import static java.lang.Math.abs;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Array;

public class BaseActor extends Group {

    protected static Rectangle worldBounds;
    private static int ID_COUNTER = 0;

    public Animation<TextureRegion> animation;
    private float animationTime = 0f;
    private boolean animationPaused = false;

    private Vector2 velocityVec = new Vector2(0f, 0f);
    private Vector2 accelerationVec = new Vector2(0f, 0f);
    private float acceleration = 0f;
    private float maxSpeed = 1000f;
    private float deceleration = 0f;
    private Polygon boundaryPolygon = null;

    public boolean isFacingRight = true;
    public boolean pause = false;
    public float animationWidth = getWidth();
    public float animationHeight = getWidth();
    public boolean isCollisionEnabled = true;
    public float shakyCamIntensity = .5f;
    public boolean isShakyCam = false;

    public final int ID = (ID_COUNTER++ % 9000) + 1000;
    private static final com.badlogic.gdx.graphics.glutils.ShapeRenderer shapeRenderer = new com.badlogic.gdx.graphics.glutils.ShapeRenderer();

    public BaseActor(float x, float y, Stage stage) {
        super();

        setPosition(x, y);
        stage.addActor(this);

        animation = null;
        animationTime = 0;
        animationPaused = false;

        velocityVec = new Vector2(0, 0);
        accelerationVec = new Vector2(0, 0);
        acceleration = 0;
        maxSpeed = 1000;
        deceleration = 0;

        boundaryPolygon = null;
        /*setDebug(true);*/
    }

    @Override
    public void setSize(float width, float height) {
        super.setSize(width, height);
        setAnimationSize(width, height);
    }

    @Override
    public void act(float delta) {
        if (!pause)
            super.act(delta);

        if (!animationPaused)
            animationTime += delta;

        if (isShakyCam)
            shakeCamera();
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        //  apply color tint effect
        Color c = getColor();
        batch.setColor(c.r, c.g, c.b, c.a);

        if (animation != null && isVisible()) {
            if (isFacingRight)
                batch.draw(
                        animation.getKeyFrame(animationTime),
                        getX() + abs(getWidth() - animationWidth) / 2,
                        getY() + abs(getHeight() - animationHeight) / 2,
                        getOriginX(),
                        getOriginY(),
                        animationWidth,
                        animationHeight,
                        getScaleX(),
                        getScaleY(),
                        getRotation()
                );
            else
                batch.draw(
                        animation.getKeyFrame(animationTime),
                        getX() + getWidth(),
                        getY(),
                        getOriginX() - getWidth(),
                        getOriginY(),
                        -getWidth(),
                        getHeight(),
                        getScaleX(),
                        getScaleY(),
                        getRotation()
                );
        }
        super.draw(batch, parentAlpha);

        // Draw collision polygon if debug enabled
        if (boundaryPolygon != null && getDebug()) {
            batch.end();  // end sprite batch before using ShapeRenderer

            shapeRenderer.setProjectionMatrix(batch.getProjectionMatrix());
            shapeRenderer.setTransformMatrix(batch.getTransformMatrix());
            shapeRenderer.begin(com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType.Line);
            shapeRenderer.setColor(Color.RED);

            Polygon poly = getBoundaryPolygon();
            float[] vertices = poly.getTransformedVertices();
            int n = vertices.length / 2;

            for (int i = 0; i < n; i++) {
                float x1 = vertices[i * 2];
                float y1 = vertices[i * 2 + 1];
                float x2 = vertices[((i + 1) % n) * 2];
                float y2 = vertices[((i + 1) % n) * 2 + 1];
                shapeRenderer.line(x1, y1, x2, y2);
            }

            shapeRenderer.end();

            batch.begin();  // restart sprite batch for other drawing
        }
    }

    protected void setAnimationSize(float width, float height) {
        animationWidth = width;
        animationHeight = height;
    }

    public void flip() {
        isFacingRight = !isFacingRight;
    }

    public void setAnimationPaused(Boolean pause) {
        animationPaused = pause;
    }

    public Boolean isAnimationFinished() {
        return animation.isAnimationFinished(animationTime);
    }

    public Animation<TextureRegion> loadTexture(String fileName) {
        Array<String> fileNames = new Array(1);
        fileNames.add(fileName);
        return loadAnimationFromFiles(fileNames, 1f, true);
    }

    private Animation<TextureRegion> loadAnimationFromFiles(Array<String> fileNames, float frameDuration, Boolean loop) {
        Array<TextureRegion> textureArray = new Array();

        for (int i = 0; i < fileNames.size; i++) {
            Texture texture = new Texture(Gdx.files.internal(fileNames.get(i)));
            texture.setFilter(TextureFilter.Linear, TextureFilter.Linear);
            textureArray.add(new TextureRegion(texture));
        }

        Animation<TextureRegion> anim = new Animation(frameDuration, textureArray);

        if (loop)
            anim.setPlayMode(Animation.PlayMode.LOOP);
        else
            anim.setPlayMode(Animation.PlayMode.NORMAL);

        if (animation == null)
            setAnimation(anim);

        return anim;
    }

    public void setAnimation(Animation<TextureRegion> anim) {
        animation = anim;
        TextureRegion tr = animation.getKeyFrame(0);
        float w = tr.getRegionWidth() * BaseGame.UNIT_SCALE;
        float h = tr.getRegionHeight() * BaseGame.UNIT_SCALE;
        setSize(w, h);
        setOrigin(w / 2, h / 2);

        if (boundaryPolygon == null)
            setBoundaryRectangle();
    }

    public void loadImage(String name) {
        TextureRegion region = AssetLoader.textureAtlas.findRegion(name);
        if (region == null)
            Gdx.app.error(getClass().getSimpleName(), "Error: region is null. Are you sure the image '" + name + "' exists?");
        setAnimation(new Animation(1f, region));
    }


    // Physics ---------------------------------------------------------------------------------------------------
    public void setSpeed(float speed) {
        if (velocityVec.len() == 0)
            velocityVec.set(speed, 0);
        else
            velocityVec.setLength(speed);
    }

    public float getSpeed() {
        return velocityVec.len();
    }

    public void setAcceleration(float acc) {
        acceleration = acc;
    }

    public void setDeceleration(float dec) {
        deceleration = dec;
    }

    public void setMaxSpeed(float ms) {
        maxSpeed = ms;
    }

    public boolean isMoving() {
        return (getSpeed() > 0);
    }

    public void setMotionAngle(float angle) {
        velocityVec.setAngleDeg(angle);
    }

    public float getMotionAngle() {
        return velocityVec.angleDeg();
    }

    public void accelerateAtAngle(float angle) {
        accelerationVec.add(
                new Vector2(acceleration, 0).setAngleDeg(angle));
    }

    public void accelerateAtAngle(float external_force, float angle) {
        accelerationVec.add(
                new Vector2(external_force, 0).setAngleDeg(angle));
    }

    public void accelerateForward() {
        accelerateAtAngle(getRotation());
    }

    public void applyPhysics(float dt) {
        // apply acceleration
        velocityVec.add(accelerationVec.x * dt, accelerationVec.y * dt);

        float speed = getSpeed();

        // decrease speed (decelerate) when not accelerating
        if (accelerationVec.len() == 0)
            speed -= deceleration * dt;

        // keep speed within set bounds
        speed = MathUtils.clamp(speed, 0, maxSpeed);

        // update velocity
        setSpeed(speed);

        // update position according to value stored in velocity vector
        moveBy(velocityVec.x * dt, velocityVec.y * dt);

        // reset acceleration
        accelerationVec.set(0, 0);
    }

    // camera ---------------------------------------------------------------------------------------------------
    public void zoomCamera(float zoom) {
        if (this.getStage() != null) {
            OrthographicCamera camera = (OrthographicCamera) this.getStage().getCamera();
            camera.zoom = zoom;

            bindCameraToWorld(camera);
            camera.update();
        }
    }

    public void alignCamera(float targetX, float targetY, float lerp) {
        if (this.getStage() != null) {
            OrthographicCamera camera = (OrthographicCamera) this.getStage().getViewport().getCamera();

            // center camera on actor
            camera.position.set(new Vector3(
                    camera.position.x + (targetX + getOriginX() - camera.position.x) * lerp,
                    camera.position.y + (targetY + getOriginY() - camera.position.y) * lerp,
                    0f
            ));

            /*bindCameraToWorld(camera);*/
            camera.update();
        }
    }

    private void bindCameraToWorld(OrthographicCamera camera) {
        float minX = (camera.viewportWidth * camera.zoom) / 2;
        float maxX = worldBounds.width - (camera.viewportWidth * camera.zoom) / 2;
        if (minX <= maxX)
            camera.position.x = MathUtils.clamp(camera.position.x, minX, maxX);
        else
            camera.position.x = (camera.viewportWidth * camera.zoom) / 2 - ((camera.viewportWidth * camera.zoom) - worldBounds.width) / 2;

        float minY = (camera.viewportHeight * camera.zoom) / 2;
        float maxY = worldBounds.height - (camera.viewportHeight * camera.zoom) / 2;
        if (minY <= maxY)
            camera.position.y = MathUtils.clamp(camera.position.y, minY, maxY);
        else
            camera.position.y = (camera.viewportHeight * camera.zoom) / 2 - ((camera.viewportHeight * camera.zoom) - worldBounds.height) / 2;
    }

    public void averageBetweenTargetsCamera(Vector2 targetA, Vector2 targetB, float lerp) {
        if (this.getStage() != null) {
            OrthographicCamera camera = (OrthographicCamera) this.getStage().getCamera();
            float avgX = (targetA.x + targetB.x) / 2;
            float avgY = (targetA.y + targetB.y) / 2;
            Vector3 position = camera.position;

            position.x = camera.position.x + (avgX - camera.position.x) * lerp;
            position.y = camera.position.y + (avgY - camera.position.y) * lerp;
            camera.position.set(position);

            bindCameraToWorld(camera);
            camera.update();
        }
    }

    public Boolean searchFocalPoints(Array<Vector2> focalPoints, Vector2 target, float threshold, float lerp) {
        if (this.getStage() != null) {
            OrthographicCamera camera = (OrthographicCamera) this.getStage().getCamera();
            for (Vector2 point : focalPoints) {
                if (target.dst(point) < threshold) {
                    alignCamera(point.x, point.y, lerp);
                    return true;
                }
            }
        }
        return false;
    }

    protected void shakeCamera() {
        this.getStage().getCamera().position.set(
                new Vector3(
                        this.getStage().getCamera().position.x + MathUtils.random(
                                -shakyCamIntensity,
                                shakyCamIntensity
                        ),
                        this.getStage().getCamera().position.y + MathUtils.random(
                                -shakyCamIntensity,
                                shakyCamIntensity
                        ),
                        0f
                )
        );
        bindCameraToWorld((OrthographicCamera) this.getStage().getCamera());
    }


    // Collision detection --------------------------------------------------------------------------------------
    public void setBoundaryRectangle() {
        float w = getWidth();
        float h = getHeight();
        float[] vertices = {0, 0, w, 0, w, h, 0, h};
        boundaryPolygon = new Polygon(vertices);
    }

    public void setBoundaryPolygon(int numSides, float scale) {
        float w = getWidth() * scale;
        float h = getHeight() * scale;

        float[] vertices = new float[2 * numSides];
        for (int i = 0; i < numSides; i++) {
            float angle = i * MathUtils.PI2 / numSides;
            vertices[2 * i] = (w / 2) * MathUtils.cos(angle) + (getWidth() / 2);
            vertices[2 * i + 1] = (h / 2) * MathUtils.sin(angle) + (getHeight() / 2);
        }
        boundaryPolygon = new Polygon(vertices);
    }



    public void setCustomCollisionBox(float x, float y, float width, float height) {
        float[] vertices = {
            x, y,
            x + width, y,
            x + width, y + height,
            x, y + height
        };
        boundaryPolygon = new Polygon(vertices);
    }

    public Polygon getBoundaryPolygon() {
        boundaryPolygon.setPosition(getX(), getY());
        boundaryPolygon.setOrigin(getOriginX(), getOriginY());
        boundaryPolygon.setRotation(getRotation());
        boundaryPolygon.setScale(getScaleX(), getScaleY());
        return boundaryPolygon;
    }

    public boolean overlaps(BaseActor other) {
        if (!isCollisionEnabled || !other.isCollisionEnabled)
            return false;

        Polygon poly1 = this.getBoundaryPolygon();
        Polygon poly2 = other.getBoundaryPolygon();

        // initial test to improve performance
        if (!poly1.getBoundingRectangle().overlaps(poly2.getBoundingRectangle()))
            return false;

        return Intersector.overlapConvexPolygons(poly1, poly2);
    }

    public Vector2 preventOverlap(BaseActor other) {
        if (!isCollisionEnabled || !other.isCollisionEnabled) return null;
        Polygon poly1 = this.getBoundaryPolygon();
        Polygon poly2 = other.getBoundaryPolygon();

        // initial test to improve performance
        if (!poly1.getBoundingRectangle().overlaps(poly2.getBoundingRectangle()))
            return null;

        Intersector.MinimumTranslationVector mtv = new Intersector.MinimumTranslationVector();
        boolean polygonOverlap = Intersector.overlapConvexPolygons(poly1, poly2, mtv);

        if (!polygonOverlap)
            return null;

        this.moveBy(mtv.normal.x * mtv.depth, mtv.normal.y * mtv.depth);
        return mtv.normal;
    }

    // miscellaneous -------------------------------------------------------------------------------------------
    public void centerAtPosition(float x, float y) {
        setPosition(x - getWidth() / 2, y - getHeight() / 2);
    }

    public void centerAtActor(BaseActor baseActor) {
        centerAtPosition(baseActor.getX() + baseActor.getWidth() / 2, baseActor.getY() + baseActor.getHeight() / 2);
    }

    public void setOpacity(float opacity) {
        this.getColor().a = opacity;
    }

    public void boundToWorld() {
        if (getX() < 0)
            setX(0);
        if (getX() + getWidth() > worldBounds.width)
            setX(worldBounds.width - getWidth());
        if (getY() < 0)
            setY(0);
        if (getY() + getHeight() > worldBounds.height)
            setY(worldBounds.height - getHeight());
    }

    public static void setWorldBounds(float width, float height) {
        worldBounds = new Rectangle(0, 0, width, height);
    }

    public void setBounds(float width, float height) {
        worldBounds = new Rectangle(0, 0, width, height);
    }

    public static void setWorldBounds(BaseActor ba) {
        setWorldBounds(ba.getWidth(), ba.getHeight());
    }
}
