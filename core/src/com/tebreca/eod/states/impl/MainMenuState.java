package com.tebreca.eod.states.impl;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.IntSet;
import com.github.czyzby.lml.parser.impl.tag.listener.InputListenerLmlTag;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Singleton;
import com.kotcrab.vis.ui.VisUI;
import com.tebreca.eod.App;
import com.tebreca.eod.helper.config.Settings;
import com.tebreca.eod.states.GameStateManager;

import java.util.logging.Logger;

@Singleton
public class MainMenuState extends AbstractUIState {

    private final GameStateManager stateManager;
    private final Injector injector;
    private final FreeTypeFontGenerator fontGenerator;
    private final Stage stage = getStage();
    private final Table table = getTable();
    private static final Logger logger = Logger.getLogger("MainMenuState.java");
    private BitmapFont font;

    @Inject
    public MainMenuState(GameStateManager stateManager, Injector injector, FreeTypeFontGenerator fontGenerator, Settings settings) {
        super(stateManager, injector, fontGenerator, settings);
        this.stateManager = stateManager;
        this.injector = injector;
        this.fontGenerator = fontGenerator;
    }

    @Override
    public String getID() {
        return "mainmenu";
    }


    @Override
    public void enable() {
        VisUI.load(VisUI.SkinScale.X2);
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 18;
        font = fontGenerator.generateFont(parameter);
        parameter.size = 56;
        BitmapFont titleFont = fontGenerator.generateFont(parameter);
        Gdx.input.setInputProcessor(stage);
        Label title = new Label("Neo-Coliseum", new Label.LabelStyle(titleFont, Color.WHITE));
        title.setColor(Color.WHITE);
        table.add(title);
        table.row().height(stage.getHeight() * 0.2f);
        Label.LabelStyle labelStyle = new Label.LabelStyle(font, Color.WHITE);
        table.add(new Label("", labelStyle)).size(100, 100);
        table.row().height(stage.getHeight() * 0.1f);
        Button settings = new Button(new Label("Settings", new Label.LabelStyle(font, Color.WHITE)), VisUI.getSkin());
        settings.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                stateManager.setCurrentState(injector.getInstance(SettingsState.class));
            }
        });
        table.add(settings).width(stage.getWidth() * 0.2f);
        table.row().height(stage.getHeight() * 0.1f);
        Label host1 = new Label("Host", new Label.LabelStyle(font, Color.WHITE));
        if (App.getInstance().serverEnabled())
            host1.setText("Stop Server");
        Button host = new Button(host1, VisUI.getSkin());
        host.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (App.getInstance().getServerThread() != null) {
                    App.getInstance().stopServer();
                    host1.setText("Host");
                    return;
                }
                App.getInstance().startServer();
                host1.setText("Stop Server");
            }
        });
        table.add(host).width(stage.getWidth() * 0.2f);
        table.row().height(stage.getHeight() * 0.1f);
        Button join = new Button(new Label("Join", new Label.LabelStyle(font, Color.WHITE)), VisUI.getSkin());
        join.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                stateManager.setCurrentState(injector.getInstance(JoinState.class));
            }
        });
        table.add(join).width(stage.getWidth() * 0.2f);
        table.row().height(stage.getHeight() * 0.1f);
        Button exit = new Button(new Label("Exit", new Label.LabelStyle(font, Color.WHITE)), VisUI.getSkin());
        exit.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Gdx.app.exit();
            }
        });
        table.add(exit).width(stage.getWidth() * 0.2f);
        stage.addListener(new InputListenerLmlTag.KeysListener(new IntSet(Input.Keys.ESCAPE)) {
            @Override
            protected void handleEvent(Actor actor) {

            }

            @Override
            public boolean keyDown(InputEvent event, int keycode) {
                if (keycode == Input.Keys.ESCAPE) {
                    Gdx.app.exit();
                }
                return true;
            }
        });
    }

    @Override
    public void render() {
        super.render();
    }

    @Override
    public void disable() {
        super.disable();
        VisUI.dispose();
    }

}