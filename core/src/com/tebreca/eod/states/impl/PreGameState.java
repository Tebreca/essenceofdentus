package com.tebreca.eod.states.impl;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Singleton;
import com.kotcrab.vis.ui.VisUI;
import com.tebreca.eod.client.NeoClient;
import com.tebreca.eod.common.Player;
import com.tebreca.eod.helper.config.Settings;
import com.tebreca.eod.packet.rules.JoinTeamRule;
import com.tebreca.eod.server.NeoServer;
import com.tebreca.eod.states.GameStateManager;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@SuppressWarnings("")
@Singleton
public class PreGameState extends AbstractUIState {

    private final Player player;
    private final NeoClient neoClient;
    private BitmapFont font;

    List<Player> team1 = new ArrayList<>();
    List<Player> team2 = new ArrayList<>();

    int teamsize = NeoServer.lobbySize / 2;
    private final List<Label> teamlabels1 = new ArrayList<>();
    private final List<Label> teamlabels2 = new ArrayList<>();
    private Label title;
    private long startTime;
    private Button joinTeam1;
    private Button joinTeam2;

    @Inject
    protected PreGameState(GameStateManager stateManager, Injector injector, FreeTypeFontGenerator fontGenerator, Player player, NeoClient client, Settings settings) {
        super(stateManager, injector, fontGenerator, settings);
        this.player = player;
        neoClient = client;
        if (!player.username().equals(client.username)) {
            player.setUsername(client.username);
        }
    }

    @Override
    public String getID() {
        return "pregame";
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
        title = new Label("Choose a Team", new Label.LabelStyle(titleFont, Color.WHITE));
        table.add(title);
        table.row().height(stage.getHeight() * 0.2f);
        Label.LabelStyle labelStyle = new Label.LabelStyle(font, Color.WHITE);
        table.add(new Label("", labelStyle)).size(100, 100);

        table.row();
        Label team1 = new Label("Orange", new Label.LabelStyle(titleFont, Color.ORANGE));
        Label team2 = new Label("Purple", new Label.LabelStyle(titleFont, Color.PURPLE));
        table.add(team1).width(stage.getWidth() * 0.3f);
        team1.setAlignment(Align.center);
        table.add(team2).width(stage.getWidth() * 0.3f);
        team2.setAlignment(Align.center);
        table.row().height(stage.getHeight() * 0.1f);
        Iterator<Player> playerIterator1 = this.team1.iterator();
        Iterator<Player> playerIterator2 = this.team2.iterator();

        for (int i = 0; i < teamsize; i++) {
            addLabel(labelStyle, playerIterator1, teamlabels1);
            addLabel(labelStyle, playerIterator2, teamlabels2);
            table.row();
        }
        table.row().height(stage.getHeight() * 0.1f);
        this.joinTeam1 = new Button(new Label("Join", labelStyle), VisUI.getSkin());
        this.joinTeam2 = new Button(new Label("Join", labelStyle), VisUI.getSkin());

        joinTeam1.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                neoClient.send(new JoinTeamRule(player.uuid(), (short) 1), true);
            }
        });

        joinTeam2.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                neoClient.send(new JoinTeamRule(player.uuid(), (short) 2), true);
            }
        });
        table.add(joinTeam1).width(stage.getWidth() * 0.2f);
        table.add(joinTeam2).width(stage.getWidth() * 0.2f);

    }

    float time = 0;

    @Override
    public void render() {
        int timeleft = (int) ((startTime - System.currentTimeMillis()) / 1000);
        if (timeleft >= 0) {
            title.setText(String.format("Choose a Team (%s)", timeleft));
        }
        time += Gdx.graphics.getDeltaTime();
        if (time > 0.5) {
            updateTeams();
        }
        super.render();
    }

    private void updateTeams() {
        updateTeam(team1.iterator(), teamlabels1);
        updateTeam(team2.iterator(), teamlabels2);
    }

    private void updateTeam(Iterator<Player> iterator, List<Label> labels) {
        for (Label label : labels) {
            if (iterator.hasNext()) {
                label.setText(iterator.next().username());
            } else {
                label.setText("");
            }
        }
    }

    private void addLabel(Label.LabelStyle labelStyle, Iterator<Player> iterator, List<Label> labelList) {
        Label actor;
        if (iterator.hasNext()) {
            actor = new Label(iterator.next().username(), labelStyle);
        } else {
            actor = new Label("", labelStyle);
        }
        labelList.add(actor);
        table.add(actor);
    }


    public void playerJoins(int team, Player player) {
        if (player.uuid().equals(this.player.uuid())){
            table.removeActor(joinTeam1);
            table.removeActor(joinTeam2);
        }
        List<Player> list = switch (team) {
            case 1 -> team1;
            case 2 -> team2;
            default -> throw new IllegalArgumentException("Team out of index! Expected 1 or 2 but got " + team + " instead!");
        };
        list.add(player);
    }

    public void playerLeaves(Player player) {
        if (!team1.remove(player)) {
            team2.remove(player);
        }
    }

    public void setTimer(long startTime) {
        this.startTime = startTime;

    }
}
