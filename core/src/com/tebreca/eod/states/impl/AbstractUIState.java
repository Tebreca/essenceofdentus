package com.tebreca.eod.states.impl;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.kotcrab.vis.ui.VisUI;
import com.tebreca.eod.states.GameStateManager;
import com.tebreca.eod.states.IGameState;

public abstract class AbstractUIState implements IGameState {

    private final GameStateManager stateManager;
    private final Injector injector;
    private final FreeTypeFontGenerator fontGenerator;
    private Stage stage = new Stage();
    private Table table = new Table();

    public Stage getStage() {
        return stage;
    }

    public Table getTable() {
        return table;
    }
    @Inject
    protected AbstractUIState(GameStateManager stateManager, Injector injector, FreeTypeFontGenerator fontGenerator) {
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

    }

    @Override
    public void render() {
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.act(Gdx.graphics.getDeltaTime());
        stage.draw();
    }

    @Override
    public void disable() {
        table.clearChildren();
    }

}
