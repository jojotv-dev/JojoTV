package ed;

import com.nuvio.tv.domain.model.TmdbCollectionMediaType;
import com.nuvio.tv.domain.model.TmdbCollectionSourceType;

/* JADX INFO: loaded from: classes4.dex */
public abstract /* synthetic */ class f {

    /* JADX INFO: renamed from: a, reason: collision with root package name */
    public static final /* synthetic */ int[] f15083a;

    /* JADX INFO: renamed from: b, reason: collision with root package name */
    public static final /* synthetic */ int[] f15084b;

    static {
        int[] iArr = new int[TmdbCollectionSourceType.values().length];
        try {
            iArr[TmdbCollectionSourceType.DIRECTOR.ordinal()] = 1;
        } catch (NoSuchFieldError unused) {
        }
        try {
            iArr[TmdbCollectionSourceType.COMPANY.ordinal()] = 2;
        } catch (NoSuchFieldError unused2) {
        }
        try {
            iArr[TmdbCollectionSourceType.NETWORK.ordinal()] = 3;
        } catch (NoSuchFieldError unused3) {
        }
        f15083a = iArr;
        int[] iArr2 = new int[TmdbCollectionMediaType.values().length];
        try {
            iArr2[TmdbCollectionMediaType.MOVIE.ordinal()] = 1;
        } catch (NoSuchFieldError unused4) {
        }
        try {
            iArr2[TmdbCollectionMediaType.TV.ordinal()] = 2;
        } catch (NoSuchFieldError unused5) {
        }
        f15084b = iArr2;
    }
}
