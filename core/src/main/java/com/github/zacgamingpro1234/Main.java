package com.github.zacgamingpro1234;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;

/**
 * {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms.
 */
public class Main extends ApplicationAdapter {
    private SpriteBatch batch;
    private Sprite plr;
    float xSpeed;
    float ySpeed;
    boolean isJumping;
    FitViewport viewport;
    public static float delta;
    static float timeaccum;
    static float PHYSstep = .01f;
    static int MAXsteps = 35;
    static int totalSteps;


    @Override
    public void create() {
        batch = new SpriteBatch();
        viewport = new FitViewport(16, 9);
        Texture texture = new Texture("square.png");
        plr = new Sprite(texture);
        plr.setSize(1, 1);
        plr.setCenter(plr.getWidth() / 2, plr.getHeight() / 2);
        plr.setOrigin(plr.getWidth() / 2, plr.getHeight() / 2);
        plr.setOriginCenter();
        Gdx.app.setLogLevel(Application.LOG_DEBUG);
    }

    @Override
    public void render() { // Runs Every Frame
        delta = Gdx.graphics.getDeltaTime();
        timeaccum += delta;
        if (timeaccum > PHYSstep) {
           totalSteps = (int) (timeaccum/ PHYSstep);
            if (totalSteps > MAXsteps) {
                Gdx.app.log("Physhics Step", "Lag Spike Of " + totalSteps +
                    " Ticks, With Frame Time " + delta + "ms ");
                timeaccum = 0;
            }else{
                while (!(totalSteps <= 0)){
                    input();
                    timeaccum -= PHYSstep;
                    totalSteps -= 1;
                }
            }

        }
        logic();
        draw();
    }

    private void input() {

        if (Gdx.input.isKeyPressed(Input.Keys.A)) {
            xSpeed -= 1f;
            xSpeed = MathUtils.clamp(xSpeed, -4, 4);
        } else if (Gdx.input.isKeyPressed(Input.Keys.D)) {
            xSpeed += 1f;
            xSpeed = MathUtils.clamp(xSpeed, -4, 4);
        } else if (xSpeed > 0) {
            xSpeed -= .25f;
        } else if (xSpeed < 0) {
            xSpeed += .25f;
        }
        plr.translateX(xSpeed * .01f);
        ySpeed -= .5f;
        if (Gdx.input.isKeyPressed(Input.Keys.W)) {
            if (!isJumping){
                ySpeed = 12f;
                isJumping = true;
                ySpeed = MathUtils.clamp(ySpeed, -32, 32);
            }
        }
        plr.translateY(ySpeed * .01f);
    }

    private void logic() {
        // Store the worldWidth and worldHeight as local variables for brevity
        float worldWidth = viewport.getWorldWidth();
        float worldHeight = viewport.getWorldHeight();

        // Clamp x & y to values between 0 and worldWidth
        plr.setX(MathUtils.clamp(plr.getX(), 0, worldWidth - plr.getWidth()));
        if (plr.getY() < 0) {
            isJumping = false;
        }
        plr.setY(MathUtils.clamp(plr.getY(), 0, worldHeight - plr.getHeight()));
    }

    private void draw() {
        ScreenUtils.clear(Color.BLACK);
        viewport.apply();
        batch.setProjectionMatrix(viewport.getCamera().combined);
        batch.begin();
        plr.draw(batch);
        batch.end();
    }


    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true); // true centers the camera
    }


    @Override
    public void dispose() {
        batch.dispose();
    }
}
