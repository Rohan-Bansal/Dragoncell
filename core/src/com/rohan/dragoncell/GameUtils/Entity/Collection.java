package com.rohan.dragoncell.GameUtils.Entity;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.FileTextureData;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.ScreenUtils;
import com.rohan.dragoncell.FileUtils.Tuple;
import com.rohan.dragoncell.GameScenes.MainScreen;
import com.rohan.dragoncell.GameUtils.Entity.Mob.Cow;
import com.rohan.dragoncell.GameUtils.Entity.Mob.PassiveMob;
import com.rohan.dragoncell.GameUtils.Entity.Object.BreakableObject;
import com.rohan.dragoncell.GameUtils.ItemStack;
import com.rohan.dragoncell.GameUtils.Material;
import com.rohan.dragoncell.GameUtils.MaterialsList;
import com.rohan.dragoncell.GameUtils.ObtainMethods;

import java.awt.geom.Ellipse2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Queue;
import java.util.Random;

import static com.rohan.dragoncell.GameUtils.ObtainMethods.rooms;

public class Collection {

    private Player player;
    private MaterialsList materials;
    private ArrayList<BreakableObject> trees = new ArrayList<BreakableObject>();
    private ArrayList<BreakableObject> desert = new ArrayList<BreakableObject>();
    private ArrayList<BreakableObject> ores = new ArrayList<BreakableObject>();
    private ArrayList<BreakableObject> treesToRemove = new ArrayList<BreakableObject>();
    private ArrayList<BreakableObject> oresToRemove = new ArrayList<BreakableObject>();
    private ArrayList<BreakableObject> desertToRemove = new ArrayList<BreakableObject>();
    private HashMap<Tuple<Integer, Integer>, Float> digTime = new HashMap<Tuple<Integer, Integer>, Float>();
    private HashMap<Tuple<Integer, Integer>, Material> digResults = new HashMap<Tuple<Integer, Integer>, Material>();
    private ArrayList<Tuple<Integer, Integer>> digResultsToRemove = new ArrayList<Tuple<Integer, Integer>>();
    private ArrayList<Tuple<Integer, Integer>> digTimeToRemove = new ArrayList<Tuple<Integer, Integer>>();

    public ArrayList<PassiveMob> animals = new ArrayList<PassiveMob>();
    private ArrayList<PassiveMob> animalsToRemove = new ArrayList<PassiveMob>();

    private Random rand = new Random();
    private Sprite axeIcon = new Sprite(new Texture(Gdx.files.internal("Interface/World/Collection/axe.png")));
    private Sprite hammerIcon = new Sprite(new Texture(Gdx.files.internal("Interface/World/Collection/hammer.png")));
    private BitmapFont timeDrawer = new BitmapFont(Gdx.files.internal("Fonts/ari2.fnt"), Gdx.files.internal("Fonts/ari2.png"), false);


    public int[] roomCoords = {3, 2};

    private int biomeType = 1;
    private int prevBiomeType = 1;
    private Rectangle tempRect;
    private Color tempColor;

    private int spawnOre_ = 0;
    private int spawnCactus_ = 0;
    private int spawnTree_ = 0;


    private BitmapFont nameDrawer = new BitmapFont(Gdx.files.internal("Fonts/ari2.fnt"), Gdx.files.internal("Fonts/ari2.png"), false);
    private BitmapFont noPickUpDrawer = new BitmapFont(Gdx.files.internal("Fonts/ari2.fnt"), Gdx.files.internal("Fonts/ari2.png"), false);
    private GlyphLayout layout = new GlyphLayout();

    public Collection(MaterialsList materials, Player player) {
        this.player = player;
        this.materials = materials;

        nameDrawer.getData().setScale(0.8f);

        noPickUpDrawer.setColor(Color.SCARLET);
        noPickUpDrawer.getData().setScale(0.5f);
        hammerIcon.setScale(1.5f);

        timeDrawer.getData().setScale(0.5f);

        refreshView();
    }

    private void generateCows() {
        if(animals.size() == 0) {
            for(int i = 0; i < rand.nextInt(6); i++) {
                PassiveMob temp = new Cow("Cow", player, materials);
                recursiveMobChange(temp, 1);
                animals.add(temp);
            }

        }
    }

