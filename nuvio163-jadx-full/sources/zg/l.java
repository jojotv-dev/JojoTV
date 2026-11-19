package zg;

import java.util.Arrays;

/* JADX INFO: loaded from: classes5.dex */
public final class l {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public final byte f66759a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public final byte[] f66760b;

    public l(byte b5, byte[] bArr) {
        byte[] bArr2 = new byte[8191];
        this.f66760b = bArr2;
        this.f66759a = b5;
        System.arraycopy(bArr, 0, bArr2, 0, Math.min(bArr.length, 8191));
    }

    public final String toString() {
        return "PeerInfo{type=" + ((int) this.f66759a) + ", data=" + Arrays.toString(this.f66760b) + '}';
    }
}
