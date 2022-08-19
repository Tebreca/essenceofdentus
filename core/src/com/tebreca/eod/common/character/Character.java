package com.tebreca.eod.common.character;

import com.tebreca.eod.helper.IEntry;

import java.util.Objects;

public class Character implements IEntry {

    Essence essence;
    String name;
    String codename;
    Weapon weapon;
    /**
     * Country of origin
     */
    String origin;
    Ability[] abilities;
    Role role;

    public Role getRole() {
        return role;
    }

    public Character(Essence essence, String name, String codename, Weapon weapon, String origin, Ability[] abilities, Role role) {
        this.essence = essence;
        this.name = name;
        this.codename = codename;
        this.weapon = weapon;
        this.origin = origin;
        this.abilities = abilities;
        this.role = role;
    }

    public Essence getEssence() {
        return essence;
    }

    public String getName() {
        return name;
    }

    public String getCodename() {
        return codename;
    }

    public Weapon getWeapon() {
        return weapon;
    }

    public String getOrigin() {
        return origin;
    }

    public Ability[] getAbilities() {
        return abilities;
    }

    @Override
    public String getID() {
        return codename;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Character character = (Character) o;
        return Objects.equals(getID(), character.getID());
    }

    @Override
    public int hashCode() {
        return Objects.hash(codename);
    }
}
