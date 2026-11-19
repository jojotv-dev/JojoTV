package com.liskovsoft.mediaserviceinterfaces.data;

/* JADX INFO: loaded from: classes4.dex */
public interface Command {
    public static final int KEY_BACK = 5;
    public static final int KEY_DOWN = 1;
    public static final int KEY_ENTER = 4;
    public static final int KEY_LEFT = 2;
    public static final int KEY_RIGHT = 3;
    public static final int KEY_UNDEFINED = -1;
    public static final int KEY_UP = 0;
    public static final int TYPE_CONNECTED = 5;
    public static final int TYPE_DISCONNECTED = 6;
    public static final int TYPE_DPAD = 13;
    public static final int TYPE_GET_STATE = 4;
    public static final int TYPE_IDLE = 11;
    public static final int TYPE_NEXT = 8;
    public static final int TYPE_OPEN_VIDEO = 0;
    public static final int TYPE_PAUSE = 3;
    public static final int TYPE_PLAY = 2;
    public static final int TYPE_PREVIOUS = 9;
    public static final int TYPE_SEEK = 1;
    public static final int TYPE_STOP = 12;
    public static final int TYPE_SUBTITLES = 15;
    public static final int TYPE_UNDEFINED = -1;
    public static final int TYPE_UPDATE_PLAYLIST = 7;
    public static final int TYPE_VOICE = 14;
    public static final int TYPE_VOLUME = 10;

    long getCurrentTimeMs();

    int getDelta();

    String getDeviceId();

    String getDeviceName();

    int getKey();

    String getPlaylistId();

    int getPlaylistIndex();

    String getSubLanguageCode();

    int getType();

    String getVideoId();

    int getVolume();

    boolean isVoiceStarted();
}
