package com.tebreca.eod.inject;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.tebreca.eod.states.GameStateManager;
import com.tebreca.eod.states.IGameState;
import com.tebreca.eod.states.impl.MainMenuState;

public class GameModule extends AbstractModule {

    @Singleton
    @Provides
    public GameStateManager getGameStateManager(){
        return new GameStateManager();
    }

    @Override
    protected void configure() {
        //default gamestate to fall back to
        bind(IGameState.class).to(MainMenuState.class);
        super.configure();
    }
}
