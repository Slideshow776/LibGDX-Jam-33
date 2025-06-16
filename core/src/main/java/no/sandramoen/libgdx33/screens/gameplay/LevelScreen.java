package no.sandramoen.libgdx33.screens.gameplay;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Array;
import com.github.tommyettinger.textra.TypingLabel;

import no.sandramoen.libgdx33.actors.Background;
import no.sandramoen.libgdx33.actors.Enemy;
import no.sandramoen.libgdx33.actors.MapLine;
import no.sandramoen.libgdx33.actors.Player;
import no.sandramoen.libgdx33.actors.WaterPickup;
import no.sandramoen.libgdx33.gui.BaseProgressBar;
import no.sandramoen.libgdx33.utils.AssetLoader;
import no.sandramoen.libgdx33.utils.BaseGame;
import no.sandramoen.libgdx33.utils.BaseScreen;

public class LevelScreen extends BaseScreen {
    public static TypingLabel scoreLabel;
    public static TypingLabel messageLabel;
    public BaseProgressBar water_bar;
    public BaseProgressBar radiation_bar;
    private Background map_background;

    private Player player;
    private Array<Enemy> enemies;
    private Array<MapLine> map_lines;
    private Array<WaterPickup> waterPickups;

    private float game_time = 0f;
    private float lastEnemySpawnTime = 0f;
    private float enemySpawnInterval = 10f;
    private final float MIN_SPAWN_INTERVAL = 0.5f;

    private float map_line_spawn_interval = 0.075f;
    private float last_map_line_spawn_time = 0f;

    private float water_spawn_interval = 5.0f;
    private float last_water_spawn_time = 0f;
    private float water_consumption_rate = 0.41f;

    private float scoreUpdateTimer = 0f;
    private final float SCORE_UPDATE_INTERVAL = 2.5f; // update every 1 second, change `s` here
    private int score = 0; // your current score variable

    private boolean is_game_over = false;

    @Override
    public void initialize() {
        // background
        map_background = new Background(0f, 0f, mainStage);

        // characters
        player = new Player(BaseGame.WORLD_WIDTH / 2, BaseGame.WORLD_HEIGHT / 2, mainStage);
        player.setColor(Color.FOREST);

        enemies = new Array<Enemy>();
        for(int i = 0; i < 5; i++)
            enemies.add(new Enemy(mainStage));

        map_lines = new Array<MapLine>();
        waterPickups = new Array<WaterPickup>();

        initialize_gui();
    }


    @Override
    public void update(float delta) {
        if (player.isMoving() && !player.is_dead) {
            game_time += delta;

            // update enemies
            for (Enemy enemy : enemies) {
                enemy.pause = false;

                // collision detection
                if (player.overlaps(enemy))
                    set_game_over();
            }

            handle_map_lines(delta);
            handle_water(delta);

        } else {
            for (Enemy enemy : enemies)
                enemy.pause = true;
            for (MapLine map_line : map_lines)
                map_line.pause = true;
        }

        increment_difficulty(delta);
        handle_score(delta);
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


    private void handle_map_lines(float delta) {
        last_map_line_spawn_time += delta;
        if (last_map_line_spawn_time >= map_line_spawn_interval) {
            MapLine map_line = new MapLine(player.getX() + player.getWidth() / 2, player.getY() + player.getHeight() / 2, mainStage);
            map_line.setRotation(player.getMotionAngle());
            map_line.setZIndex(map_background.getZIndex() + 1);
            map_lines.add(map_line);
            last_map_line_spawn_time = 0f;
        }
        for (MapLine map_line : map_lines)
            map_line.pause = false;
    }


    private void handle_water(float delta) {
        // pick up water
        for (WaterPickup water_pickup : waterPickups) {
            if (player.overlaps(water_pickup)) {
                float roll = MathUtils.random();
                if (roll <= 0.1f) {
                    water_bar.incrementPercentage(30);
                } else if (roll <= 0.5f) {
                    water_bar.incrementPercentage(15);
                } else {
                    water_bar.incrementPercentage(5);
                }
                water_pickup.consume();
                waterPickups.removeValue(water_pickup, false);
            }
        }

        // consume water
        if (!water_bar.progress.hasActions()) {
            water_bar.decrementPercentage(1, water_consumption_rate);
        }

        // create water
        last_water_spawn_time += delta;
        if (last_water_spawn_time >= water_spawn_interval) {
            WaterPickup water_pickup = new WaterPickup(mainStage);
            water_pickup.centerAtPosition(
                MathUtils.random(1f, BaseGame.WORLD_WIDTH - 1),
                MathUtils.random(1f, BaseGame.WORLD_HEIGHT - 1)
            );
            waterPickups.add(water_pickup);
            last_water_spawn_time = 0f;
        }

        // dying from thirst
        if (water_bar.level <= 0.1f) {
            set_game_over();
        }
    }


    private void handle_score(float delta) {
        if (is_game_over)
            return;

        scoreUpdateTimer += delta;
        if (scoreUpdateTimer >= SCORE_UPDATE_INTERVAL) {
            scoreUpdateTimer -= SCORE_UPDATE_INTERVAL; // reset timer but keep overflow

            // Update your score logic here (example just increments)
            score += 1;

            // Update the score label text
            scoreLabel.setText(String.valueOf(score));
        }
    }


    private void increment_difficulty(float _delta) {
        float spawnInterval = Math.max(enemySpawnInterval - game_time * 0.15f, MIN_SPAWN_INTERVAL);
        if (game_time - lastEnemySpawnTime >= spawnInterval) {
            //System.out.println("added a new enemy, count: " + enemies.size + ", spawn interval: " + spawnInterval);
            lastEnemySpawnTime = game_time;
            enemies.add(new Enemy(mainStage));

            water_consumption_rate -= 0.0125f;
            water_spawn_interval -= 0.1f;
        }
    }


    private void set_game_over() {
        is_game_over = true;
        player.kill();
        messageLabel.getColor().a = 1.0f;
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

        water_bar = new BaseProgressBar(Gdx.graphics.getWidth() * -.41f, Gdx.graphics.getHeight() * 0.5f, uiStage);
        water_bar.rotateBy(90f);
        water_bar.setProgress(75);
        water_bar.setColor(Color.BLUE);
        water_bar.setProgressBarColor(Color.CYAN);
        water_bar.setOpacity(0.75f);
        water_bar.progress.setOpacity(0.75f);
        uiStage.addActor(water_bar);

        radiation_bar = new BaseProgressBar(Gdx.graphics.getWidth() * .51f, Gdx.graphics.getHeight() * 0.5f, uiStage);
        radiation_bar.rotateBy(90f);
        radiation_bar.setProgress(5);
        radiation_bar.setColor(Color.OLIVE);
        radiation_bar.setProgressBarColor(Color.GREEN);
        radiation_bar.setOpacity(0.75f);
        radiation_bar.progress.setOpacity(0.75f);
        uiStage.addActor(radiation_bar);

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

        /*uiTable.add(water_bar)
            .expand()
            .left()
            .row();*/

        //uiTable.setDebug(true);
    }
}
