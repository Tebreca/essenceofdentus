package com.tebreca.eod.client.texture;

import java.util.Objects;

public final class CharacterTextureData {
    private String sprite;
    private String animationmap;

    public CharacterTextureData(String sprite, String animationmap) {
        this.sprite = sprite;
        this.animationmap = animationmap;
    }

    public String sprite() {
        return sprite;
    }

    public String animationmap() {
        return animationmap;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (CharacterTextureData) obj;
        return Objects.equals(this.sprite, that.sprite) &&
                Objects.equals(this.animationmap, that.animationmap);
    }

    @Override
    public int hashCode() {
        return Objects.hash(sprite, animationmap);
    }

    @Override
    public String toString() {
        return "CharacterTextureData[" +
                "sprite=" + sprite + ", " +
                "animationmap=" + animationmap + ']';
    }


}
