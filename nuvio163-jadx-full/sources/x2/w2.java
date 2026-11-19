package x2;

import kf.ge;

/* JADX INFO: loaded from: classes.dex */
public interface w2 {
    long a();

    long b();

    default float c() {
        return 2.0f;
    }

    default long d() {
        float f10 = 48;
        return ge.G(f10, f10);
    }

    default float e() {
        return Float.MAX_VALUE;
    }

    float f();

    default float g() {
        return 16.0f;
    }
}
