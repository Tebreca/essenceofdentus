package com.tebreca.eod.inject;

import com.badlogic.gdx.Files;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;

public class FontModule extends AbstractModule {


    @Singleton
    @Provides
    public FreeTypeFontGenerator getFreeTypeFontGenerator(){
        return new FreeTypeFontGenerator(Gdx.files.getFileHandle("./fonts/sans.ttf", Files.FileType.Local));
    }

}
