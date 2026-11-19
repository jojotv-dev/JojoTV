package u7;

import java.util.Arrays;

/* JADX INFO: loaded from: classes4.dex */
public abstract class c {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public static final byte[] f58289a;

    static {
        byte[] bArr = new byte[128];
        Arrays.fill(bArr, (byte) -1);
        for (int i10 = 0; i10 < 10; i10++) {
            bArr[i10 + 48] = (byte) i10;
        }
        for (int i11 = 0; i11 < 26; i11++) {
            byte b5 = (byte) (i11 + 10);
            bArr[i11 + 65] = b5;
            bArr[i11 + 97] = b5;
        }
        f58289a = bArr;
    }

    public static int a(char c5) {
        if (c5 < 128) {
            return f58289a[c5];
        }
        return -1;
    }
}