    public void render(SpriteBatch batch) {

        detectRoomChange();
        //detectSceneChange();

        for(PassiveMob mob : animals) {
            mob.render(batch);
            if(mob.hits != 0) {
                nameDrawer.draw(batch, mob.hits + "", mob.position.x + 67, mob.position.y + 25);
                if(mob.hits == 18) {
                    animalsToRemove.add(mob);
                    mob.kill();
                }
            }
            if(player.getRect().overlaps(mob.getRect())) {
                nameDrawer.setColor(Color.GOLDENROD);
                nameDrawer.getData().setScale(0.7f);
                layout.setText(nameDrawer, mob.name);
                nameDrawer.draw(batch, mob.name, mob.position.x + (mob.getRect().getWidth() / 2) - layout.width / 2, mob.position.y + 57);
                try {
                    if (player.getInventory().getInventory().get(player.getInventory().getSlotSelected() - 1).stackedItem.name.toLowerCase().equals("knight sword")) {
                        if(Gdx.input.isKeyJustPressed(Input.Keys.E)) {
                            mob.hits += 1;
                            mob.speed += 0.1f;
                        }
                    }
                } catch (IndexOutOfBoundsException e) {
                }

            }
        }

        nameDrawer.setColor(Color.WHITE);
        nameDrawer.getData().setScale(1f);


        for(BreakableObject tree : trees) {
            if(biomeType == 1 || biomeType == 2) {
                tree.render(batch);
            }
        }

        for(BreakableObject ore : ores) {
            if(biomeType == 5) {
                ore.render(batch);
                ore.unobtainable = false;
            }
        }

        for(BreakableObject cactus : desert) {
            if(biomeType == 3) {
                cactus.render(batch);
            }
        }

        for(int x = 0; x < spawnOre_; x++) {
            spawnOre();
            spawnOre_ -= 1;
        }
        for(int x = 0; x < spawnCactus_; x++) {
            spawnCactus();
            spawnCactus_ -= 1;
        }
        for(int x = 0; x < spawnTree_; x++) {
            spawnTree();
            spawnTree_ -= 1;
        }

        player.renderPlayer(batch);
        tempRect = new Rectangle(player.position.x, player.position.y, player.currentFrame.getRegionWidth(), player.currentFrame.getRegionHeight());

        for(BreakableObject ore2 : ores) {
            if(biomeType == 5) {
                if(ore2.sprite.getBoundingRectangle().overlaps(tempRect)) {
                    if(materials.getMaterialByID(ore2.ID).levelNeeded > player.getLeveling().getLevel()) {
                        ore2.unobtainable = true;
                        noPickUpDrawer.draw(batch, "Level " + materials.getMaterialByID(ore2.ID).levelNeeded, ore2.sprite.getX() - 10, ore2.sprite.getY() + 45);
                    }
                }
            }
        }

        if(biomeType == 2) {
            int pixel = ScreenUtils.getFrameBufferPixmap(0,0, 1000, 800).getPixel(Math.round(player.position.x), Math.round(player.position.y));
            tempColor = new Color(pixel);
            if(tempColor.r * 255 == 0.0 && tempColor.g * 255 == 74.0 && tempColor.b * 255 == 127.0) {
                player.speed = 0.4f;
                player.position.x += 0.3f;
            } else {
                player.speed = 1.5f;
            }
        }

        if(biomeType == 1 || biomeType == 2) {
            treeBiome(batch, tempRect);
        } else if(biomeType == 5) {
            animals.clear();
            oreBiome(batch, tempRect);
        } else if(biomeType == 3) {
            animals.clear();
            desertBiome(batch, tempRect);
        } else if(biomeType == 4) {
            animals.clear();
        }

        if(biomeType == 1) {
            if(animals.size() == 0) {
                generateCows();
            }
        } else if(biomeType == 2) {
            if(animals.size() == 0) {
                generateCows();
            }
        }

        for(BreakableObject t : treesToRemove) {
            trees.remove(t);
        }

        for(BreakableObject t : oresToRemove) {
            ores.remove(t);
        }

        for(BreakableObject t : desertToRemove) {
            desert.remove(t);
        }

        for(PassiveMob t : animalsToRemove) {
            animals.remove(t);
        }

        if(digTime != null && digResults != null) {
            for(Tuple<Integer, Integer> coords : digTime.keySet()) {

                if(digTime.get(coords) <= 0) {
                    for(Tuple<Integer, Integer> coords_2 : digResults.keySet()) {
                        if((coords_2.x + " " + coords_2.y).equals(coords.x + " " + coords.y)) {
                            player.getInventory().addItem(digResults.get(coords_2));
                            digResultsToRemove.add(coords_2);
                            //digResults.remove(coords_2);
                        }
                    }
                    //digTime.remove(coords);
                    digTimeToRemove.add(coords);
                    break;
                } else {
                    timeDrawer.draw(batch, ObtainMethods.round((double) digTime.get(coords), 1) + "", coords.x, coords.y);
                    digTime.put(coords, digTime.get(coords) - 0.02f);
                }

            }
        }

        if(digTime != null && digTimeToRemove != null) {
            for(Tuple<Integer, Integer> coords : digTimeToRemove) {
                for(Tuple<Integer, Integer> coords_2 : digTime.keySet()) {
                    if((coords_2.x + " " + coords_2.y).equals(coords.x + " " + coords.y)) {
                        digTime.remove(coords_2);
                    }
                }
            }
        }
        if(digResults != null && digResultsToRemove != null) {
            for(Tuple<Integer, Integer> coords : digResultsToRemove) {
                for(Tuple<Integer, Integer> coords_2 : digResults.keySet()) {
                    if((coords_2.x + " " + coords_2.y).equals(coords.x + " " + coords.y)) {
                        digResults.remove(coords_2);
                    }
                }
            }
        }


        digResultsToRemove.clear();
        digTimeToRemove.clear();
        animalsToRemove.clear();

        treesToRemove.clear();
        oresToRemove.clear();
        desertToRemove.clear();

    }

