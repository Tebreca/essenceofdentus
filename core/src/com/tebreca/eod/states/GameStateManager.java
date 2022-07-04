package com.tebreca.eod.states;

public class GameStateManager {

    IGameState currentState;

    public IGameState getCurrentState() {
        return currentState;
    }

    public void setCurrentState(IGameState currentState) {
        if (this.currentState != null) {
            this.currentState.disable();
        }
        this.currentState = currentState;
        currentState.enable();
    }

}
