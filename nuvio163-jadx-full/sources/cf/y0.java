package cf;

import com.nuvio.tv.domain.model.TmdbCollectionSourceType;

/* JADX INFO: loaded from: classes4.dex */
public abstract /* synthetic */ class y0 {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public static final /* synthetic */ int[] f7292a;

    static {
        int[] iArr = new int[TmdbCollectionSourceType.values().length];
        try {
            iArr[TmdbCollectionSourceType.COMPANY.ordinal()] = 1;
        } catch (NoSuchFieldError unused) {
        }
        try {
            iArr[TmdbCollectionSourceType.COLLECTION.ordinal()] = 2;
        } catch (NoSuchFieldError unused2) {
        }
        f7292a = iArr;
    }
}
