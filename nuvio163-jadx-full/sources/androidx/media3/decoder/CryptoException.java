package androidx.media3.decoder;

import androidx.media3.common.util.UnstableApi;

/* JADX INFO: loaded from: classes3.dex */
@UnstableApi
public class CryptoException extends Exception {
    public final int errorCode;

    public CryptoException(int i10, String str) {
        super(str);
        this.errorCode = i10;
    }
}
