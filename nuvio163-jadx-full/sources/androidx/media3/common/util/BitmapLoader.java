package androidx.media3.common.util;

import android.net.Uri;
import androidx.media3.common.MediaMetadata;
import com.google.common.util.concurrent.k0;

/* JADX INFO: loaded from: classes.dex */
@UnstableApi
public interface BitmapLoader {
    k0 decodeBitmap(byte[] bArr);

    k0 loadBitmap(Uri uri);

    default k0 loadBitmapFromMetadata(MediaMetadata mediaMetadata) {
        byte[] bArr = mediaMetadata.artworkData;
        if (bArr != null) {
            return decodeBitmap(bArr);
        }
        Uri uri = mediaMetadata.artworkUri;
        if (uri != null) {
            return loadBitmap(uri);
        }
        return null;
    }

    boolean supportsMimeType(String str);
}
