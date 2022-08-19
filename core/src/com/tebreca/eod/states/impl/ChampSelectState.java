package com.tebreca.eod.states.impl;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.tebreca.eod.client.NeoClient;
import com.tebreca.eod.common.Player;
import com.tebreca.eod.common.character.Character;
import com.tebreca.eod.common.character.CharacterRegistry;
import com.tebreca.eod.packet.rules.SelectChampRule;
import com.tebreca.eod.states.IGameState;
import org.apache.log4j.Logger;

import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * Called ChampSelect because CharacterSelect is way too long and CharSelect would be confusing
 */
@Singleton
public class ChampSelectState implements IGameState {

    private static final Logger logger = Logger.getLogger(ChampSelectState.class);

    private final Map<Character, Texture> textureSheets;
    private final Map<HitBox, Character> hitBoxes = new HashMap<>();
    private final FreeTypeFontGenerator freeTypeFontGenerator;
    private final BitmapFont titleFont;
    private final BitmapFont namefont;
    private final BitmapFont descriptionfont;
    private final CharacterRegistry characterRegistry;
    Texture picked = new Texture("./textures/picked.png");
    Texture unknown = new Texture("./textures/unknown.png");
    Player[] orangeTeam = new Player[0];
    Player[] purpleTeam = new Player[0];
    Player[] localTeam;
    private NeoClient client;
    boolean dirty = true;
    private String charactername = "";

    private float height;
    private float width;

    Map<Player, Character> playerCharacterMap = new HashMap<>();
    List<Character> claimed = new ArrayList<>();
    Label title;
    private boolean selected = false;
    private long executionTime;

    @Inject
    public ChampSelectState(NeoClient client, FreeTypeFontGenerator fontGenerator, CharacterRegistry registry) {
        this.client = client;
        textureSheets = new HashMap<>();
        client.characterTextureSheets.forEach((c, t) -> textureSheets.put(c, t.get("sprite").orElseThrow()));
        freeTypeFontGenerator = fontGenerator;
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 40;
        titleFont = fontGenerator.generateFont(parameter);
        parameter.size = 20;
        namefont = fontGenerator.generateFont(parameter);
        parameter.size = 12;
        descriptionfont = fontGenerator.generateFont(parameter);
        height = Gdx.graphics.getHeight();
        width = Gdx.graphics.getWidth();

        characterRegistry = registry;
    }


    private boolean isPurple() {
        return !isOrange();
    }

    private boolean isOrange() {
        return Arrays.stream(orangeTeam).anyMatch(p -> p.uuid().equals(client.userID));
    }

    public void setOrangeTeam(Player[] orangeTeam) {
        this.orangeTeam = orangeTeam;
        localTeam = isOrange() ? orangeTeam : purpleTeam;
    }

    public void setPurpleTeam(Player[] purpleTeam) {
        this.purpleTeam = purpleTeam;
        localTeam = isOrange() ? orangeTeam : purpleTeam;
    }

    SpriteBatch spriteBatch = new SpriteBatch();

    @Override
    public String getID() {
        return "champselect";
    }

    @Override
    public void resize(int width, int height) {
        this.height = height;
        this.width = width;
        dirty = true;
    }

    @Override
    public void render() {
        updateMouse();
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        spriteBatch.begin();
        renderView(spriteBatch);
        spriteBatch.end();
    }

    private void updateMouse() {
        if (selected()) {
            return;
        }
        int x = Gdx.input.getX();
        int y = (int) (height - Gdx.input.getY());
        boolean click = Gdx.input.isButtonJustPressed(Input.Buttons.LEFT);
        Optional<Map.Entry<HitBox, Character>> entry =
                hitBoxes.entrySet().stream().filter(e -> e.getKey().contains(x, y)).min((o1, o2) -> {
                    double distance1 = o1.getKey().distance(x, y);
                    double distance2 = o2.getKey().distance(x, y);
                    if (distance1 > distance2) {
                        return 1;
                    } else if (distance1 == distance2) {
                        return 0;
                    }
                    return -1;
                });
        if (entry.isPresent()) {
            if (click) {
                logger.info("Clicked on " + entry.get().getValue().getCodename() + "!");
                client.send(new SelectChampRule(entry.get().getValue().getID(), client.userID), true);
            } else {
                charactername = entry.get().getValue().getCodename();
            }
        }

    }

    private boolean selected() {
        return selected;
    }

