package com.tebreca.eod.states;

import java.util.ArrayDeque;
import java.util.Queue;
import java.util.function.Supplier;

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

    Queue<Supplier<IGameState>> stateQueue = new ArrayDeque<>();

    public Queue<Supplier<IGameState>> getStateQueue() {
        return stateQueue;
    }

    public void checkqueue() {
        if (!stateQueue.isEmpty()) {
            setCurrentState(stateQueue.poll().get());
        }
    }
}
