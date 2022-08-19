package com.tebreca.eod.client;

import com.badlogic.gdx.graphics.Texture;
import com.google.gson.Gson;
import com.tebreca.eod.client.texture.CharacterTextureData;
import com.tebreca.eod.client.texture.TextureSheet;
import com.tebreca.eod.common.character.Character;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Locale;
import java.util.Optional;

public class TextureSheetReader {

    private static final Logger logger = Logger.getLogger(TextureSheetReader.class);
    private static final Gson gson = new Gson();

    public static Optional<CharacterTextureData> getForCharacter(Character character) {
        String filename = character.getID().replace(' ', '_').toLowerCase(Locale.ROOT);
        File file = new File("./characters/" + filename + ".json");
        CharacterTextureData data = null;
        try {
            if (!file.exists() && file.createNewFile()) {
                data = new CharacterTextureData("./textures/hexagon.png", ""/*TODO*/);
                FileWriter writer = new FileWriter(file);
                gson.toJson(data, writer);
                writer.close();
            } else {
                FileReader reader = new FileReader(file);
                data = gson.fromJson(reader, CharacterTextureData.class);
                reader.close();
            }

        } catch (IOException e) {
            logger.error("Exception trying to initialize character textures", e);
        }
        return Optional.ofNullable(data);
    }

    public static TextureSheet from(CharacterTextureData data) {
        TextureSheet.Builder builder = new TextureSheet.Builder();
        builder.add(new Texture(data.sprite()), "sprite");
        //builder.add(new Texture(data.animationmap()), "a-map");
        return builder.build();
    }


}
