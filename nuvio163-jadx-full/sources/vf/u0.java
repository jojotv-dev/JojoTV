package vf;

import com.nuvio.tv.domain.model.AppTheme;

/* JADX INFO: loaded from: classes4.dex */
public abstract /* synthetic */ class u0 {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public static final /* synthetic */ int[] f61002a;

    static {
        int[] iArr = new int[AppTheme.values().length];
        try {
            iArr[AppTheme.GOLD.ordinal()] = 1;
        } catch (NoSuchFieldError unused) {
        }
        try {
            iArr[AppTheme.JADE.ordinal()] = 2;
        } catch (NoSuchFieldError unused2) {
        }
        try {
            iArr[AppTheme.ROSE_GOLD.ordinal()] = 3;
        } catch (NoSuchFieldError unused3) {
        }
        try {
            iArr[AppTheme.ARCTIC_BLUE.ordinal()] = 4;
        } catch (NoSuchFieldError unused4) {
        }
        try {
            iArr[AppTheme.GRAPHITE.ordinal()] = 5;
        } catch (NoSuchFieldError unused5) {
        }
        f61002a = iArr;
    }
}
