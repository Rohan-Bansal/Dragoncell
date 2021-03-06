package com.rohan.dragoncell.GameUtils.Entity;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.rohan.dragoncell.GameUtils.Display.Leveling;
import org.w3c.dom.css.Rect;

import java.util.HashMap;

public class Player {

    private Inventory inventory;
    private Leveling leveling;

    public int hearts = 10;
    public int health = 10;
    public int coins = 0;
    public float speed = 1.5f;

    private float stateTime;
    private boolean flip;

    private Texture walkSheet;
    public TextureRegion currentFrame;
    private String horidirection = "left";
    private Animation<TextureRegion> walkAnim;
    public Vector2 position;
    private int animState;

    public boolean desertUnlocked = false;
    public boolean oreFieldUnlocked = false;
    public boolean beachUnlocked = false;
    public boolean restrictMovement = false;
    public String[] desertRequirement = new String[] {"11", "20"};
    public String[] beachRequirement = new String[] {"37", "10"};

    public Player() {

        inventory = new Inventory(this);
        leveling = new Leveling();

        leveling.setLevel(1);

        position = new Vector2(285, 260);

        walkSheet = new Texture(Gdx.files.internal("Character/character_right.png"));
        TextureRegion[][] walkTMP = TextureRegion.split(walkSheet,
                walkSheet.getWidth() / 3,
                walkSheet.getHeight() / 1);

        TextureRegion[] walkFrames = new TextureRegion[3 * 1];
        int index = 0;
        for (int i = 0; i < 1; i++) {
            for (int j = 0; j < 3; j++) {
                walkFrames[index++] = walkTMP[i][j];
            }
        }
        walkAnim = new Animation<TextureRegion>(0.3f, walkFrames);
        walkAnim.setPlayMode(Animation.PlayMode.LOOP_PINGPONG);


    }

    public Rectangle getRect() {
        return new Rectangle(position.x, position.y, currentFrame.getRegionWidth(), currentFrame.getRegionHeight());
    }

    public Inventory getInventory() {
        return inventory;
    }

    public Leveling getLeveling() {
        return leveling;
    }


    public void renderInventory() {

        inventory.render();
        leveling.update();
    }

    public void renderPlayer(SpriteBatch batch) {

        if(!restrictMovement)  {
            checkMove();
        }

        currentFrame = walkAnim.getKeyFrame(stateTime, true);
        flip = horidirection.equals("left");

        batch.draw(currentFrame, flip ? position.x + currentFrame.getRegionWidth() : position.x, position.y, flip ? -currentFrame.getRegionWidth() : currentFrame.getRegionWidth(), currentFrame.getRegionHeight());
    }

    private void checkMove() {

        if(Gdx.input.isKeyPressed(Input.Keys.W) && position.y < 440) {
            stateTime += Gdx.graphics.getDeltaTime();
            position.y += speed;
        } else
        if(Gdx.input.isKeyPressed(Input.Keys.A) && position.x > 40) {
            stateTime += Gdx.graphics.getDeltaTime();
            horidirection = "left";
            position.x -= speed;
        } else
        if(Gdx.input.isKeyPressed(Input.Keys.S) && position.y > 30) {
            stateTime += Gdx.graphics.getDeltaTime();
            position.y -= speed;
        } else
        if(Gdx.input.isKeyPressed(Input.Keys.D) && position.x < 500) {
            stateTime += Gdx.graphics.getDeltaTime();
            horidirection = "right";
            position.x += speed;
        }
    }
}
