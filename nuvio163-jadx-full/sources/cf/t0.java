package cf;

import com.nuvio.tv.domain.model.TmdbCollectionSourceType;

/* JADX INFO: loaded from: classes4.dex */
public abstract /* synthetic */ class t0 {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public static final /* synthetic */ int[] f7220a;

    static {
        int[] iArr = new int[TmdbCollectionSourceType.values().length];
        try {
            iArr[TmdbCollectionSourceType.LIST.ordinal()] = 1;
        } catch (NoSuchFieldError unused) {
        }
        try {
            iArr[TmdbCollectionSourceType.COLLECTION.ordinal()] = 2;
        } catch (NoSuchFieldError unused2) {
        }
        try {
            iArr[TmdbCollectionSourceType.COMPANY.ordinal()] = 3;
        } catch (NoSuchFieldError unused3) {
        }
        try {
            iArr[TmdbCollectionSourceType.NETWORK.ordinal()] = 4;
        } catch (NoSuchFieldError unused4) {
        }
        try {
            iArr[TmdbCollectionSourceType.PERSON.ordinal()] = 5;
        } catch (NoSuchFieldError unused5) {
        }
        try {
            iArr[TmdbCollectionSourceType.DIRECTOR.ordinal()] = 6;
        } catch (NoSuchFieldError unused6) {
        }
        try {
            iArr[TmdbCollectionSourceType.DISCOVER.ordinal()] = 7;
        } catch (NoSuchFieldError unused7) {
        }
        f7220a = iArr;
    }
}
