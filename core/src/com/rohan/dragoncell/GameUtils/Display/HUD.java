package com.rohan.dragoncell.GameUtils.Display;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.rohan.dragoncell.GameScenes.MainScreen;
import com.rohan.dragoncell.GameUtils.Entity.Player;

import java.util.HashMap;

public class HUD {

    public Player player;
    private SpriteBatch batch = new SpriteBatch();

    private Texture heart, emptyHeart, inventory, chest, crafting_, recipeBook, forge_, presser_, shop_;
    public Texture collection_;
    private Sprite craftingIcon, clearIcon, materialsBookIcon, forgeIcon, collectionIcon, clearIconHighlight, finishIcon, finishIconHighlight, presserIcon, shopIcon;
    private Sprite alert_ = new Sprite(new Texture(Gdx.files.internal("Interface/World/Collection/alert.png")));
    private boolean clearIconActive = true;
    private boolean finishIconActive = true;
    public boolean craftingActive = true;
    public boolean forgeActive = false;
    public boolean shopActive = false;
    public boolean shopUnlocked = false;
    public boolean collectionActive = false;
    public boolean matBookActive = true;
    public boolean presserActive = false;
    public boolean presserUnlocked = false;

    public HashMap<String, String> alert = new HashMap<String, String>();
    private BitmapFont alertDrawer = new BitmapFont(Gdx.files.internal("Fonts/ari2.fnt"), Gdx.files.internal("Fonts/ari2.png"), false);

    private float animTime = 0f;

    private GlyphLayout layout = new GlyphLayout();
    private BitmapFont inventory_ = new BitmapFont(Gdx.files.internal("Fonts/turok2.fnt"), Gdx.files.internal("Fonts/turok2.png"), false);
    private BitmapFont crafting = new BitmapFont(Gdx.files.internal("Fonts/turok2.fnt"), Gdx.files.internal("Fonts/turok2.png"), false);
    private BitmapFont recipe_ = new BitmapFont(Gdx.files.internal("Fonts/turok2.fnt"), Gdx.files.internal("Fonts/turok2.png"), false);

    private BitmapFont levelDrawer = new BitmapFont(Gdx.files.internal("Fonts/ari2.fnt"), Gdx.files.internal("Fonts/ari2.png"), false);


    public HUD(Player player) {
        this.player = player;

        heart = new Texture(Gdx.files.internal("Interface/HUD/heart.png"));
        emptyHeart = new Texture(Gdx.files.internal("Interface/HUD/background.png"));
        inventory = new Texture(Gdx.files.internal("Interface/HUD/inventory.png"));
        chest = new Texture(Gdx.files.internal("Interface/HUD/chestInventory.png"));
        crafting_ = new Texture(Gdx.files.internal("Interface/HUD/workbench_view.png"));
        forge_ = new Texture(Gdx.files.internal("Interface/HUD/forge_view.png"));
        presser_ = new Texture(Gdx.files.internal("Interface/HUD/presser_view.png"));
        collection_ = new Texture(Gdx.files.internal("Interface/HUD/Collection/woodland.png"));
        recipeBook = new Texture(Gdx.files.internal("Interface/HUD/recipeBook.png"));
        craftingIcon = new Sprite(new Texture(Gdx.files.internal("Interface/HUD/craftingIcon.png")));
        collectionIcon = new Sprite(new Texture(Gdx.files.internal("Interface/HUD/collectionIcon.png")));
        clearIcon = new Sprite(new Texture(Gdx.files.internal("Interface/HUD/clear.png")));
        forgeIcon = new Sprite(new Texture(Gdx.files.internal("Interface/HUD/forgeIcon.png")));
        presserIcon = new Sprite(new Texture(Gdx.files.internal("Interface/HUD/presserIcon.png")));
        materialsBookIcon = new Sprite(new Texture(Gdx.files.internal("Interface/HUD/materialsBookIcon.png")));
        clearIconHighlight = new Sprite(new Texture(Gdx.files.internal("Interface/HUD/clear_highlight.png")));
        finishIcon = new Sprite(new Texture(Gdx.files.internal("Interface/HUD/finish.png")));
        finishIconHighlight = new Sprite(new Texture(Gdx.files.internal("Interface/HUD/finish_highlight.png")));
        shopIcon = new Sprite(new Texture(Gdx.files.internal("Interface/HUD/shopIcon.png")));
        shop_ = new Texture(Gdx.files.internal("Interface/HUD/shop_view.png"));


        levelDrawer.getData().setScale(0.6f);

        craftingIcon.setPosition(549, 400);
        forgeIcon.setPosition(549, 370);
        materialsBookIcon.setPosition(549,430);
        collectionIcon.setPosition(549, 340);
        presserIcon.setPosition(549, 280);
        shopIcon.setPosition(549, 310);


        inventory_.setColor(Color.TAN);
        inventory_.getData().setScale(2);

        crafting.setColor(Color.TAN);
        crafting.getData().setScale(2);

        recipe_.setColor(Color.TAN);
        recipe_.getData().setScale(1f);

        alert_.setPosition(-220, 10);
        alertDrawer.setColor(Color.GOLDENROD);
        alertDrawer.getData().setScale(0.5f);
    }