    private void treeBiome(SpriteBatch batch, Rectangle tempRect) {
        if (Gdx.input.isKeyJustPressed(Input.Keys.E)) {
            for (BreakableObject tree_ : trees) {
                if (tempRect.overlaps(tree_.sprite.getBoundingRectangle())) {
                    if (tree_.hits == 18) {
                        treesToRemove.add(tree_);
                        player.getLeveling().setSubLevelPoints(player.getLeveling().getSubLevelPoints() + 1);
                        String path = ((FileTextureData) tree_.sprite.getTexture().getTextureData()).getFileHandle().path();
                        if (path.contains("tree2")) {
                            player.getInventory().addItem(new Material(materials.CHERRY), rand.nextInt(2));
                            discoveredItem(materials.CHERRY);
                        } else if (path.contains("tree4")) {
                            player.getInventory().addItem(new Material(materials.GREEN_APPLE), rand.nextInt(2));
                            discoveredItem(materials.GREEN_APPLE);
                        } else if (path.contains("tree1")) {
                            player.getInventory().addItem(new Material(materials.STICK), rand.nextInt(3));
                        }
                        spawnTree_ += 1;
                    } else {
                        tree_.hits += 1;
                    }
                }
            }
            if (spawnTree_ == 0) {
                if (player.getInventory().getInventory().get(player.getInventory().getSlotSelected() - 1).stackedItem.name.toLowerCase().equals("spade")) {
                    if (digTime.size() == 0) {
                        digTime.put(new Tuple<Integer, Integer>(Math.round(player.position.x), Math.round(player.position.y)), 2.0f);
                        digResults.put(new Tuple<Integer, Integer>(Math.round(player.position.x), Math.round(player.position.y)), new Material(materials.DIRT));
                    }

                }
            }
        }
        for (BreakableObject object_ : trees) {
            if (biomeType == 1 || biomeType == 2) {
                Rectangle tempRect_ = new Rectangle(player.position.x, player.position.y, player.currentFrame.getRegionWidth(), player.currentFrame.getRegionHeight());
                if (tempRect_.overlaps(object_.sprite.getBoundingRectangle())) {
                    axeIcon.setCenter(object_.sprite.getX() + (object_.sprite.getWidth() / 2), object_.sprite.getY() + 110);
                    axeIcon.draw(batch);
                    if (object_.hits != 0) {
                        nameDrawer.draw(batch, object_.hits + "", object_.sprite.getX() + 90, object_.sprite.getY() + 50);
                    }
                }
            }
        }
    }

