package com.tebreca.eod.states.impl;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.google.common.math.Quantiles;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.tebreca.eod.client.NeoClient;
import com.tebreca.eod.common.map.Map;
import com.tebreca.eod.packet.rules.LoadedIntoGameRule;
import com.tebreca.eod.states.IGameState;
import org.apache.log4j.Logger;

@Singleton
public class InGamestate implements IGameState {

    private final NeoClient client;
    private final Map map;
    OrthographicCamera camera;
    SpriteBatch batch;
    Texture texture;
    private Logger logger;

    boolean inputEnabled = false;

    @Inject
    public InGamestate(NeoClient client, Map map) {
        this.camera = new OrthographicCamera(10, 10 * (Gdx.graphics.getWidth() / (float) Gdx.graphics.getHeight()));
        this.batch = new SpriteBatch();
        this.client = client;
        this.texture = new Texture(map.texture());
        this.map = map;
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
        if (inputEnabled)
            handleinput();
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        drawScene();
        batch.end();
    }

    private void handleinput() {
        float x = 0;
        float y = 0;
        float deltaTime = Gdx.graphics.getDeltaTime();
        x -= Gdx.input.isKeyPressed(Input.Keys.A) ? 1 : 0;
        x += Gdx.input.isKeyPressed(Input.Keys.D) ? 1 : 0;
        y -= Gdx.input.isKeyPressed(Input.Keys.S) ? 1 : 0;
        y += Gdx.input.isKeyPressed(Input.Keys.W) ? 1 : 0;
        camera.position.add(x * deltaTime, y * deltaTime, 0);
        camera.update();
    }

    private void drawScene() {
        batch.draw(texture, 0, 0);

    }

    @Override
    public void enable() {
        this.logger = Logger.getLogger(InGamestate.class);
        logger.info("Joining game world");


        //do loading
        client.send(new LoadedIntoGameRule());
    }

    @Override
    public void disable() {

    }

    public void markReady() {
        inputEnabled = true;
        logger.info("Game has Started");
    }
}
