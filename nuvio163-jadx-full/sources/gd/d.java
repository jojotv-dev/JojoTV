package gd;

import com.nuvio.tv.domain.model.TmdbCollectionMediaType;

/* JADX INFO: loaded from: classes4.dex */
public abstract /* synthetic */ class d {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public static final /* synthetic */ int[] f19136a;

    static {
        int[] iArr = new int[TmdbCollectionMediaType.values().length];
        try {
            iArr[TmdbCollectionMediaType.MOVIE.ordinal()] = 1;
        } catch (NoSuchFieldError unused) {
        }
        try {
            iArr[TmdbCollectionMediaType.TV.ordinal()] = 2;
        } catch (NoSuchFieldError unused2) {
        }
        f19136a = iArr;
    }
}
