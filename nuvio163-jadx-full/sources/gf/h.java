package gf;

import com.nuvio.tv.domain.model.TmdbCollectionMediaType;
import com.nuvio.tv.domain.model.TmdbCollectionSourceType;

/* JADX INFO: loaded from: classes4.dex */
public abstract /* synthetic */ class h {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public static final /* synthetic */ int[] f19531a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public static final /* synthetic */ int[] f19532b;

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
        f19531a = iArr;
        int[] iArr2 = new int[TmdbCollectionSourceType.values().length];
        try {
            iArr2[TmdbCollectionSourceType.LIST.ordinal()] = 1;
        } catch (NoSuchFieldError unused3) {
        }
        try {
            iArr2[TmdbCollectionSourceType.COLLECTION.ordinal()] = 2;
        } catch (NoSuchFieldError unused4) {
        }
        try {
            iArr2[TmdbCollectionSourceType.COMPANY.ordinal()] = 3;
        } catch (NoSuchFieldError unused5) {
        }
        try {
            iArr2[TmdbCollectionSourceType.NETWORK.ordinal()] = 4;
        } catch (NoSuchFieldError unused6) {
        }
        try {
            iArr2[TmdbCollectionSourceType.PERSON.ordinal()] = 5;
        } catch (NoSuchFieldError unused7) {
        }
        try {
            iArr2[TmdbCollectionSourceType.DIRECTOR.ordinal()] = 6;
        } catch (NoSuchFieldError unused8) {
        }
        try {
            iArr2[TmdbCollectionSourceType.DISCOVER.ordinal()] = 7;
        } catch (NoSuchFieldError unused9) {
        }
        f19532b = iArr2;
    }
}