    private void desertBiome(SpriteBatch batch, Rectangle tempRect) {
        if(Gdx.input.isKeyJustPressed(Input.Keys.E)) {
            for (BreakableObject cact : desert) {
                if(!cact.unobtainable) {
                    if (tempRect.overlaps(cact.sprite.getBoundingRectangle())) {
                        if (cact.hits == 7) {
                            desertToRemove.add(cact);
                            player.getLeveling().setSubLevelPoints(player.getLeveling().getSubLevelPoints() + 1);
                            String path = ((FileTextureData) cact.sprite.getTexture().getTextureData()).getFileHandle().path();
                            if (path.contains("cactus")) {
                                player.getInventory().addItem(new Material(materials.CACTUS));
                                discoveredItem(materials.CACTUS);
                            }
                            spawnCactus_ += 1;
                        } else {
                            cact.hits += 1;
                        }
                    }
                }
            }
            if(spawnCactus_ == 0) {
                if(player.getInventory().getInventory().get(player.getInventory().getSlotSelected() - 1).stackedItem.name.toLowerCase().equals("spade")) {
                    if(digTime.size() == 0) {
                        digTime.put(new Tuple<Integer, Integer>(Math.round(player.position.x), Math.round(player.position.y)), 2.5f);
                        digResults.put(new Tuple<Integer, Integer>(Math.round(player.position.x), Math.round(player.position.y)), new Material(materials.SAND));
                        if(!materials.SAND.discovered) {
                            discoveredItem(materials.SAND);
                        }
                    }

                }
            }
        }
        for(BreakableObject object_ : desert) {
            if(biomeType == 3) {
                if(!object_.unobtainable) {
                    Rectangle tempRect_ = new Rectangle(player.position.x, player.position.y, player.currentFrame.getRegionWidth(), player.currentFrame.getRegionHeight());
                    if (tempRect_.overlaps(object_.sprite.getBoundingRectangle())) {
                        axeIcon.setCenter(object_.sprite.getX() + (object_.sprite.getWidth() / 2), object_.sprite.getY() + 75);
                        axeIcon.draw(batch);
                        if(object_.hits != 0) {
                            nameDrawer.draw(batch, object_.hits + "", object_.sprite.getX() + 35, object_.sprite.getY() + 45);
                        }
                    }
                }

            }
        }
    }
    private void oreBiome(SpriteBatch batch, Rectangle tempRect) {
        if(Gdx.input.isKeyJustPressed(Input.Keys.E)) {
            for (BreakableObject ore_ : ores) {
                if (tempRect.overlaps(ore_.sprite.getBoundingRectangle())) {
                    if(!ore_.unobtainable) {
                        if (ore_.hits == ore_.material.hitsNeeded) {
                            oresToRemove.add(ore_);
                            player.getLeveling().setSubLevelPoints(player.getLeveling().getSubLevelPoints() + 1);
                            if (ore_.material.name.equals(materials.COAL.name)) {
                                player.getInventory().addItem(new Material(materials.COAL));
                                discoveredItem(materials.COAL);
                            } else if (ore_.material.name.equals(materials.IRON_ORE.name)) {
                                player.getInventory().addItem(new Material(materials.IRON_ORE));
                                discoveredItem(materials.IRON_ORE);
                            } else if (ore_.material.name.equals(materials.CRIMSTONE_ORE.name)) {
                                player.getInventory().addItem(new Material(materials.CRIMSTONE_ORE));
                                discoveredItem(materials.CRIMSTONE_ORE);
                            } else if (ore_.material.name.equals(materials.STONE.name)) {
                                player.getInventory().addItem(new Material(materials.STONE));
                                discoveredItem(materials.STONE);
                            } else if (ore_.material.name.equals(materials.SASMITE_ORE.name)) {
                                player.getInventory().addItem(new Material(materials.SASMITE_ORE));
                                discoveredItem(materials.SASMITE_ORE);
                            } else if (ore_.material.name.equals(materials.COPPER_ORE.name)) {
                                player.getInventory().addItem(new Material(materials.COPPER_ORE));
                                discoveredItem(materials.COPPER_ORE);
                            } else if (ore_.material.name.equals(materials.AMBER.name)) {
                                player.getInventory().addItem(new Material(materials.AMBER));
                                discoveredItem(materials.AMBER);
                            }
                            spawnOre_ += 1;
                        } else {
                            ore_.hits += 1;
                        }
                    }
                }
            }
        }
        for(BreakableObject object_ : ores) {
            if(biomeType == 5) {
                if(!object_.unobtainable) {
                    Rectangle tempRect_ = new Rectangle(player.position.x, player.position.y, player.currentFrame.getRegionWidth(), player.currentFrame.getRegionHeight());
                    if (tempRect_.overlaps(object_.sprite.getBoundingRectangle())) {
                        hammerIcon.setCenter(object_.sprite.getX() + (object_.sprite.getWidth() / 2), object_.sprite.getY() + 40);
                        hammerIcon.draw(batch);
                        if(object_.hits != 0) {
                            nameDrawer.draw(batch, object_.hits + "", object_.sprite.getX() + 40, object_.sprite.getY() + 16);
                        }
                    }
                }

            }
        }
    }


