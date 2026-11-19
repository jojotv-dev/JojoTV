package androidx.media3.decoder.iamf;

import androidx.media3.common.MediaLibraryInfo;
import androidx.media3.common.util.LibraryLoader;
import androidx.media3.common.util.UnstableApi;

/* JADX INFO: loaded from: classes.dex */
@UnstableApi
public final class IamfLibrary {
    private static final LibraryLoader LOADER;

    static {
        MediaLibraryInfo.registerModule("media3.decoder.iamf");
        LOADER = new LibraryLoader("iamfJNI") { // from class: androidx.media3.decoder.iamf.IamfLibrary.1
            @Override // androidx.media3.common.util.LibraryLoader
            public void loadLibrary(String str) {
                System.loadLibrary(str);
            }
        };
    }

    private IamfLibrary() {
    }

    public static boolean isAvailable() {
        return LOADER.isAvailable();
    }

    public static void setLibraries(String... strArr) {
        LOADER.setLibraries(strArr);
    }
}
