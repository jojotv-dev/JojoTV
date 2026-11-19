package androidx.media;

import c6.b;

/* JADX INFO: loaded from: classes3.dex */
public class AudioAttributesImplBaseParcelizer {
    public static AudioAttributesImplBase read(b bVar) {
        AudioAttributesImplBase audioAttributesImplBase = new AudioAttributesImplBase();
        audioAttributesImplBase.mUsage = bVar.f(audioAttributesImplBase.mUsage, 1);
        audioAttributesImplBase.mContentType = bVar.f(audioAttributesImplBase.mContentType, 2);
        audioAttributesImplBase.mFlags = bVar.f(audioAttributesImplBase.mFlags, 3);
        audioAttributesImplBase.mLegacyStream = bVar.f(audioAttributesImplBase.mLegacyStream, 4);
        return audioAttributesImplBase;
    }

    public static void write(AudioAttributesImplBase audioAttributesImplBase, b bVar) {
        bVar.getClass();
        bVar.j(audioAttributesImplBase.mUsage, 1);
        bVar.j(audioAttributesImplBase.mContentType, 2);
        bVar.j(audioAttributesImplBase.mFlags, 3);
        bVar.j(audioAttributesImplBase.mLegacyStream, 4);
    }
}
