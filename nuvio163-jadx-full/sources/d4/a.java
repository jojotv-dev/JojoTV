package d4;

import android.graphics.Insets;
import android.graphics.drawable.Icon;
import android.net.Uri;
import android.os.ext.SdkExtensions;
import android.os.storage.StorageManager;
import android.view.DisplayCutout;
import android.view.View;
import android.view.Window;
import java.io.File;

/* JADX INFO: loaded from: classes3.dex */
public abstract class a {
    public static Icon a(Uri uri) {
        return Icon.createWithAdaptiveBitmapContentUri(uri);
    }

    public static void b(int i10) {
        SdkExtensions.getExtensionVersion(i10);
    }

    public static File c(StorageManager storageManager, Uri uri) {
        return storageManager.getStorageVolume(uri).getDirectory();
    }

    public static Insets d(DisplayCutout displayCutout) {
        return displayCutout.getWaterfallInsets();
    }

    public static void e(Window window, boolean z2) {
        View decorView = window.getDecorView();
        int systemUiVisibility = decorView.getSystemUiVisibility();
        decorView.setSystemUiVisibility(z2 ? systemUiVisibility & (-257) : systemUiVisibility | 256);
        window.setDecorFitsSystemWindows(z2);
    }

    public static void f(Window window, boolean z2) {
        window.setDecorFitsSystemWindows(z2);
    }
}
