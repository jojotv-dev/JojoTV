package dg;

import com.uwetrottmann.tmdb2.enumerations.MediaType;

/* JADX INFO: loaded from: classes4.dex */
public abstract /* synthetic */ class d {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public static final /* synthetic */ int[] f13328a;

    static {
        int[] iArr = new int[MediaType.values().length];
        f13328a = iArr;
        try {
            iArr[MediaType.MOVIE.ordinal()] = 1;
        } catch (NoSuchFieldError unused) {
        }
        try {
            f13328a[MediaType.TV.ordinal()] = 2;
        } catch (NoSuchFieldError unused2) {
        }
        try {
            f13328a[MediaType.PERSON.ordinal()] = 3;
        } catch (NoSuchFieldError unused3) {
        }
    }
}
