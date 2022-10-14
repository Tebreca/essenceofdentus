package com.tebreca.eod.common.logic;

public interface Hitbox {

    boolean hit(double x, double y);

    Position center();

    double radius();

}
