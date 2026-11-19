package androidx.media3.decoder.mpegh;

import androidx.media3.common.MediaLibraryInfo;
import androidx.media3.common.util.LibraryLoader;
import androidx.media3.common.util.UnstableApi;

/* JADX INFO: loaded from: classes.dex */
@UnstableApi
public final class MpeghLibrary {
    private static final LibraryLoader LOADER;

    static {
        MediaLibraryInfo.registerModule("media3.decoder.mpegh");
        LOADER = new LibraryLoader("mpeghJNI") { // from class: androidx.media3.decoder.mpegh.MpeghLibrary.1
            @Override // androidx.media3.common.util.LibraryLoader
            public void loadLibrary(String str) {
                System.loadLibrary(str);
            }
        };
    }

    private MpeghLibrary() {
    }

    public static boolean isAvailable() {
        return LOADER.isAvailable();
    }

    public static void setLibraries(String... strArr) {
        LOADER.setLibraries(strArr);
    }
}
