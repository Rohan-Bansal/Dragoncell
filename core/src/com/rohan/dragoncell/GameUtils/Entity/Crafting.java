package com.rohan.dragoncell.GameUtils.Entity;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.rohan.dragoncell.FileUtils.Tuple;
import com.rohan.dragoncell.GameScenes.MainScreen;
import com.rohan.dragoncell.GameUtils.Display.HUD;
import com.rohan.dragoncell.GameUtils.Material;
import com.rohan.dragoncell.GameUtils.MaterialsList;
import com.rohan.dragoncell.GameUtils.ObtainMethods;
import com.rohan.dragoncell.Main;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;

public class Crafting {

    private Inventory inventory;
    private MaterialsList materials;

    public ArrayList<Sprite> craftingSlots = new ArrayList<Sprite>();
    private ArrayList<Material> craftingItems = new ArrayList<Material>();
    private ArrayList<Material> craftingItemsToRemove = new ArrayList<Material>();
    private HashMap<Integer, Tuple<Integer, Integer>> slotPositions = new HashMap<Integer, Tuple<Integer, Integer>>();
    private Sprite highlight = new Sprite(new Texture(Gdx.files.internal("Interface/HUD/craftingSlot2.png")));
    private Sprite outputFail = new Sprite(new Texture(Gdx.files.internal("Interface/HUD/output_fail.png")));
    private Sprite outputSuccess = new Sprite(new Texture(Gdx.files.internal("Interface/HUD/output_success.png")));
    private boolean outputSucc = false;
    private Material craft = null;


    public Crafting(Inventory inventory, MaterialsList materials) {
        this.inventory = inventory;
        this.materials = materials;

        refreshSlots();

        for(Sprite slot : craftingSlots) {
            slotPositions.put(craftingSlots.indexOf(slot) + 1, new Tuple<Integer, Integer>(Math.round(slot.getX() + 25), Math.round(slot.getY() + 25)));
        }

        outputFail.setCenter(280, 100);
        outputSuccess.setCenter(280, 100);

    }

    public void refreshSlots() {
        craftingSlots.clear();

        int xPos = 190;
        int yPos = 350;
        int incr = 1;
        while(incr < 17) {
            Sprite temp = new Sprite(new Texture(Gdx.files.internal("Interface/HUD/craftingSlot.png")));
            temp.setCenter(xPos, yPos);
            craftingSlots.add(temp);
            xPos += 60;
            if(xPos > 400) {
                xPos = 190;
                yPos -= 60;
            }
            incr++;
        }
    }

    public void clearGrid() {
        for(Material material : craftingItems) {
            inventory.addItem(material);
            craftingItemsToRemove.add(material);
        }
    }

