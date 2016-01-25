/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.actions.MoveToAction;
import com.esotericsoftware.spine.AnimationState;
import com.esotericsoftware.spine.AnimationStateData;
import com.esotericsoftware.spine.Skeleton;
import com.esotericsoftware.spine.SkeletonData;
import com.esotericsoftware.spine.SkeletonJson;
import com.esotericsoftware.spine.SkeletonRenderer;
import java.util.Random;

/**
 *
 * @author AshCash
 */
public class Facebuttons extends Actor {
//    this.setBounds(this, factor, _width, _height);
//        
//        this.addListener(new InputListener() {
//        @Override
//        public boolean touchDown (InputEvent event, float x, float y, int pointer, int button) {
//            System.out.println(name + "down");
//            
//            moveTo(0f,100f,10);
//            
//            return true;
//        }
//
//        @Override
//        public void touchUp (InputEvent event, float x, float y, int pointer, int button) {
//            System.out.println(name + "up");
//            trip();
//        }
//    });

//    FileHandle faceiconFileHandle; 
//        faceiconFileHandle = Gdx.files.internal("faceicons/"+s+".png");
//        facebuttonTexture = new Texture(faceiconFileHandle);
//        facebutton = new Sprite(facebuttonTexture);
//        facebutton.setOriginCenter();
//        facebutton.setScale(0.5f);
//        facebutton.setX(game.facebuttonX.get(this.name));   
    Texture texture;
    Sprite sprite;
    public String assignedname;
    public float clickedY = 0f;
    public boolean enabled = true;
    Everybodyloveskimchi game;

    float _originalX, _originalY;

    public TextureAtlas atlas;
    public Skeleton skeleton;
    public AnimationState state;
    public SkeletonRenderer renderer;
    
    public String ag;

    public Facebuttons(Everybodyloveskimchi g, String n) {
        this.game = g;
        this.assignedname = n;
        texture = new Texture(Gdx.files.internal("faceicons/" + n + ".png"));
        this.renderer = new SkeletonRenderer();
        renderer.setPremultipliedAlpha(true);
        this.atlas = new TextureAtlas(Gdx.files.internal("foodx.atlas"));
        SkeletonJson json = new SkeletonJson((TextureAtlas) atlas);
        json.setScale(0.7f);
        SkeletonData skeletonData = json.readSkeletonData(Gdx.files.internal("foodx.json"));
        skeleton = new Skeleton(skeletonData);
        skeleton.setPosition(0, 0);
        skeleton.getRootBone().setScale(0.6f);
        AnimationStateData stateData = new AnimationStateData(skeletonData);
        state = new AnimationState(stateData);
        state.setTimeScale(0.5f);
        state.setAnimation(0, "idle", false);
        
        setAg();
        
        this.setBounds(0, 0, 98f, 106f);

        this.addListener(new InputListener() {
            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                if (enabled) {
                    System.out.println(assignedname + "down");
                    clickedY = -5f;
                    game.members.get(assignedname).foodtoeat = game.currentFood;
                    game.sendMemeber(assignedname);
//          clickedY 
//            moveTo(0f,100f,10);
//          
                }
                return true;

            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                if (enabled) {
                    System.out.println(assignedname + "up");
                    clickedY = 0f;
//            trip();
                }
            }
        });
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        skeleton.setPosition(super.getX() + 40f, 230f);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
        batch.end();
        batch.begin();
        state.update(Gdx.graphics.getDeltaTime());
        state.apply(skeleton); // Poses skeleton using current animations. This sets the bones' local SRT.
        skeleton.updateWorldTransform(); // Uses the bones' local SRT to compute their world SRT.

        renderer.draw(batch, skeleton);
        batch.end();
        batch.begin();
        batch.draw(texture, this.getX(), this.getY() + 20f);

    }

    public void disable() {
        float x = this.getX();
        float y = -50f;
        MoveToAction action = new MoveToAction();
        action.setPosition(x, y);
        action.setDuration(0.3f);
        this.addAction(action);
        enabled = false;
    }

    public void enable() {
        float x = this.getX();
        float y = _originalY;
        MoveToAction action = new MoveToAction();
        action.setPosition(x, y);
        action.setDuration(0.3f);
        this.addAction(action);
        enabled = true;
    }

    public void setOriginalPosition(float x, float y) {
        this._originalX = x;
        this._originalY = y;
    }
    public void setAg(){
        Random random = new Random();
            ag = Integer.toString(random.nextInt(14) + 1);
            skeleton.setSkin(ag);
            game.members.get(assignedname).ag = ag;
    }
    public void reset() {
        setAg();
        state.setAnimation(0,"idle",false);
    }

}