    private void detectRoomChange() {
        if(rooms[roomCoords[0]][roomCoords[1]] != 0) {
            try {
                if(player.position.x > 490) { // right
                    if(checkPossible(rooms[roomCoords[0]][roomCoords[1] + 1])) {
                        roomCoords[1] += 1;
                        prevBiomeType = biomeType;
                        biomeType = rooms[roomCoords[0]][roomCoords[1]];
                        refreshView();
                        MainScreen.headsUp.changeCollectionScene(biomeType);
                        player.position.x = 50;
                    }

                } else if(player.position.x < 40) { // left

                    if(checkPossible(rooms[roomCoords[0]][roomCoords[1] - 1])) {
                        roomCoords[1] -= 1;
                        prevBiomeType = biomeType;
                        biomeType = rooms[roomCoords[0]][roomCoords[1]];
                        refreshView();
                        MainScreen.headsUp.changeCollectionScene(biomeType);
                        player.position.x = 480;
                    }

                } else if(player.position.y < 40) { // down

                    if(checkPossible(rooms[roomCoords[0] + 1][roomCoords[1]])) {
                        roomCoords[0] += 1;
                        prevBiomeType = biomeType;
                        biomeType = rooms[roomCoords[0]][roomCoords[1]];
                        refreshView();
                        MainScreen.headsUp.changeCollectionScene(biomeType);
                        player.position.y = 430;
                    }

                } else if(player.position.y > 439) { // up

                    if(checkPossible(rooms[roomCoords[0] - 1][roomCoords[1]])) {
                        roomCoords[0] -= 1;
                        prevBiomeType = biomeType;
                        biomeType = rooms[roomCoords[0]][roomCoords[1]];
                        refreshView();
                        MainScreen.headsUp.changeCollectionScene(biomeType);
                        player.position.y = 50;
                    }

                }
            } catch (ArrayIndexOutOfBoundsException e) {
                Gdx.app.log("Scene Loading Error", "End Of World");
            }


        }
    }

