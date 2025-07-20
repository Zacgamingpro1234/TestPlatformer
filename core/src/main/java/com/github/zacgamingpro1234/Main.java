package com.github.zacgamingpro1234;

import com.badlogic.gdx.*;
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
    boolean isSliding;
    boolean isFullscreen;
    float slidetime;
    boolean lastXinput; //false = left, true = right
    FitViewport viewport;
    public static float delta;
    static float timeaccum;
    static int tps = 100; // Change This Value To Change Physics Updates Per Second
    static float PHYSstep = (float) 1/tps;
    static int MAXsteps = (int) (((float) 25/100) * tps);
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
        if (Gdx.input.isKeyPressed(Input.Keys.A) && !(xSpeed <= -4)) {
            xSpeed -= 1f;
            lastXinput = false;
        } else if (Gdx.input.isKeyPressed(Input.Keys.D) && !(xSpeed >= 4)) {
            xSpeed += 1f;
            lastXinput = true;
        } else if (xSpeed > 0) {
            xSpeed -= .25f / ((float) tps/100);
            if (xSpeed < 0) xSpeed = 0;
        } else if (xSpeed < 0) {
            xSpeed += .25f / ((float) tps/100);
            if (xSpeed > 0) xSpeed = 0;
        }

        ySpeed -= .5f / ((float) tps/100);
        if (Gdx.input.isKeyJustPressed(Input.Keys.SHIFT_LEFT) && !isSliding){
            ySpeed = -4f;
            if (lastXinput){
                xSpeed = 12f; // true = right
            }else{
                xSpeed = -12f; // false = left
            }
            isSliding = true;
        } else if (Gdx.input.isKeyPressed(Input.Keys.SPACE) && isJumping && !isSliding){
            ySpeed = -1f;
        } else if (Gdx.input.isKeyPressed(Input.Keys.W) && !isJumping) {
                ySpeed = 12f;
                isJumping = true;
        }

        xSpeed = MathUtils.clamp(xSpeed, -16, 16);
        ySpeed = MathUtils.clamp(ySpeed, -32, 32);
        plr.translate(xSpeed * PHYSstep, ySpeed * PHYSstep);
        if(Gdx.input.isKeyJustPressed(Input.Keys.F11)) {
            Graphics.Monitor currMonitor = Gdx.graphics.getMonitor();
            Graphics.DisplayMode displayMode = Gdx.graphics.getDisplayMode(currMonitor);
            if (isFullscreen){
                Gdx.graphics.setWindowedMode(1072, 603);
            }else{
                Gdx.graphics.setFullscreenMode(displayMode);
            }
            isFullscreen = !isFullscreen;
        }

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
        if (isSliding){
            slidetime += delta;
            if (slidetime >= 1){
                isSliding = false;
                slidetime = 0;
            }
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
