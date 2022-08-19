package com.tebreca.eod.client.texture;

import com.badlogic.gdx.graphics.Texture;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class TextureSheet {

    private Map<String, Texture> textureMap;


    private TextureSheet(Map<String, Texture> textureMap) {
        this.textureMap = textureMap;
    }


    public Optional<Texture> get(String id) {
        return Optional.ofNullable(textureMap.get(id));
    }


    public Texture[] all() {
        return textureMap.values().toArray(Texture[]::new);
    }

    public static class Builder {

        private Map<String, Texture> textureMap = new HashMap<>();

        public Builder() {
        }

        public Builder add(Texture texture, String id) {
            textureMap.put(id, texture);
            return this;
        }

        public TextureSheet build() {
            return new TextureSheet(textureMap);
        }

    }

}
