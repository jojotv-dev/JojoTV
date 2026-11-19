package com.liskovsoft.youtubeapi.lounge.models.commands;

import com.liskovsoft.googlecommon.common.converters.jsonpath.JsonPath;
import java.util.List;
import w.h0;

/* JADX INFO: loaded from: classes4.dex */
public class CommandItem {
    public static final String KEY_BACK = "BACK";
    public static final String KEY_DOWN = "DOWN";
    public static final String KEY_ENTER = "ENTER";
    public static final String KEY_LEFT = "LEFT";
    public static final String KEY_RIGHT = "RIGHT";
    public static final String KEY_UP = "UP";
    public static final String TYPE_DPAD = "dpadCommand";
    public static final String TYPE_GET_NOW_PLAYING = "getNowPlaying";
    public static final String TYPE_G_SESSION_ID = "S";
    public static final String TYPE_LOUNGE_STATUS = "loungeStatus";
    public static final String TYPE_NEXT = "next";
    public static final String TYPE_NOOP = "noop";
    public static final String TYPE_PAUSE = "pause";
    public static final String TYPE_PLAY = "play";
    public static final String TYPE_PREVIOUS = "previous";
    public static final String TYPE_REMOTE_CONNECTED = "remoteConnected";
    public static final String TYPE_REMOTE_DISCONNECTED = "remoteDisconnected";
    public static final String TYPE_SEEK_TO = "seekTo";
    public static final String TYPE_SESSION_ID = "c";
    public static final String TYPE_SET_PLAYLIST = "setPlaylist";
    public static final String TYPE_SET_VOLUME = "setVolume";
    public static final String TYPE_STOP_VIDEO = "stopVideo";
    public static final String TYPE_SUBTITLES = "setSubtitlesTrack";
    public static final String TYPE_UPDATE_PLAYLIST = "updatePlaylist";
    public static final String TYPE_VOICE = "voiceCommand";

    @JsonPath({"$[0]"})
    private int mIndex;

    @JsonPath({"$[1][1].key"})
    private String mKey;

    @JsonPath({"$[1][1:]"})
    private List<String> mParams;

    @JsonPath({"$[1][1]"})
    private PlaylistParams mPlaylistParams;

    @JsonPath({"$[1][1]"})
    private RemoteParams mRemoteParams;

    @JsonPath({"$[1][1]"})
    private SeekToParams mSeekToParams;

    @JsonPath({"$[1][0]"})
    private String mType;

    @JsonPath({"$[1][1]"})
    private VoiceParams mVoiceParams;

    @JsonPath({"$[1][1]"})
    private VolumeParams mVolumeParams;

    public int getIndex() {
        return this.mIndex;
    }

    public String getKey() {
        return this.mKey;
    }

    public List<String> getParams() {
        return this.mParams;
    }

    public PlaylistParams getPlaylistParams() {
        return this.mPlaylistParams;
    }

    public RemoteParams getRemoteParams() {
        return this.mRemoteParams;
    }

    public SeekToParams getSeekToParams() {
        return this.mSeekToParams;
    }

    public String getType() {
        return this.mType;
    }

    public VoiceParams getVoiceParams() {
        return this.mVoiceParams;
    }

    public VolumeParams getVolumeParams() {
        return this.mVolumeParams;
    }

    public String toString() {
        return h0.k("Type: ", getType());
    }
}
