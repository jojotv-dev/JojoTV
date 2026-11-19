package kf;

import com.nuvio.tv.domain.model.HomeLayout;

/* JADX INFO: loaded from: classes.dex */
public abstract /* synthetic */ class t4 {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public static final /* synthetic */ int[] f30696a;

    static {
        int[] iArr = new int[HomeLayout.values().length];
        try {
            iArr[HomeLayout.CLASSIC.ordinal()] = 1;
        } catch (NoSuchFieldError unused) {
        }
        try {
            iArr[HomeLayout.GRID.ordinal()] = 2;
        } catch (NoSuchFieldError unused2) {
        }
        try {
            iArr[HomeLayout.MODERN.ordinal()] = 3;
        } catch (NoSuchFieldError unused3) {
        }
        f30696a = iArr;
        int[] iArr2 = new int[com.nuvio.tv.data.local.n8.values().length];
        try {
            iArr2[0] = 1;
        } catch (NoSuchFieldError unused4) {
        }
        try {
            com.nuvio.tv.data.local.n8 n8Var = com.nuvio.tv.data.local.n8.f9086b;
            iArr2[1] = 2;
        } catch (NoSuchFieldError unused5) {
        }
    }
}
