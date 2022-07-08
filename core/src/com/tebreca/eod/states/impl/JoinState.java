package com.tebreca.eod.states.impl;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Singleton;
import com.kotcrab.vis.ui.VisUI;
import com.tebreca.eod.App;
import com.tebreca.eod.states.GameStateManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

@Singleton
public class JoinState extends AbstractUIState {

    TextField ip;
    private BitmapFont font;
    private Label error;
    private static final Timer timer = new Timer();

    List<TimerTask> clears = new ArrayList<>();

    @Inject
    public JoinState(GameStateManager stateManager, Injector injector, FreeTypeFontGenerator fontGenerator) {
        super(stateManager, injector, fontGenerator);
    }

    @Override
    public String getID() {
        return "join";
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
        Label title = new Label("Connect to a server", new Label.LabelStyle(titleFont, Color.WHITE));
        stage.addActor(title);
        title.setPosition(0, stage.getHeight());
        table.row().height(stage.getHeight() * 0.2f);
        Label.LabelStyle labelStyle = new Label.LabelStyle(font, Color.WHITE);
        table.add(new Label("", labelStyle));
        table.row().height(stage.getHeight() * 0.1f);
        error = new Label("", labelStyle);
        error.setColor(Color.RED);
        table.add(error);
        TextField.TextFieldStyle fieldStyle = VisUI.getSkin().get(TextField.TextFieldStyle.class);
        fieldStyle.font = font;
        fieldStyle.background = null;

        table.row().height(stage.getHeight() * 0.1f);
        Label username1 = new Label("Username:", labelStyle);
        TextField username = new TextField("dentinusdenzanden", fieldStyle);
        username.setAlignment(Align.center);
        table.add(username1);
        table.add(username).width(stage.getWidth() * 0.2f);

        table.row().height(stage.getHeight() * 0.1f);
        Label ip1 = new Label("ip:", labelStyle);
        ip = new TextField("localhost", fieldStyle);
        ip.setAlignment(Align.center);
        Button.ButtonStyle buttonStyle = VisUI.getSkin().get(Button.ButtonStyle.class);
        Button button = new Button(new Label("Go", labelStyle), buttonStyle);
        button.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                App.getInstance().setUsername(username.getText());
                if (ip.getText().equals("localhost") || ip.getText().equals("")) {
                    App.getInstance().connect();
                } else {
                    App.getInstance().connect(ip.getText());
                }
            }
        });
        table.row().height(stage.getHeight() * 0.1f);
        table.add(ip1);
        table.add(ip).width(stage.getWidth() * 0.2f);
        table.row().height(stage.getHeight() * 0.1f);
        Button button1 = new Button(new Label("Back to menu", labelStyle), VisUI.getSkin());
        button1.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                stateManager.setCurrentState(injector.getInstance(MainMenuState.class));
            }
        });
        table.add(button1).width(stage.getWidth() * 0.2f);
        table.add(button).width(stage.getWidth() * 0.2f);
    }

    public void error(String error) {
        this.error.setText(error);

        if (!clears.isEmpty()){
            clears.forEach(TimerTask::cancel);
        }
        timer.purge();

        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                JoinState.this.error.setText("");
            }
        };
        clears.add(task);
        timer.schedule(task, 2000);
    }

    @Override
    public void disable() {
        VisUI.dispose();
        super.disable();
    }
}
