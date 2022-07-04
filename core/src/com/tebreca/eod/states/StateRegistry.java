package com.tebreca.eod.states;

import com.tebreca.eod.App;
import com.tebreca.eod.helper.HashRegistry;
import com.tebreca.eod.helper.RegistryHandler;
import com.tebreca.eod.states.impl.MainMenuState;

public class StateRegistry extends HashRegistry<IGameState> {


    public static final StateRegistry INSTANCE = new StateRegistry();

    static {
        RegistryHandler.subscribe(StateRegistry::addEntries, IGameState.class);
    }

    @Override
    protected IGameState[] createArray(int size) {
        return new IGameState[size];
    }

    @Override
    protected Class<IGameState> getTClass() {
        return IGameState.class;
    }


    private static void addEntries(StateRegistry stateRegistry) {
        stateRegistry.register(App.injector.getInstance(MainMenuState.class));
    }


}
