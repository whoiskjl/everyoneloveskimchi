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
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.MoveToAction;
import com.badlogic.gdx.utils.TimeUtils;
import com.esotericsoftware.spine.AnimationState;
import com.esotericsoftware.spine.AnimationStateData;
import com.esotericsoftware.spine.Skeleton;
import com.esotericsoftware.spine.SkeletonData;
import com.esotericsoftware.spine.SkeletonJson;
import com.esotericsoftware.spine.SkeletonRenderer;
import com.esotericsoftware.spine.SkeletonRendererDebug;

/**
 *
 * @author AshCash
 */
public class Members extends Actor{
    public Everybodyloveskimchi game;
    TextureAtlas atlas;
    Skeleton skeleton;
    AnimationState state;
    String name;
    SkeletonRenderer renderer;
    SkeletonRendererDebug debugRenderer;
    float _whereToX, _whereToY;
    
    float factor = 0.3f;
    float _width = 220f;
    float _height = 520f;
    public float _originalX,_originalY;
    boolean _moved = false;
    String currentState = "idle";
    long startTime;
    long eatingTime;
    

    public String ag,foodtoeat;
    public boolean tobedead = false;
    
    public Members(Everybodyloveskimchi game, String s){
        this.game = game;
        this.name = s;
        this.eatingTime = game.eatingTime;
        this.renderer = new SkeletonRenderer();
        renderer.setPremultipliedAlpha(true); // PMA results in correct blending without outlines.
        this.debugRenderer = new SkeletonRendererDebug();
        debugRenderer.setBoundingBoxes(false);
	debugRenderer.setRegionAttachments(false);
        
        String filename;
        filename = s + ".atlas";
        this.atlas =  new TextureAtlas(Gdx.files.internal(filename));
        SkeletonJson json = new SkeletonJson((TextureAtlas) atlas);
        json.setScale(1f);
        filename = s + ".json";
        SkeletonData skeletonData =json.readSkeletonData(Gdx.files.internal(filename));
        skeleton=  new Skeleton(skeletonData);
        skeleton.setPosition(0,0);
        skeleton.getRootBone().setScale(factor);
        AnimationStateData stateData = new AnimationStateData(skeletonData);
        state = new AnimationState(stateData); 
        state.setTimeScale(2f);
        state.setAnimation(0, "idle", true);
        
    
    
        
    }
    public void moveTo(float x, float y, float sec) {
        MoveToAction action = new MoveToAction();
        action.setPosition(x, y);       
        action.setDuration(sec);
        this.addAction(action);
        state.setAnimation(0, "walk", true);
        _whereToX = x;
        _whereToY = y;
//        currentState = "walkto";
    }
    public void goEat(){
        game.facebuttons.get(name).disable();
        MoveToAction action = new MoveToAction();
        action.setPosition(0.5f * (float)Gdx.graphics.getWidth(), this.getY());       
        action.setDuration(game.walkDuration);
        this.addAction(action);
    }
    @Override
    public void act(float delta) {
        super.act(delta);
        skeleton.setPosition( this.getX() + (factor * _width * 0.5f), this.getY() );
        if(this.getX()==_whereToX&&this.getY()==_whereToY&&currentState.equals("walkto") ) {
            state.setAnimation(0,"eating",true);
            currentState = "eating";
            startTime = TimeUtils.millis();
            System.out.println(name + " just stared eating");
        } else if(currentState.equals("eating") && TimeUtils.timeSinceMillis(startTime) > eatingTime) {
            // @ check if it was a bad food other wise
            currentState = "walkback";
            walkback();
        } else if(this.getX()==_originalX&&this.getY()==_originalY&&currentState.equals("walkback")) {
            idle();
        }
        
        
    }
    @Override
    public void draw (Batch batch, float parentAlpha) {
        
        super.draw(batch,parentAlpha);
        state.update(Gdx.graphics.getDeltaTime());
        state.apply(skeleton); // Poses skeleton using current animations. This sets the bones' local SRT.
	skeleton.updateWorldTransform(); // Uses the bones' local SRT to compute their world SRT.
       
        renderer.draw(batch, skeleton);

    }
    public void trip () {
        game.isTrip = !game.isTrip;
    }
    public void sendToEat() {
        if( ag.equals(game.currentFood)) {
            tobedead = true;
        }
        game.nextFood();
        moveTo((float)Gdx.graphics.getWidth()*0.7f, getY(),2);
    }
    public void walkback() {
        if(tobedead) {
            state.setAnimation(0,"death",false);
            for(String s:game.names) {
                if(!s.equals(name)) {
                game.members.get(s).runoff();
                game.paused = true;
                }
            }
            tobedead = false;
        }else {
            currentState = "walkback";
            skeleton.setFlipX(false);
            moveTo(_originalX, _originalY,2);
            // ate , okay
            game.resetSec();
            game.score++;
        }
    }
    public void idle() {
        
        game.facebuttons.get(name).enable();
        currentState = "idle";
        skeleton.setFlipX(true);
        state.setAnimation(0,"idle",true);
    }
    
    public void runoff() {
        currentState = "runoff";
            skeleton.setFlipX(false);
            moveTo(-100f, -100f,2);
            game.facebuttons.get(name).disable();
    }

    private void setBounds(Rectangle rectangle) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
