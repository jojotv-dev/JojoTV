package androidx.media;

import c6.d;

/* JADX INFO: loaded from: classes3.dex */
public interface AudioAttributesImpl extends d {

    public interface Builder {
        AudioAttributesImpl build();

        Builder setContentType(int i10);

        Builder setFlags(int i10);

        Builder setLegacyStreamType(int i10);

        Builder setUsage(int i10);
    }

    Object getAudioAttributes();

    int getContentType();

    int getFlags();

    int getLegacyStreamType();

    int getRawLegacyStreamType();

    int getUsage();

    int getVolumeControlStream();
}
