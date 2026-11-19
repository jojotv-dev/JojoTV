package ue;

import com.nuvio.tv.domain.model.PosterShape;

/* JADX INFO: loaded from: classes4.dex */
public abstract /* synthetic */ class m2 {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public static final /* synthetic */ int[] f59023a;

    static {
        int[] iArr = new int[PosterShape.values().length];
        try {
            iArr[PosterShape.POSTER.ordinal()] = 1;
        } catch (NoSuchFieldError unused) {
        }
        try {
            iArr[PosterShape.LANDSCAPE.ordinal()] = 2;
        } catch (NoSuchFieldError unused2) {
        }
        try {
            iArr[PosterShape.SQUARE.ordinal()] = 3;
        } catch (NoSuchFieldError unused3) {
        }
        f59023a = iArr;
    }
}
