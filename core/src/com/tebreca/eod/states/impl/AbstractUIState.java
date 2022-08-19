package com.tebreca.eod.states.impl;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.tebreca.eod.helper.config.Settings;
import com.tebreca.eod.states.GameStateManager;
import com.tebreca.eod.states.IGameState;

public abstract class AbstractUIState implements IGameState {

    protected final GameStateManager stateManager;
    protected final Injector injector;
    protected final FreeTypeFontGenerator fontGenerator;
    protected Stage stage = new Stage();
    protected Table table = new Table();

    public Stage getStage() {
        return stage;
    }

    public Table getTable() {
        return table;
    }

    @Inject
    protected AbstractUIState(GameStateManager stateManager, Injector injector, FreeTypeFontGenerator fontGenerator, Settings settings) {
        this.stateManager = stateManager;
        this.injector = injector;
        this.fontGenerator = fontGenerator;
        stage.addActor(table);

        table.setDebug(false);
        table.setFillParent(true);
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
        table.setSize(width, height);
    }

    @Override
    public void render() {
        stateManager.checkqueue();
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.act(Gdx.graphics.getDeltaTime());
        stage.draw();
    }

    @Override
    public void disable() {
        table.clearChildren();
    }

}
