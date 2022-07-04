package com.tebreca.eod.states;

import com.tebreca.eod.helper.IEntry;

public interface IGameState extends IEntry {

    void resize(int width, int height);

    void render();

    void enable();

    void disable();
}