    public void changeCollectionScene(int area) {

        switch(area) {
            case 1:
                collection_ = new Texture(Gdx.files.internal("Interface/HUD/Collection/woodland.png"));
                break;
            case 2:
                collection_ = new Texture(Gdx.files.internal("Interface/HUD/Collection/woodland_river.png"));
                break;
            case 3:
                collection_ = new Texture(Gdx.files.internal("Interface/HUD/Collection/desert.png"));
                break;
            case 4:
                collection_ = new Texture(Gdx.files.internal("Interface/HUD/Collection/beach.png"));
                break;
            case 5:
                collection_ = new Texture(Gdx.files.internal("Interface/HUD/Collection/oreland.png"));
                break;
            case 6:
                collection_ = new Texture(Gdx.files.internal("Interface/HUD/Collection/island.png"));
                break;
        }
    }

    public void render(float delta) {

        batch.begin();

        batch.draw(inventory, 570, 10);
        craftingIcon.draw(batch);
        forgeIcon.draw(batch);
        materialsBookIcon.draw(batch);
        collectionIcon.draw(batch);

        if(presserUnlocked) {
            presserIcon.draw(batch);
        }
        if(shopUnlocked) {
            shopIcon.draw(batch);
        }

        checkClicks();

        if(matBookActive) {
            batch.draw(recipeBook, 80, 520);
            MainScreen.materialsBook.render(batch);

            layout.setText(recipe_, "Material Book");
            recipe_.draw(batch, "Material Book", 80 + (recipeBook.getWidth() / 2) - layout.width / 2, 770);
        } else {
            for(int x = 0; x < player.hearts; x++) {
                if(player.health == player.hearts) {
                    batch.draw(heart, 20 * x + 5, 770);
                } else {
                    if(x > player.health - 1) {
                        batch.draw(emptyHeart, 20 * x + 5, 770);
                    } else {
                        batch.draw(heart, 20 * x + 5, 770);
                    }
                }
            }

            layout.setText(levelDrawer, new String(new char[player.getLeveling().getSubLevelPoints()]).replace("\0", "-"));
            levelDrawer.setColor(Color.GREEN);
            levelDrawer.draw(batch, new String(new char[player.getLeveling().getSubLevelPoints()]).replace("\0", "-"), 10, 755);
            levelDrawer.setColor(Color.LIGHT_GRAY);
            levelDrawer.draw(batch, new String(new char[player.getLeveling().getSubLevelGoal() - player.getLeveling().getSubLevelPoints()]).replace("\0", "-"),
                    10 + layout.width + 2, 755);

            levelDrawer.setColor(Color.GOLDENROD);
            levelDrawer.draw(batch, "Level " + player.getLeveling().getLevel(), 10, 735);
        }

        crafting.setColor(Color.TAN);
        if(craftingActive) {
            batch.draw(crafting_, 20, 10);
            MainScreen.crafting.render(batch);

            clearIcon.setCenter(480, 260);
            clearIconHighlight.setCenter(480, 260);

            finishIcon.setCenter(480, 200);
            finishIconHighlight.setCenter(480, 200);

            layout.setText(crafting, "Workbench");
            crafting.draw(batch, "Workbench", 20 + (crafting_.getWidth() / 2) - layout.width / 2, 480);
        } else if(forgeActive) {
            batch.draw(forge_, 20, 10);
            MainScreen.forge.render(batch);

            clearIcon.setCenter(480, 280);
            clearIconHighlight.setCenter(480, 280);

            layout.setText(crafting, "Forge");
            crafting.draw(batch, "Forge", 20 + (forge_.getWidth() / 2) - layout.width / 2, 480);
        } else if(collectionActive) {
            batch.draw(collection_, 20, 10);
            MainScreen.collectionView.render(batch);
        } else if(presserActive) {
            batch.draw(presser_, 20, 10);
            MainScreen.presser.render(batch);

            clearIcon.setCenter(480, 260);
            clearIconHighlight.setCenter(480, 260);

            layout.setText(crafting, "Juicer");
            crafting.draw(batch, "Juicer", 20 + (presser_.getWidth() / 2) - layout.width / 2, 480);
        } else if(shopActive) {
            crafting.setColor(Color.DARK_GRAY);
            batch.draw(shop_, 20, 10);
            MainScreen.shop.render(batch);

            layout.setText(crafting, "Market");
            crafting.draw(batch, "Market", 20 + (shop_.getWidth() / 2) - layout.width / 2, 480);
        }
        if(clearIconActive) {
            if (clearIcon.getBoundingRectangle().contains(Gdx.input.getX(), 800 - Gdx.input.getY())) {
                clearIconHighlight.draw(batch);
                if (Gdx.input.justTouched()) {
                    if(craftingActive) {
                        MainScreen.crafting.clearGrid();
                    } else if(presserActive) {
                        MainScreen.presser.clearGrid();
                    }
                }
            } else {
                clearIcon.draw(batch);
            }
        }
        if(finishIconActive) {
            if(finishIcon.getBoundingRectangle().contains(Gdx.input.getX(), 800 - Gdx.input.getY())) {
                finishIconHighlight.draw(batch);
                if(Gdx.input.justTouched()) {
                    MainScreen.crafting.craft();
                }
            } else {
                finishIcon.draw(batch);
            }
        }

        layout.setText(inventory_, "Inventory");
        inventory_.draw(batch, "Inventory", 570 + (inventory.getWidth() / 2) - layout.width / 2, 770);

        if(alert.size() != 0) {
            alertDrawer.setColor(Color.GOLDENROD);
            alertDrawer.getData().setScale(0.5f);

            alert_.draw(batch);
            layout.setText(alertDrawer, alert.get("alert_text"));
            alertDrawer.draw(batch, alert.get("alert_text"), alert_.getX() + 20 + (alert_.getWidth() / 2) - layout.width / 2, 55);

            alertDrawer.setColor(Color.TAN);
            alertDrawer.getData().setScale(0.4f);

            layout.setText(alertDrawer, alert.get("alert_description"));
            alertDrawer.draw(batch, alert.get("alert_description"), alert_.getX() + 10 + (alert_.getWidth() / 2) - layout.width / 2, 35);

            animTime += Gdx.graphics.getDeltaTime();

            if(alert_.getX() >= 15) {
                if(animTime > 5) {
                    alert.clear();
                    alert_.setPosition(-220, 10);
                    animTime = 0f;
                }
            } else {
                alert_.setX(alert_.getX() + 2.5f);
            }
        }

        batch.end();

    }

