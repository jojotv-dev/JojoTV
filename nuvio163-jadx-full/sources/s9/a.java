package s9;

import java.util.Arrays;
import l9.d;
import l9.e;

/* JADX INFO: loaded from: classes4.dex */
public class a extends d {

    /* JADX INFO: renamed from: f, reason: collision with root package name */
    public static final byte[] f54152f = {0, 0};

    /* JADX INFO: renamed from: g, reason: collision with root package name */
    public static final byte[] f54153g = {0, 0, 0, 0};

    public a() {
        super(e.f32188c);
    }

    public final void v(int i10) {
        byte[] bArr = new byte[i10];
        Arrays.fill(bArr, (byte) 0);
        i(i10, bArr);
    }

    public final void w() {
        i(2, f54152f);
    }

    public final void x() {
        i(4, f54153g);
    }

    public a(byte[] bArr) {
        super(bArr, true, e.f32188c);
    }
}