    public void craft() {
        ArrayList<Material> craftables = new ArrayList<Material>();
        for(Material material : materials.materialList) {
            if(material.canBeCrafted) {
                craftables.add(material);
            }
        }

        ArrayList<String> temp = new ArrayList<String>();
        ArrayList<String> temp2 = new ArrayList<String>();

        for(Material n : craftingItems) {
            temp.add(n.name);
        }

        ArrayList<String> presserRecipe = new ArrayList<String>() {{
            add("Basic Gears");
            add("Basic Gears");
            add("Basic Gears");
            add("Hardened Stone");
            add("Hardened Stone");
            add("Hardened Stone");
        }};

        ArrayList<String> marketRecipe = new ArrayList<String>() {{
            add("Hardened Wood");
            add("Hardened Wood");
            add("Nails");
            add("Nails");
            add("Hardened Stone");
            add("Hardened Stone");
        }};

        if(presserRecipe.size() == temp.size()) {
            if(temp.containsAll(presserRecipe) && presserRecipe.containsAll(temp)) {
                outputSucc = true;
                craft = new Material("Presser", "Materials/presser.png", "Null", 0, 1);
                craft.setCenter(outputSuccess.getX() + 25, outputSuccess.getY() + 25);
                craftingItems.clear();
                MainScreen.headsUp.player.getLeveling().setSubLevelPoints(MainScreen.headsUp.player.getLeveling().getSubLevelPoints() + 2);
                return;
            }
        }
        if(marketRecipe.size() == temp.size()) {
            if(temp.containsAll(marketRecipe) && marketRecipe.containsAll(temp)) {
                outputSucc = true;
                craft = new Material("Market", "Materials/market.png", "Null", 0, 1);
                craft.setCenter(outputSuccess.getX() + 25, outputSuccess.getY() + 25);
                craftingItems.clear();
                MainScreen.headsUp.player.getLeveling().setSubLevelPoints(MainScreen.headsUp.player.getLeveling().getSubLevelPoints() + 2);
                return;
            }
        }

        for(Material m : craftables) {
            temp2.clear();
            for(Material x : m.recipe) {
                temp2.add(x.name);
            }
            if (temp2.size() == temp.size()) {
                if (temp.containsAll(temp2) && temp2.containsAll(temp)) {
                    Gdx.app.log("Crafting", "Crafting Option: " + m.name);
                    if(!checkDiscovered(m)) {
                        return;
                    } else {
                        craft = new Material(m.name, m.description, m.ID, m.rarity);
                        break;
                    }

                }
            }
        }

        if(craft != null) {
            outputSucc = true;
            MainScreen.headsUp.player.getLeveling().setSubLevelPoints(MainScreen.headsUp.player.getLeveling().getSubLevelPoints() + 1);
            craft.setCenter(outputSuccess.getX() + 25, outputSuccess.getY() + 25);
            craftingItems.clear();
        }
    }

    private boolean checkDiscovered(Material material) {
        for(Material m : materials.materialList) {
            if(m.name.equals(material.name)) {
                if(m.discovered) {
                    return true;
                } else {
                    return false;
                }
            }
        }
        return false;
    }

    public boolean addToSlot(int slot) {
        Material temp = new Material(inventory.followMaterial.stackedItem.name,
                inventory.followMaterial.stackedItem.description, inventory.followMaterial.stackedItem.ID, inventory.followMaterial.stackedItem.rarity);
        temp.setCenter(slotPositions.get(slot).x, slotPositions.get(slot).y);

        for(Material material : craftingItems) {
            if(material.getSprite().getX() == temp.getSprite().getX() && material.getSprite().getY() == temp.getSprite().getY()) {
                Gdx.app.log("Crafting", "Drop Failed");
                return true;
            }
        }

        Gdx.app.log("Crafting", "Drop Successful");
        craftingItems.add(temp);

        /*if(inventory.followMaterial.count > 1) {
            inventory.followMaterial.count -= 1;
        } else {
            inventory.followMaterial = null;
        }*/
        return false;
    }

    public void render(SpriteBatch batch) {

        for(Sprite slot : craftingSlots) {
            if(slot.getBoundingRectangle().contains(Gdx.input.getX(), 800 - Gdx.input.getY())) {
                highlight.setPosition(slot.getX(), slot.getY());
                highlight.draw(batch);
            } else {
                slot.draw(batch);
            }
        }

        for(Material material : craftingItems) {
            material.render(batch);
        }

        for(Material m_ : craftingItemsToRemove) {
            craftingItems.remove(m_);
        }

        if(!outputSucc) {
            outputFail.draw(batch);
        } else {
            outputSuccess.draw(batch);
        }

        if(craft != null) {
            craft.render(batch);
            if(craft.getSprite().getBoundingRectangle().contains(Gdx.input.getX(), 800 - Gdx.input.getY())) {
                if(Gdx.input.justTouched()) {
                    if(craft.name.equals("Presser")) {
                        MainScreen.headsUp.presserUnlocked = true;
                        craft = null;
                        outputSucc = false;
                    } else if(craft.name.equals("Market")) {
                        MainScreen.headsUp.shopUnlocked = true;
                        craft = null;
                        outputSucc = false;
                    } else {
                        inventory.addItem(craft);
                        craft = null;
                        outputSucc = false;
                    }

                }
            }
        }

        craftingItemsToRemove.clear();
    }
}
