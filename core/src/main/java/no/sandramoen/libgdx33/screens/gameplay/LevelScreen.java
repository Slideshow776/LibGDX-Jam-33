package no.sandramoen.libgdx33.screens.gameplay;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.github.tommyettinger.textra.TypingLabel;

import no.sandramoen.libgdx33.actors.Background;
import no.sandramoen.libgdx33.actors.Enemy;
import no.sandramoen.libgdx33.actors.Player;
import no.sandramoen.libgdx33.utils.AssetLoader;
import no.sandramoen.libgdx33.utils.BaseActor;
import no.sandramoen.libgdx33.utils.BaseGame;
import no.sandramoen.libgdx33.utils.BaseScreen;

public class LevelScreen extends BaseScreen {
    public static TypingLabel scoreLabel;
    public static TypingLabel messageLabel;

    private Player player;
    private Array<Enemy> enemies;

    private float game_time = 0f;
    private float lastEnemySpawnTime = 0f;
    private float enemySpawnInterval = 10f;
    private final float MIN_SPAWN_INTERVAL = 0.75f;

    private float scoreUpdateTimer = 0f;
    private final float SCORE_UPDATE_INTERVAL = 2.5f; // update every 1 second, change `s` here
    private int score = 0; // your current score variable

    @Override
    public void initialize() {
        // background
        new Background(0f, 0f, mainStage);

        // characters
        player = new Player(BaseGame.WORLD_WIDTH / 2, BaseGame.WORLD_HEIGHT / 2, mainStage);
        player.setColor(Color.FOREST);

        enemies = new Array<Enemy>();
        for(int i = 0; i < 5; i++)
            enemies.add(new Enemy(mainStage));

        initialize_gui();
    }


    @Override
    public void update(float delta) {
        if (player.isMoving() && !player.is_dead) {
            game_time += delta;
            for (Enemy enemy : enemies) {
                enemy.pause = false;
            }
        } else {
            for (Enemy enemy : enemies) {
                enemy.pause = true;
            }
        }

        // spawn enemies
        float spawnInterval = Math.max(enemySpawnInterval - game_time * 0.15f, MIN_SPAWN_INTERVAL);
        if (game_time - lastEnemySpawnTime >= spawnInterval) {
            //System.out.println("added a new enemy, count: " + enemies.size + ", spawn interval: " + spawnInterval);
            lastEnemySpawnTime = game_time;
            enemies.add(new Enemy(mainStage));
        }

        // collision detection
        for (Enemy enemy : enemies) {
            if (player.overlaps(enemy)) {
                player.kill();
                messageLabel.getColor().a = 1.0f;
            }
        }

        // Update the score update timer
        scoreUpdateTimer += delta;
        if (scoreUpdateTimer >= SCORE_UPDATE_INTERVAL) {
            scoreUpdateTimer -= SCORE_UPDATE_INTERVAL; // reset timer but keep overflow

            // Update your score logic here (example just increments)
            score += 1;

            // Update the score label text
            scoreLabel.setText(String.valueOf(score));
        }
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


    private void initialize_gui() {
        Image calendar = new Image(new Texture("images/included/gui/calendar.png"));

        float desiredWidth = 40f; // or whatever fits your layout
        float aspectRatio = calendar.getHeight() / calendar.getWidth();
        calendar.setSize(desiredWidth, desiredWidth * aspectRatio);

        scoreLabel = new TypingLabel("0", AssetLoader.getLabelStyle("Play-Bold59white"));
        scoreLabel.setAlignment(Align.center);

        messageLabel = new TypingLabel("{CROWD}press '{RAINBOW}R{ENDRAINBOW}' to restart", AssetLoader.getLabelStyle("Play-Bold59white"));
        messageLabel.getColor().a = 0.0f;
        messageLabel.setAlignment(Align.center);

        uiTable.defaults()
            .padTop(Gdx.graphics.getHeight() * .02f)
        ;

        uiTable.add(calendar)
            .width(calendar.getWidth())
            .height(calendar.getHeight())
            .padBottom(-Gdx.graphics.getHeight() * .02f)
            .row();

        uiTable.add(scoreLabel)
            .height(scoreLabel.getPrefHeight() * 1.5f)
            .row()
        ;

        uiTable.add(messageLabel)
            .expandY()
            .padBottom(Gdx.graphics.getHeight() * .1f)
            .row();

        //uiTable.setDebug(true);
    }
}
