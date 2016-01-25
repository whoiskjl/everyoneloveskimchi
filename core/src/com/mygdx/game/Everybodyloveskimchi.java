package com.mygdx.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.TimeUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.esotericsoftware.spine.*;
import java.util.HashMap;
import java.util.Random;

public class Everybodyloveskimchi extends ApplicationAdapter {

    OrthographicCamera camera;
    SpriteBatch batch;

    HashMap<String, Members> members = new HashMap();

    String[] names = {"dad", "mom", "kid1", "kid2", "kid3"};

    Stage mainstage, uistage, bgstage;

    public int score = 0;

    boolean isTrip = false;

    public HashMap<String, Facebuttons> facebuttons;

    float walkDuration = 2f;

    public long eatingTime = 1000;

    public BitmapFont font;

    public long secondsLeft = 6000, defaultsecondsLeft = 6000;
    public long startTime;
    public Texture title;

    public TextureAtlas atlas;
    public Skeleton skeleton;
    public AnimationState state;
    public SkeletonRenderer renderer;

    public String currentFood;

    public boolean paused = true;
    public float buttonX;
    public Sound mp3Sound;

    @Override
    public void create() {
        camera = new OrthographicCamera();
        batch = new SpriteBatch();

        mainstage = new Stage(new FitViewport(640, 480));
        uistage = new Stage(new FitViewport(640, 480));
//                mainstage = new Stage();
//                uistage = new Stage();
//                bgstage = new Stage();
        Gdx.input.setInputProcessor(mainstage);

        this.facebuttons = new HashMap();

        buttonX = 40f;

        font = new BitmapFont(Gdx.files.internal("myfont.fnt"));

        font.setOwnsTexture(true);

        title = new Texture(Gdx.files.internal("title.png"));

        for (String s : names) {
            Members member = new Members(this, s);
            members.put(s, member);
//                    Random random = new Random();
//                    int randomX = 60 + random.nextInt(100);
//                    members.get(s).setPosition(facebuttonX.get(s), 0);
            mainstage.addActor(members.get(s));
            Facebuttons fb = new Facebuttons(this, s);
            facebuttons.put(s, fb);
            facebuttons.get(s).setPosition(buttonX, 20f);
            facebuttons.get(s).setOriginalPosition(buttonX, 20f);
            uistage.addActor(facebuttons.get(s));
            buttonX += 110f;
        }

        this.renderer = new SkeletonRenderer();
        renderer.setPremultipliedAlpha(true);
        this.atlas = new TextureAtlas(Gdx.files.internal("food.atlas"));
        SkeletonJson json = new SkeletonJson((TextureAtlas) atlas);
        json.setScale(1f);
        SkeletonData skeletonData = json.readSkeletonData(Gdx.files.internal("food.json"));
        skeleton = new Skeleton(skeletonData);
        skeleton.setPosition(0, 0);
        skeleton.getRootBone().setScale(0.6f);
        AnimationStateData stateData = new AnimationStateData(skeletonData);
        state = new AnimationState(stateData);
        state.setTimeScale(1f);
        state.setAnimation(0, "idle", true);
        skeleton.setPosition(550f, 400f);
        skeleton.setSkin("1");
        mainstage.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {

                start();

            }
        });
        resetGame();
        mp3Sound = Gdx.audio.newSound(Gdx.files.internal("soundfile.mp3"));
        long id = mp3Sound.loop();
    }

    @Override
    public void render() {
        if (!isTrip) {
            Gdx.gl.glClearColor(1f, 1f, 1f, 1f);
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        }
        // Configure the camera, SpriteBatch, and SkeletonRendererDebug.

        batch.begin();
        font.getData().setScale(0.3f);

        String sl;
        sl = "Click to Start";
        if (!paused) {
            long sec = TimeUtils.timeSinceMillis(startTime);
            long left = (int) ((secondsLeft - sec) / 1000);
            sl = Long.toString(left);
            if (left < 0) {
                sl = "G A ME OVER";
            }
            if ((int) ((secondsLeft - sec) / 1000) <= 0) {
                for (String s : names) {
                    members.get(s).runoff();
                }
                gameover();
            }

        }
        font.draw(batch, "Meal # " + Integer.toString(score) + " : " + sl + ".", 10f, (float) Gdx.graphics.getHeight() - 10f);
        batch.end();
        if (paused) {
            batch.begin();
            batch.draw(title, 205f, 230f);
            batch.end();
        }
        if (!paused) {

            batch.begin();
            state.update(Gdx.graphics.getDeltaTime());
            state.apply(skeleton); // Poses skeleton using current animations. This sets the bones' local SRT.
            skeleton.updateWorldTransform(); // Uses the bones' local SRT to compute their world SRT.

            renderer.draw(batch, skeleton);
            batch.end();

//                camera.update();
//        	batch.getProjectionMatrix().set(camera.combined);

        }
        batch.begin();
        mainstage.act();

        mainstage.draw();
        uistage.act();

        uistage.draw();
        batch.end();

    }

    @Override
    public void resize(int width, int height) {
//                camera.setToOrtho(false);
    }

    public void recalZindex() {
        Array<Actor> actors = mainstage.getActors();
        for (Actor a : actors) {
            a.setZIndex((int) (a.getY() * 100));
        }
    }

    public void trip() {
        isTrip = true;
    }

    public void resetGame() {
        float tempY = 250f;
        float currentX = 150f;
        score = 0;
        startTime = TimeUtils.millis();
        for (String s : names) {
            members.get(s).setPosition(currentX, tempY);
            members.get(s).skeleton.setFlipX(true);

            members.get(s)._originalX = currentX;
            members.get(s)._originalY = tempY;

            tempY -= 25f;
            currentX -= 30f;

        }
    }

    public void sendMemeber(String name) {
        facebuttons.get(name).disable();
        members.get(name).currentState = "walkto";
        members.get(name).sendToEat();
    }

    public void resetSec() {
        secondsLeft = defaultsecondsLeft;
        startTime = TimeUtils.millis();
    }

    public void nextFood() {
        Random random = new Random();
        currentFood = Integer.toString(random.nextInt(15) + 1);
        skeleton.setSkin(currentFood);
    }

    public void start() {
        paused = false;
        startTime = System.currentTimeMillis();
        Gdx.input.setInputProcessor(uistage);
    }

    public void gameover() {
        Gdx.input.setInputProcessor(mainstage);
//        paused = true;
//        for (String s : names) {
//            facebuttons.get(s).reset();
//        }
//        resetGame();
    }
}
