package vf;

import com.nuvio.tv.domain.model.AppFont;

/* JADX INFO: loaded from: classes.dex */
public abstract /* synthetic */ class a1 {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public static final /* synthetic */ int[] f60777a;

    static {
        int[] iArr = new int[AppFont.values().length];
        try {
            iArr[AppFont.INTER.ordinal()] = 1;
        } catch (NoSuchFieldError unused) {
        }
        try {
            iArr[AppFont.DM_SANS.ordinal()] = 2;
        } catch (NoSuchFieldError unused2) {
        }
        try {
            iArr[AppFont.OPEN_SANS.ordinal()] = 3;
        } catch (NoSuchFieldError unused3) {
        }
        f60777a = iArr;
    }
}
