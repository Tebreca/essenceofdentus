package com.tebreca.eod.common.character;

import com.tebreca.eod.helper.HashRegistry;

public class CharacterRegistry extends HashRegistry<Character> {

    @Override
    protected Character[] createArray(int size) {
        return new Character[0];
    }

    @Override
    protected Class<Character> getTClass() {
        return null;
    }

    @Override
    public void addEntries() {
        register(new Character(Essence.BOTANY, "Sterling O’Donnell", "Lann", null, "Republic of Albion", new Ability[0], Role.SUPPORT));
        register(new Character(Essence.LIGHT, "Haru Sakamoto", "Nari", null, "New Japan", new Ability[0], Role.DAMAGE));
        register(new Character(Essence.STONE, "Arne Sauer", "The Land-Lord", null, "Duchy of Bavaria", new Ability[0], Role.TANK));
        register(new Character(Essence.DESERT, "Yasmine Farouk", "Siham", null, "Egypt", new Ability[0], Role.SUPPORT));
        register(new Character(Essence.FLAME, "Bahman Amani", "Taj", null, "Persia", new Ability[0], Role.DAMAGE));
        register(new Character(Essence.WATER, "Park Ji-Hye", "Ji-Heal", null, "United Korea", new Ability[0], Role.SUPPORT));
        register(new Character(Essence.STEEL, "Alderic Duchamp", "Heavy Metal", null, "Monaco", new Ability[0], Role.TANK));
        register(new Character(Essence.BLOOD, "Elena Rosales", "The Bloodslinger", null, "Argentina", new Ability[0], Role.DAMAGE));
        register(new Character(Essence.AIR, "Abey Lewis", "Atsah", null, "Native-American Alliance", new Ability[0], Role.DAMAGE));
        register(new Character(Essence.WINTER, "Zofia Ostrowski", "Dhe Buffer", null, "Lechia", new Ability[0], Role.TANK));
        register(new Character(Essence.NULL, "Hendrik Dentus", "null", null, "???", new Ability[0], Role.TANK));
        register(new Character(Essence.TIME, "Dante Bianchi", "Dantime", null, "Italy", new Ability[0], Role.SUPPORT));

    }
}