    private boolean checkPossible(int biome) {
        Gdx.app.log("Collection", "Checking Possible Biome");

        //int tempBiomeType = Integer.parseInt(areas.get(areaNumber)[biome].split(" ")[0]);
        int tempBiomeType = biome;
        if(ObtainMethods.getBiomeByInt.get(tempBiomeType).equals("Desert")) {
            if (!player.desertUnlocked) {
                for (ItemStack item : player.getInventory().getInventory()) {
                    if (item.stackedItem.name.equals(materials.getMaterialByID(Integer.parseInt(player.desertRequirement[0])).name)) {
                        if (item.count >= Integer.parseInt(player.desertRequirement[1])) {
                            player.desertUnlocked = true;
                            if(item.count > Integer.parseInt(player.desertRequirement[1])) {
                                item.count -= Integer.parseInt(player.desertRequirement[1]);
                            } else {
                                player.getInventory().materialsToRemove.add(item);
                            }
                            Gdx.app.log("Collection", "Unlocked Desert Area");
                            discoveredItem(materials.WATER, "Desert");
                            return true;
                        }
                    }
                }
                discoveredItem(materials.WATER, "Area Locked", materials.getMaterialByID(Integer.parseInt(player.desertRequirement[0])).name + ": " +
                        Integer.parseInt(player.desertRequirement[1]));
            } else {
                return true;
            }
        } else if(ObtainMethods.getBiomeByInt.get(tempBiomeType).equals("Beach")) {
            if (!player.beachUnlocked) {
                for (ItemStack item : player.getInventory().getInventory()) {
                    if (item.stackedItem.name.equals(materials.getMaterialByID(Integer.parseInt(player.beachRequirement[0])).name)) {
                        if (item.count >= Integer.parseInt(player.beachRequirement[1])) {
                            player.beachUnlocked = true;
                            if(item.count > Integer.parseInt(player.beachRequirement[1])) {
                                item.count -= Integer.parseInt(player.beachRequirement[1]);
                            } else {
                                player.getInventory().materialsToRemove.add(item);
                            }
                            Gdx.app.log("Collection", "Unlocked Beach Area");
                            discoveredItem(materials.WATER, "Beach");
                            return true;
                        }
                    }
                }
                discoveredItem(materials.WATER, "Area Locked", materials.getMaterialByID(Integer.parseInt(player.beachRequirement[0])).name + ": " +
                        Integer.parseInt(player.beachRequirement[1]));
            } else {
                return true;
            }
        } else if(ObtainMethods.getBiomeByInt.get(tempBiomeType).equals("Ore Field")) {

            if(!player.oreFieldUnlocked) {
                player.oreFieldUnlocked = true;
                discoveredItem(materials.WATER, "Ore Field");
                return true;
            } else {
                return true;
            }
        } else {
            Gdx.app.log("Collection", ObtainMethods.getBiomeByInt.get(tempBiomeType));
            return true;
        }
        return false;
    }

    private void recursiveMobChange(PassiveMob mob, int scenario) {
        if(scenario == 1) {
            if (mob.position.y > 400 && mob.position.y < 30) {
                mob.position.set(new Vector2(rand.nextInt((460 - 40) + 1) + 40, rand.nextInt((400 - 30) + 1) + 30));
                for(BreakableObject object : trees) {
                    if(object.sprite.getBoundingRectangle().overlaps(mob.getRect())) {
                        recursiveMobChange(mob, 1);
                    }
                }
                recursiveMobChange(mob, 1);
            }
        }
    }

    private void recursivePosChange(BreakableObject object, int scenario) {

        if(scenario == 1) {
            if(object.sprite.getY() < 325 && object.sprite.getY() > 175) {
                object.sprite.setPosition(rand.nextInt((460 - 40) + 1) + 40, rand.nextInt((400 - 30) + 1) + 30);
                recursivePosChange(object, 1);
            }
        } else if(scenario == 2) {
            for(BreakableObject tree : trees) {
                if(tree.sprite.getBoundingRectangle().overlaps(object.sprite.getBoundingRectangle())) {
                    object.sprite.setPosition(rand.nextInt((460 - 40) + 1) + 40, rand.nextInt((400 - 30) + 1) + 30);
                    recursivePosChange(object, 2);
                }
            }
        } else if(scenario == 3) {
            for(BreakableObject ore : ores) {
                if(ore.sprite.getBoundingRectangle().overlaps(object.sprite.getBoundingRectangle())) {
                    object.sprite.setPosition(rand.nextInt((460 - 40) + 1) + 40, rand.nextInt((400 - 30) + 1) + 30);
                    recursivePosChange(object, 3);
                }
            }
        } else if(scenario == 4) {
            for(BreakableObject cact : desert) {
                if(cact.sprite.getBoundingRectangle().overlaps(object.sprite.getBoundingRectangle())) {
                    object.sprite.setPosition(rand.nextInt((460 - 40) + 1) + 40, rand.nextInt((400 - 30) + 1) + 30);
                    recursivePosChange(object, 3);
                }
            }
        }
    }