    private void renderView(SpriteBatch batch) {
        batch.setColor(Color.WHITE);
        StringBuilder builder = new StringBuilder("Select a character (");
        builder.append(timeleft());
        builder.append(')');
        titleFont.draw(batch, builder.toString(), titleFont.getLineHeight(), height - titleFont.getLineHeight());

        float x = titleFont.getLineHeight() + 10;
        float y = (height * 0.8f);
        for (Player player : localTeam) {
            Texture texture = unknown;
            if (playerCharacterMap.containsKey(player)) {
                texture = textureSheets.get(playerCharacterMap.get(player));
            }
            batch.draw(texture, 10, y - 27, 45, 40);
            namefont.draw(batch, player.username(), x, y);
            y -= 60;
        }

        if (dirty) {
            hitBoxes.clear();
        }

        y = height * 0.3f;
        namefont.draw(batch, charactername, x, y + 100);
        int rowlength = textureSheets.size() / 3;
        int rowId = 1;
        int current = 1;
        int currentRowlengh = rowlength + rowId % 2;
        for (Map.Entry<Character, Texture> entry : textureSheets.entrySet()) {
            Texture texture = entry.getValue();
            Character character = entry.getKey();
            batch.draw(texture, x, y, 90, 80);
            if (claimed.contains(character)) {
                batch.draw(picked, x, y, 90, 80);
            }
            if (dirty) {
                hitBoxes.put(new HitBox(x, y, 90, 80), character);
            }
            x += 144;
            if (++current > currentRowlengh) {
                current = 1;
                rowId++;
                x = titleFont.getLineHeight() + 10 + (rowId == 2 ? 72 : 0);
                currentRowlengh = rowlength + rowId % 2;
                y -= 42;
            }
        }

        if (dirty)
            dirty = false;
    }

    private int timeleft() {
        double ceil = Math.ceil((executionTime - System.currentTimeMillis()) / 1000d);
        return Math.max(0, (int) ceil);
    }

    @Override
    public void enable() {

    }

    @Override
    public void disable() {

    }

    public void selectChamp(SelectChampRule rule) {
        Player player = client.getPlayer(rule.userid());
        Character character = characterRegistry.getEntry(rule.champId());
        playerCharacterMap.put(player, character);
        if (List.of(localTeam).contains(player)) {
            claimed.add(character);
        }
        if (player.uuid().equals(client.userID)) {
            selected = true;
        }
    }

    public void setTimer(long executionTime) {
        this.executionTime = executionTime;
    }


    private static final class HitBox {
        private final float x;
        private final float y;
        private final float height;
        private final float width;
        private final float midX;
        private final float midY;


        private HitBox(float x, float y, float height, float width) {
            midX = x + (width / 2);
            midY = y + (height / 2) - 5;
            this.x = x;
            this.y = y;
            this.height = height;
            this.width = width;
        }

        float midX() {
            return midX;
        }

        float midY() {
            return midY;
        }

        public float x() {
            return x;
        }

        public float y() {
            return y;
        }

        public float height() {
            return height;
        }

        public float width() {
            return width;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == this) return true;
            if (obj == null || obj.getClass() != this.getClass()) return false;
            var that = (HitBox) obj;
            return Float.floatToIntBits(this.x) == Float.floatToIntBits(that.x) &&
                    Float.floatToIntBits(this.y) == Float.floatToIntBits(that.y) &&
                    Float.floatToIntBits(this.height) == Float.floatToIntBits(that.height) &&
                    Float.floatToIntBits(this.width) == Float.floatToIntBits(that.width);
        }

        @Override
        public int hashCode() {
            return Objects.hash(x, y, height, width);
        }

        @Override
        public String toString() {
            return "HitBox[" +
                    "x=" + x + ", " +
                    "y=" + y + ", " +
                    "height=" + height + ", " +
                    "width=" + width + ']';
        }

        boolean contains(float x, float y) {
            float diffX = x - this.x;
            float diffY = y - this.y;
            if (diffX < 0 || diffY < 0) {
                return false;
            }
            diffX -= width;
            diffY -= height;
            if (diffX > 0 || diffY > 0) {
                return false;
            }
            return true;
        }

        /**
         * @return distance to center of this hitbox
         */
        double distance(float x, float y) {
            return Math.sqrt(Math.pow(x - midX, 2) + Math.pow(y - midY(), 2));
        }

    }
}
