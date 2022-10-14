package com.tebreca.eod.common.logic;

public class QuadHitbox implements Hitbox{


    private final Position position;
    private final double width;
    private final double height;
    private final Position center;

    public QuadHitbox(Position position, double width, double height){
        this.position = position;
        this.width = width;
        this.height = height;
        center = position.copy();
        center.move(width/2, height/2);

    }



    @Override
    public boolean hit(double x, double y) {
        return false;
    }

    @Override
    public Position center() {
        return null;
    }

    @Override
    public double radius() {
        return 0;
    }
}
