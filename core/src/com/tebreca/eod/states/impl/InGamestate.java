package com.tebreca.eod.states.impl;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.tebreca.eod.states.IGameState;

@Singleton
public class InGamestate implements IGameState {

    Camera camera;
    SpriteBatch batch;
    Texture texture = new Texture("textures/testworld.png");

    @Inject
    public InGamestate() {
        this.camera = new OrthographicCamera(Gdx.graphics.getHeight(), Gdx.graphics.getHeight());
        this.batch = new SpriteBatch();
        batch.setProjectionMatrix(camera.combined);
    }

    @Override
    public String getID() {
        return "game";
    }

    @Override
    public void resize(int width, int height) {
        camera.viewportHeight = height;
        camera.viewportWidth = width;
        camera.update();
    }

    @Override
    public void render() {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.begin();
        drawScene();
        batch.end();
    }

    private void drawScene() {
        batch.draw(texture, 0, 0);

    }

    @Override
    public void enable() {

    }

    @Override
    public void disable() {

    }
}
