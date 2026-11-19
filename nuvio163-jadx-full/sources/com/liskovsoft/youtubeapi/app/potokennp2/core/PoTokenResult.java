package com.liskovsoft.youtubeapi.app.potokennp2.core;

import java.util.Objects;

/* JADX INFO: loaded from: classes4.dex */
public final class PoTokenResult {
    public final String playerRequestPoToken;
    public final String streamingDataPoToken;
    public final String videoId;
    public final String visitorData;

    public PoTokenResult(String str, String str2, String str3, String str4) {
        Objects.requireNonNull(str);
        this.videoId = str;
        Objects.requireNonNull(str2);
        this.visitorData = str2;
        Objects.requireNonNull(str3);
        this.playerRequestPoToken = str3;
        this.streamingDataPoToken = str4;
    }
}
