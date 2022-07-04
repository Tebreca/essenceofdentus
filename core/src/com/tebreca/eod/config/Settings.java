package com.tebreca.eod.config;

public class Settings {

    boolean fullscreen = false;

    boolean vsyncEnabled = true;

    DisplaySize displaySize = DisplaySize.H1920_1080;

    public static void save() {
        //TODO
    }


    public boolean isVsyncEnabled() {
        return vsyncEnabled;
    }

    public void setVsyncEnabled(boolean vsyncEnabled) {
        this.vsyncEnabled = vsyncEnabled;
    }

    public boolean isFullscreen() {
        return fullscreen;
    }

    public void setFullscreen(boolean fullscreen) {
        this.fullscreen = fullscreen;
    }

    public int getWidth() {
        return displaySize.getWidth();
    }

    public int getHeight() {
        return displaySize.getHeight();
    }

    public void setDisplaySize(DisplaySize displaySize) {
        this.displaySize = displaySize;
    }

    public DisplaySize getDisplaySize() {
        return displaySize;
    }


    public enum DisplaySize {
        V320_568(320, 568),
        V360_640(360, 640),
        V375_667(375, 667),
        V414_736(414, 736),
        V720_1280(720, 1280),
        V768_1024(768, 1024),
        H1024_768(1024, 768),
        H1280_800(1280, 800),
        H1280_1024(1280, 1024),
        H1366_768(1366, 768),
        H1440_900(1440, 900),
        H1536_864(1536, 864),
        H1600_900(1600, 900),
        H1920_1080(1600, 900),
        H2560_1440(2560, 1440),
        H3840_2160(3840, 2160),
        H4096_2160(4096, 2160);

        private final int width;
        private final int height;

        DisplaySize(int width, int height) {
            this.width = width;
            this.height = height;

        }

        public int getWidth() {
            return width;
        }

        public int getHeight() {
            return height;
        }
    }
}
