package no.sandramoen.libgdx33.screens.gameplay;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.Array;

import no.sandramoen.libgdx33.actors.Background;
import no.sandramoen.libgdx33.actors.Enemy;
import no.sandramoen.libgdx33.actors.Player;
import no.sandramoen.libgdx33.utils.BaseGame;
import no.sandramoen.libgdx33.utils.BaseScreen;

public class LevelScreen extends BaseScreen {

    private Player player;
    private Array<Enemy> enemies;

    private float game_time = 0f;
    private float lastEnemySpawnTime = 0f;
    private float enemySpawnInterval = 10f;
    private final float MIN_SPAWN_INTERVAL = 0.75f;


    @Override
    public void initialize() {
        // background
        new Background(0f, 0f, mainStage);

        // characters
        player = new Player(BaseGame.WORLD_WIDTH / 2, BaseGame.WORLD_HEIGHT / 2, mainStage);

        enemies = new Array<Enemy>();
        for(int i = 0; i < 5; i++)
            enemies.add(new Enemy(mainStage));
    }


    @Override
    public void update(float delta) {
        if (player.isMoving()) {
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
                player.setColor(Color.RED);
                System.out.println("player collided with enemy: " + enemy);
            }/* else {
                player.setColor(Color.WHITE);
            }*/
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
}
