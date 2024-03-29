package com.tebreca.eod.states;

import com.tebreca.eod.App;
import com.tebreca.eod.helper.HashRegistry;
import com.tebreca.eod.states.impl.*;

public class StateRegistry extends HashRegistry<IGameState> {


    public static final StateRegistry INSTANCE = new StateRegistry();


    @Override
    protected IGameState[] createArray(int size) {
        return new IGameState[size];
    }

    @Override
    protected Class<IGameState> getTClass() {
        return IGameState.class;
    }

    @Override
    public void addEntries() {
        register(App.injector.getInstance(MainMenuState.class));
        register(App.injector.getInstance(SettingsState.class));
        register(App.injector.getInstance(LobbyState.class));
        register(App.injector.getInstance(JoinState.class));
        register(App.injector.getInstance(PreGameState.class));
        register(App.injector.getInstance(ChampSelectState.class));
    }


}
