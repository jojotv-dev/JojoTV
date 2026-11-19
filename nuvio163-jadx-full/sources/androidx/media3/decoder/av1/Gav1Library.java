package androidx.media3.decoder.av1;

import androidx.media3.common.MediaLibraryInfo;
import androidx.media3.common.util.LibraryLoader;
import androidx.media3.common.util.UnstableApi;

/* JADX INFO: loaded from: classes3.dex */
@UnstableApi
public final class Gav1Library {
    private static final LibraryLoader LOADER;

    static {
        MediaLibraryInfo.registerModule("media3.decoder.av1");
        LOADER = new LibraryLoader("gav1JNI") { // from class: androidx.media3.decoder.av1.Gav1Library.1
            @Override // androidx.media3.common.util.LibraryLoader
            public void loadLibrary(String str) {
                System.loadLibrary(str);
            }
        };
    }

    private Gav1Library() {
    }

    public static boolean isAvailable() {
        return LOADER.isAvailable();
    }
}