    private void checkClicks() {
        if(craftingIcon.getBoundingRectangle().contains(Gdx.input.getX(), 800 - Gdx.input.getY())) {
            if(Gdx.input.justTouched()) {
                if(!craftingActive) {
                    turnAllOff();
                    craftingActive = true;
                    clearIconActive = true;
                    finishIconActive = true;
                } else {
                    craftingActive = false;
                    clearIconActive = false;
                    finishIconActive = false;
                }
            }
        } else if(materialsBookIcon.getBoundingRectangle().contains(Gdx.input.getX(), 800 - Gdx.input.getY())) {
            if(Gdx.input.justTouched()) {
                if(!matBookActive) {
                    matBookActive = true;
                } else {
                    matBookActive = false;
                }
            }
        } else if(forgeIcon.getBoundingRectangle().contains(Gdx.input.getX(), 800 - Gdx.input.getY())) {
            if(Gdx.input.justTouched()) {
                if(!forgeActive) {
                    turnAllOff();
                    forgeActive = true;
                } else {
                    forgeActive = false;
                    clearIconActive = false;
                }
            }
        } else if(collectionIcon.getBoundingRectangle().contains(Gdx.input.getX(), 800 - Gdx.input.getY())) {
            if (Gdx.input.justTouched()) {
                if (!collectionActive) {
                    turnAllOff();
                    collectionActive = true;
                } else {
                    collectionActive = false;
                }
            }
        } else if(presserIcon.getBoundingRectangle().contains(Gdx.input.getX(), 800 - Gdx.input.getY())) {
            if(presserUnlocked) {
                if (Gdx.input.justTouched()) {
                    if (!presserActive) {
                        turnAllOff();
                        clearIconActive = true;
                        presserActive = true;
                    } else {
                        presserActive = false;
                        clearIconActive = false;
                    }
                }
            }
        } else if(shopIcon.getBoundingRectangle().contains(Gdx.input.getX(), 800 - Gdx.input.getY())) {
            if (shopUnlocked) {
                if (Gdx.input.justTouched()) {
                    if (!shopActive) {
                        turnAllOff();
                        shopActive = true;
                    } else {
                        shopActive = false;
                    }
                }
            }
        }
    }

    private void turnAllOff() {
        forgeActive = false;
        craftingActive = false;
        collectionActive = false;
        clearIconActive = false;
        finishIconActive = false;
        presserActive = false;
        shopActive = false;
    }
}
