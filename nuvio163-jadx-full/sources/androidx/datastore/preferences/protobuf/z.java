package androidx.datastore.preferences.protobuf;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;

/* JADX INFO: loaded from: classes.dex */
public abstract class z {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public static final Charset f2589a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public static final byte[] f2590b;

    static {
        Charset.forName("US-ASCII");
        f2589a = Charset.forName("UTF-8");
        Charset.forName("ISO-8859-1");
        byte[] bArr = new byte[0];
        f2590b = bArr;
        ByteBuffer.wrap(bArr);
        try {
            new i(bArr, 0, 0, false).e(0);
        } catch (b0 e10) {
            throw new IllegalArgumentException(e10);
        }
    }

    public static void a(Object obj, String str) {
        if (obj == null) {
            throw new NullPointerException(str);
        }
    }

    public static int b(long j10) {
        return (int) (j10 ^ (j10 >>> 32));
    }
}