    private void discoveredItem(Material material, String... discoveredBiome) {
        if(discoveredBiome.length == 1) {
            MainScreen.headsUp.alert.put("alert_text", "New Area Unlocked");
            MainScreen.headsUp.alert.put("alert_description", discoveredBiome[0]);
        } else if(discoveredBiome.length == 2) {
            MainScreen.headsUp.alert.put("alert_text", discoveredBiome[0]);
            MainScreen.headsUp.alert.put("alert_description", discoveredBiome[1]);
        } else {
            if(!material.discovered) {
                material.discovered = true;
                MainScreen.headsUp.alert.put("alert_text", "New Material Chain");
                MainScreen.headsUp.alert.put("alert_description", material.name + " : ID #" + material.ID);
                unlockItems(material);
            }
        }

    }

    public void unlockItems(Material material) {
        for(Material material_ : materials.materialList) {
            if(material_.recipe.contains(material) || material.smeltInto == material_ || material.seedDrop == material_ || material.juicedInto == material_
                    || material_.grinderRecipe.contains(material)) {
                material_.discovered = true;
                unlockItems(material_);
            }
        }
    }

    public void refreshView() {
        trees.clear();
        ores.clear();
        desert.clear();

        for(int x = 0; x < 5; x++) {
            spawnTree();
        }

        for(int x = 0; x < 10; x++) {
            spawnCactus();
        }

        for(int x = 0; x < 15; x++) {
            spawnOre();
        }
    }

    private void spawnTree() {
        BreakableObject tempTree = null;
        int spawnPercentage = rand.nextInt(100);
        if(spawnPercentage <= 60) {
            tempTree = new BreakableObject("Interface/World/Collection/tree1.png", 0);
        } else if(spawnPercentage <= 80) {
            tempTree = new BreakableObject("Interface/World/Collection/tree2.png", 0);
        } else if(spawnPercentage <= 100) {
            tempTree = new BreakableObject("Interface/World/Collection/tree4.png", 0);
        }
        tempTree.sprite.setPosition(rand.nextInt((460 - 40) + 1) + 40, rand.nextInt((400 - 30) + 1) + 30);
        recursivePosChange(tempTree, 2);

        trees.add(tempTree);
    }

    private void spawnCactus() {
        BreakableObject tempCact = new BreakableObject("Interface/World/Collection/cactus.png", 46);
        tempCact.sprite.setPosition(rand.nextInt((460 - 40) + 1) + 40, rand.nextInt((400 - 30) + 1) + 30);
        recursivePosChange(tempCact, 4);

        desert.add(tempCact);
    }

    private void spawnOre() {
        BreakableObject tempOre = null;
        int spawnPercentage = rand.nextInt(100);
        if(spawnPercentage <= 40) {
            tempOre = new BreakableObject("Materials/stone.png", 2, new Material(materials.STONE));
            tempOre.sprite.setSize(32, 32);
        } else if(spawnPercentage <= 60) {
            tempOre = new BreakableObject("Materials/coal.png", 8, new Material(materials.COAL));
            tempOre.sprite.setSize(32, 32);
        } else if(spawnPercentage <= 75) {
            tempOre = new BreakableObject("Materials/iron_ore.png", 11, new Material(materials.IRON_ORE));
            tempOre.sprite.setSize(32, 32);
        } else if(spawnPercentage <= 77) {
            tempOre = new BreakableObject("Materials/amber.png", 3, new Material(materials.AMBER));
            tempOre.sprite.setSize(32, 32);
        } else if(spawnPercentage <= 90) {
            tempOre = new BreakableObject("Materials/copper_ore.png", 24, new Material(materials.COPPER_ORE));
            tempOre.sprite.setSize(32, 32);
        } else if(spawnPercentage <= 97) {
            tempOre = new BreakableObject("Materials/sasmite_ore.png", 6, new Material(materials.SASMITE_ORE));
            tempOre.sprite.setSize(32, 32);
        } else if(spawnPercentage <= 100) {
            tempOre = new BreakableObject("Materials/crimstone_ore.png", 9, new Material(materials.CRIMSTONE_ORE));
            tempOre.sprite.setSize(32, 32);
        }
        tempOre.sprite.setPosition(rand.nextInt((460 - 40) + 1) + 40, rand.nextInt((440 - 30) + 1) + 30);
        recursivePosChange(tempOre, 3);
        ores.add(tempOre);
    }
}
