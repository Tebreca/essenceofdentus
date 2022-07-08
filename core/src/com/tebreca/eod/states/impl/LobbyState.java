package com.tebreca.eod.states.impl;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Singleton;
import com.kotcrab.vis.ui.VisUI;
import com.tebreca.eod.client.NeoClient;
import com.tebreca.eod.common.Player;
import com.tebreca.eod.server.NeoServer;
import com.tebreca.eod.states.GameStateManager;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Singleton
public class LobbyState extends AbstractUIState{

    private final NeoClient client;
    private BitmapFont font;
    List<Label> players = new ArrayList<>(NeoServer.lobbySize);


    @Inject
    protected LobbyState(GameStateManager stateManager, Injector injector, FreeTypeFontGenerator fontGenerator, NeoClient client) {
        super(stateManager, injector, fontGenerator);
        this.client = client;
    }

    @Override
    public String getID() {
        return "lobby";
    }

    float f = 0;
    @Override
    public void render() {
        f+=Gdx.graphics.getDeltaTime();
        if (f > 0.1){
            updatePlayers(client.getPlayers());
        }
        super.render();
    }

    @Override
    public void enable() {
        VisUI.load(VisUI.SkinScale.X2);
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 18;
        font = fontGenerator.generateFont(parameter);
        parameter.size = 40;
        BitmapFont titleFont = fontGenerator.generateFont(parameter);
        Gdx.input.setInputProcessor(stage);
        Label title = new Label("Lobby", new Label.LabelStyle(titleFont, Color.WHITE));
        title.setColor(Color.WHITE);
        table.add(title);
        table.row().height(stage.getHeight() * 0.2f);
        Label.LabelStyle labelStyle = new Label.LabelStyle(font, Color.WHITE);
        table.add(new Label("", labelStyle)).size(100, 100);
        for(int i = 0; i <NeoServer.lobbySize; i+=2){
            table.row().height(stage.getHeight() * 0.05f).width(stage.getWidth() *0.2f);
            Label label = new Label("No player connected", labelStyle);
            Label label2 = new Label("No player connected", labelStyle);
            this.players.add(label);
            this.players.add(label2);
            table.add(label, label2);
        }
    }

    public void updatePlayers(Player[] players){
        Iterator<Label> labelIterator = this.players.iterator();
        for (Player player : players) {
            labelIterator.next().setText(player.username());
        }
        while (labelIterator.hasNext()){
            labelIterator.next().setText("No player connected");
        }
    }

    @Override
    public void disable() {
        super.disable();
        VisUI.dispose();
    }
}
