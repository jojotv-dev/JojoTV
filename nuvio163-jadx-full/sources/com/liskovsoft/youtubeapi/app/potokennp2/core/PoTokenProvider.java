package com.liskovsoft.youtubeapi.app.potokennp2.core;

/* JADX INFO: loaded from: classes4.dex */
public interface PoTokenProvider {
    PoTokenResult getAndroidClientPoToken(String str);

    PoTokenResult getIosClientPoToken(String str);

    PoTokenResult getWebClientPoToken(String str);

    PoTokenResult getWebEmbedClientPoToken(String str);

    boolean isWebPotExpired();

    boolean isWebPotSupported();
}
