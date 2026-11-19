package kf;

import com.nuvio.tv.domain.model.PosterShape;

/* JADX INFO: loaded from: classes.dex */
public abstract /* synthetic */ class pb {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public static final /* synthetic */ int[] f30264a;

    static {
        int[] iArr = new int[PosterShape.values().length];
        try {
            iArr[PosterShape.LANDSCAPE.ordinal()] = 1;
        } catch (NoSuchFieldError unused) {
        }
        try {
            iArr[PosterShape.SQUARE.ordinal()] = 2;
        } catch (NoSuchFieldError unused2) {
        }
        try {
            iArr[PosterShape.POSTER.ordinal()] = 3;
        } catch (NoSuchFieldError unused3) {
        }
        f30264a = iArr;
    }
}
