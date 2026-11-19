package com.liskovsoft.mediaserviceinterfaces;

import eh.c;

/* JADX INFO: loaded from: classes4.dex */
public interface RemoteControlService {
    public static final int STATE_IDLE = 3;
    public static final int STATE_PAUSED = 2;
    public static final int STATE_PLAYING = 1;

    c getCommandObserve();

    String getPairingCode();

    c getPairingCodeObserve();

    c postStartPlayingObserve(String str, long j10, long j11, int i10);

    c postStateChangeObserve(long j10, long j11, int i10);

    c postSubtitleChangeObserve(String str, String str2);

    c postVolumeChangeObserve(int i10);

    boolean registerPairingCode(String str);

    c resetDataObserve();
}
