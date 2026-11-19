package androidx.media3.session;

import android.net.Uri;
import androidx.media3.common.util.Assertions;
import androidx.media3.common.util.UnstableApi;
import java.util.Arrays;

/* JADX INFO: loaded from: classes.dex */
@UnstableApi
public final class CacheBitmapLoader implements androidx.media3.common.util.BitmapLoader {
    private final androidx.media3.common.util.BitmapLoader bitmapLoader;
    private BitmapLoadRequest lastBitmapLoadRequest;

    public static class BitmapLoadRequest {
        private final byte[] data;
        private final com.google.common.util.concurrent.k0 future;
        private final Uri uri;

        public BitmapLoadRequest(byte[] bArr, com.google.common.util.concurrent.k0 k0Var) {
            this.data = bArr;
            this.uri = null;
            this.future = k0Var;
        }

        public com.google.common.util.concurrent.k0 getFuture() {
            return (com.google.common.util.concurrent.k0) Assertions.checkStateNotNull(this.future);
        }

        public boolean matches(byte[] bArr) {
            byte[] bArr2 = this.data;
            return bArr2 != null && Arrays.equals(bArr2, bArr);
        }

        public boolean matches(Uri uri) {
            Uri uri2 = this.uri;
            return uri2 != null && uri2.equals(uri);
        }

        public BitmapLoadRequest(Uri uri, com.google.common.util.concurrent.k0 k0Var) {
            this.data = null;
            this.uri = uri;
            this.future = k0Var;
        }
    }

    public CacheBitmapLoader(androidx.media3.common.util.BitmapLoader bitmapLoader) {
        this.bitmapLoader = bitmapLoader;
    }

    @Override // androidx.media3.common.util.BitmapLoader
    public com.google.common.util.concurrent.k0 decodeBitmap(byte[] bArr) {
        BitmapLoadRequest bitmapLoadRequest = this.lastBitmapLoadRequest;
        if (bitmapLoadRequest != null && bitmapLoadRequest.matches(bArr)) {
            return this.lastBitmapLoadRequest.getFuture();
        }
        com.google.common.util.concurrent.k0 k0VarDecodeBitmap = this.bitmapLoader.decodeBitmap(bArr);
        this.lastBitmapLoadRequest = new BitmapLoadRequest(bArr, k0VarDecodeBitmap);
        return k0VarDecodeBitmap;
    }

    @Override // androidx.media3.common.util.BitmapLoader
    public com.google.common.util.concurrent.k0 loadBitmap(Uri uri) {
        BitmapLoadRequest bitmapLoadRequest = this.lastBitmapLoadRequest;
        if (bitmapLoadRequest != null && bitmapLoadRequest.matches(uri)) {
            return this.lastBitmapLoadRequest.getFuture();
        }
        com.google.common.util.concurrent.k0 k0VarLoadBitmap = this.bitmapLoader.loadBitmap(uri);
        this.lastBitmapLoadRequest = new BitmapLoadRequest(uri, k0VarLoadBitmap);
        return k0VarLoadBitmap;
    }

    @Override // androidx.media3.common.util.BitmapLoader
    public boolean supportsMimeType(String str) {
        return this.bitmapLoader.supportsMimeType(str);
    }
}
