package com.tebreca.eod.states.impl;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Singleton;
import com.kotcrab.vis.ui.VisUI;
import com.tebreca.eod.helper.config.Settings;
import com.tebreca.eod.states.GameStateManager;

import java.io.IOException;

@Singleton
public class SettingsState extends AbstractUIState {

    private final GameStateManager stateManager;
    private final Injector injector;
    private final FreeTypeFontGenerator fontGenerator;
    private final Stage stage = getStage();
    private final Settings settings;
    private Table table = getTable();
    private BitmapFont font;

    @Inject
    protected SettingsState(GameStateManager stateManager, Injector injector, FreeTypeFontGenerator fontGenerator, Settings settings) {
        super(stateManager, injector, fontGenerator, settings);
        this.stateManager = stateManager;
        this.injector = injector;
        this.fontGenerator = fontGenerator;
        this.settings = settings;
    }

    @Override
    public String getID() {
        return "settings";
    }

    @Override
    public void enable() {
        VisUI.load(VisUI.SkinScale.X2);
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.size = 18;
        font = fontGenerator.generateFont(parameter);
        parameter.size = 28;
        BitmapFont titleFont = fontGenerator.generateFont(parameter);
        Gdx.input.setInputProcessor(stage);
        Gdx.input.setInputProcessor(stage);
        Label title = new Label("Settings", new Label.LabelStyle(titleFont, Color.WHITE));
        table.add(title);
        table.row().height(stage.getHeight() * 0.2f);
        Label.LabelStyle labelStyle = new Label.LabelStyle(font, Color.WHITE);
        table.add(new Label("", labelStyle)).size(100, 100);
        table.row();
        Label vsync = new Label("Vsync:", labelStyle);
        Label buttonlabel_1 = new Label(settings.isVsyncEnabled() ? "enabled" : "disabled", labelStyle);
        Button vsyncbutton = new Button(buttonlabel_1, VisUI.getSkin());
        vsyncbutton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                settings.setVsyncEnabled(!settings.isVsyncEnabled());
                buttonlabel_1.setText(settings.isVsyncEnabled() ? "enabled" : "disabled");
            }
        });
        table.add(vsync);
        table.add(vsyncbutton).width(stage.getWidth() * 0.2f);
        table.row();
        Label fullscreen = new Label("Fullscreen mode:", labelStyle);
        Label buttonlabel_2 = new Label(settings.isFullscreen() ? "enabled" : "disabled", labelStyle);
        Button fullscreenButton = new Button(buttonlabel_2, VisUI.getSkin());
        fullscreenButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                settings.setFullscreen(!settings.isFullscreen());
                buttonlabel_2.setText(settings.isFullscreen() ? "enabled" : "disabled");
            }
        });
        table.add(fullscreen);
        table.add(fullscreenButton).width(stage.getWidth() * 0.2f);
        table.row();
        Label displayResolution = new Label("Display Resolution:", labelStyle);
        Label buttonlabel_3 = new Label(settings.getWidth() + "x" + settings.getHeight(), labelStyle);
        Button displaybutton = new Button(buttonlabel_3, VisUI.getSkin());
        displaybutton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                int i = settings.getDisplaySize().ordinal() + 1;
                i = i >= Settings.DisplaySize.values().length ? 0 : i;
                Settings.DisplaySize next = Settings.DisplaySize.values()[i];
                settings.setDisplaySize(next);
                buttonlabel_3.setText(settings.getWidth() + "x" + settings.getHeight());
            }
        });
        table.add(displayResolution);
        table.add(displaybutton).width(stage.getWidth() * 0.2f);
        Button save = new Button(new Label("Save and return to menu", labelStyle), VisUI.getSkin());
        save.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                try {
                    settings.save();
                } catch (IOException e) {
                    e.printStackTrace();//TODO logger
                }
                stateManager.setCurrentState(injector.getInstance(MainMenuState.class));
            }
        });
        table.row();
        table.add(save);
    }

    @Override
    public void disable() {
        VisUI.dispose();
        table.clearChildren();
    }
}
